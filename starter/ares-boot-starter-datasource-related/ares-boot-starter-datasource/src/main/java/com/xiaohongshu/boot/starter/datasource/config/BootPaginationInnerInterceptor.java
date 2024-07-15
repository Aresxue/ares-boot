package com.xiaohongshu.boot.starter.datasource.config;

import cn.ares.boot.util.common.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @author: Ares
 * @time: 2022-03-04 14:07:32
 * @description: 分页优化版本 1-支持limit下推 2-limit_table支持
 * @description: Paging optimized version 1-support limit push down 2-limit_table support
 * @version: JDK 1.8
 */
public class BootPaginationInnerInterceptor extends PaginationInnerInterceptor {

  /*
  sql example:
  select * from table as t1
  join
  (select id from table as t2 where t2.a = 1 and t2.b <= 4 and t2.b != 2 and t2.c = 3 order by t2.d desc limit 10000, 10) as limit_table
  on t1.id = limit_table.id
   */
  private static final String LIMIT_TABLE = "limit_table";

  @Override
  public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
      RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
    IPage<?> page = ParameterUtils.findPage(parameter).orElse(null);
    if (null == page) {
      return;
    }

    // 处理 orderBy 拼接
    boolean addOrdered = false;
    String buildSql = boundSql.getSql();
    List<OrderItem> orders = page.orders();
    if (CollectionUtils.isNotEmpty(orders)) {
      addOrdered = true;
      buildSql = this.concatOrderBy(buildSql, orders);
    }

    // size 小于 0 且不限制返回值则不构造分页sql
    Long _limit = page.maxLimit() != null ? page.maxLimit() : maxLimit;
    if (page.getSize() < 0 && null == _limit) {
      if (addOrdered) {
        PluginUtils.mpBoundSql(boundSql).sql(buildSql);
      }
      return;
    }

    handlerLimit(page, _limit);
    IDialect dialect = findIDialect(executor);

    final Configuration configuration = ms.getConfiguration();
    // 计算实际PageSize的值
    // Calculate the actual PageSize value
    long rows = page.getSize();
    if (page.searchCount()) {
      long leftCount = page.getTotal() - (page.getCurrent() - 1) * rows;
      // 正常来说leftCount总是>=0, 这里避免异常情况发现小于0时直接设置为0
      if (leftCount < 0) {
        rows = 0;
      } else {
        rows = Math.min(leftCount, rows);
      }
    }

