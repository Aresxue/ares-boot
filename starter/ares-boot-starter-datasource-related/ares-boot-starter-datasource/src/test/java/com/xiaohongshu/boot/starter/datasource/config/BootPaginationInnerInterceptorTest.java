package com.xiaohongshu.boot.starter.datasource.config;

import cn.ares.boot.base.log.util.LoggerUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;

/**
 * @author: Ares
 * @time: 2024-07-02 17:16:45
 * @description: Boot pagination inner interceptor test
 * @version: JDK 1.8
 */
public class BootPaginationInnerInterceptorTest {

  private final BootPaginationInnerInterceptor interceptor = new BootPaginationInnerInterceptor();

  @Test
  public void testAutoCountSql() {
    String sql = "select * from table1 join (select * from table2) as t2 on table1.id = t2.id";
    String countSql = interceptor.autoCountSql(Page.of(1, 10), sql);
    LoggerUtil.info("auto count sql: {}", countSql);

    sql = "select * from table as t1 join (select id from table as t2 where t2.a = 1 and t2.b <= 4 and t2.b != 2 and t2.c = 3 order by t2.d desc limit 10000, 10) as limit_table on t1.id = limit_table.id";
    countSql = interceptor.autoCountSql(Page.of(1, 10), sql);
    LoggerUtil.info("auto count sql: {}", countSql);
  }


}