    DialectModel model = dialect.buildPaginationSql(buildSql, page.offset(), rows);
    PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);

    List<ParameterMapping> mappings = mpBoundSql.parameterMappings();
    Map<String, Object> additionalParameter = mpBoundSql.additionalParameters();
    model.consumers(mappings, configuration, additionalParameter);
    mpBoundSql.sql(model.getDialectSql());
    mpBoundSql.parameterMappings(mappings);
  }


  @Override
  public String autoCountSql(IPage<?> page, String sql) {
    if (!page.optimizeCountSql()) {
      return lowLevelCountSql(sql);
    }
    try {
      Select select = (Select) JsqlParserGlobal.parse(sql);
      // https://github.com/baomidou/mybatis-plus/issues/3920  分页增加union语法支持
      if (select instanceof SetOperationList) {
        return lowLevelCountSql(sql);
      }
      PlainSelect plainSelect = (PlainSelect) select;

      // 优化 order by 在非分组情况下
      List<OrderByElement> orderBy = plainSelect.getOrderByElements();
      if (CollectionUtils.isNotEmpty(orderBy)) {
        boolean canClean = true;
        for (OrderByElement order : orderBy) {
          // order by 里带参数,不去除order by
          Expression expression = order.getExpression();
          if (!(expression instanceof Column) && expression.toString()
              .contains(StringPool.QUESTION_MARK)) {
            canClean = false;
            break;
          }
        }
        if (canClean) {
          plainSelect.setOrderByElements(null);
        }
      }
      Distinct distinct = plainSelect.getDistinct();
      GroupByElement groupBy = plainSelect.getGroupBy();
      // 包含 distinct、groupBy 不优化
      if (null != distinct || null != groupBy) {
        return lowLevelCountSql(select.toString());
      }
      //#95 Github, selectItems contains #{} ${}, which will be translated to ?, and it may be in a function: power(#{myInt},2)
      for (SelectItem<?> item : plainSelect.getSelectItems()) {
        if (item.toString().contains(StringPool.QUESTION_MARK)) {
          return lowLevelCountSql(select.toString());
        }
      }

      // 包含 join 连表,进行判断是否移除 join 连表
      boolean isNeedOptimizeSql = false;
      if (optimizeJoin && page.optimizeJoinOfCountSql()) {
        List<Join> joinList = plainSelect.getJoins();
        if (CollectionUtils.isNotEmpty(joinList)) {
          isNeedOptimizeSql = sql.contains(LIMIT_TABLE) && isTheSameTable(select);
          // mysql中默认join就是inner join
          boolean isInnerJoin =
              joinList.size() == 1 && (joinList.get(0).isInner() || (!joinList.get(0).isLeft()
                  && !joinList.get(0).isRight()));
          isNeedOptimizeSql = isNeedOptimizeSql && isInnerJoin;

          // 不需要优化这里执行原有逻辑
          if (!isNeedOptimizeSql) {
            boolean canRemoveJoin = true;
            String whereS = Optional.ofNullable(plainSelect.getWhere()).map(Expression::toString)
                .orElse(StringPool.EMPTY);
            // 不区分大小写
            whereS = whereS.toLowerCase();

            for (Join join : joinList) {
              if (!join.isLeft()) {
                canRemoveJoin = false;
                break;
              }
              FromItem rightItem = join.getRightItem();
              String str = "";
              if (rightItem instanceof Table) {
                Table table = (Table) rightItem;
                str = Optional.ofNullable(table.getAlias()).map(Alias::getName)
                    .orElse(table.getName()) + StringPool.DOT;
              } else if (rightItem instanceof ParenthesedSelect) {
                ParenthesedSelect subSelect = (ParenthesedSelect) rightItem;
                /* 如果 left join 是子查询，并且子查询里包含 ?(代表有入参) 或者 where 条件里包含使用 join 的表的字段作条件,就不移除 join */
                if (subSelect.toString().contains(StringPool.QUESTION_MARK)) {
                  canRemoveJoin = false;
                  break;
                }
                str = subSelect.getAlias().getName() + StringPool.DOT;
              }
              // 不区分大小写
              str = str.toLowerCase();
              if (whereS.contains(str)) {
                /* 如果 where 条件里包含使用 join 的表的字段作条件,就不移除 join */
                canRemoveJoin = false;
                break;
              }
              for (Expression expression : join.getOnExpressions()) {
                if (expression.toString().contains(StringPool.QUESTION_MARK)) {
                  /* 如果 join 里包含 ?(代表有入参) 就不移除 join */
                  canRemoveJoin = false;
                  break;
                }
              }
            }
            if (canRemoveJoin) {
              plainSelect.setJoins(null);
            }
          }
        }
      }

      if (isNeedOptimizeSql) {
        FromItem rightItem = plainSelect.getJoins().get(0).getRightItem();
        if (rightItem instanceof ParenthesedSelect) {
          ParenthesedSelect parenthesedSelect = (ParenthesedSelect) rightItem;
          PlainSelect subPlainSelect = parenthesedSelect.getPlainSelect();
          subPlainSelect.setSelectItems(COUNT_SELECT_ITEM);
          return subPlainSelect.toString();
        }
      } else {
        // 优化 SQL
        plainSelect.setSelectItems(COUNT_SELECT_ITEM);
      }

      return select.toString();
    } catch (JSQLParserException e) {
      // 无法优化使用原 SQL
      logger.warn(
          "optimize this sql to a count sql has exception, sql:\"" + sql + "\", exception:\n"
              + e.getCause());
    } catch (Exception e) {
      logger.warn(
          "optimize this sql to a count sql has error, sql:\"" + sql + "\", exception:\n" + e);
    }
    return lowLevelCountSql(sql);
  }


  private boolean isTheSameTable(Statement statement) {
    // 解析sql获取表名
    TablesNamesFinder finder = new TablesNamesFinder();
    List<String> tableList = new ArrayList<>(finder.getTables(statement));

    // 2张表名完全相同
    if (CollectionUtil.isNotEmpty(tableList) && tableList.size() == 1) {
      return true;
    }

    // 2张表 不完全项目 类似：nurse_station_db.bed_info 与 bed_info 写法不一样
    if (CollectionUtil.isEmpty(tableList) || tableList.size() != 2) {
      return false;
    }

    String table = tableList.get(0);
    String otherTable = tableList.get(1);
    return table.contains(otherTable) || otherTable.contains(table);
  }

}
