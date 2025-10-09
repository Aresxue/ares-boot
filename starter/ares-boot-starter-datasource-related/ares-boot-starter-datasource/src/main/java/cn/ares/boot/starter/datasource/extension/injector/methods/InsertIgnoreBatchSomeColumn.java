package cn.ares.boot.starter.datasource.extension.injector.methods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import java.util.List;
import java.util.function.Predicate;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author: Ares
 * @time: 2024-12-02 16:59:39
 * @description: 批量新增数据（忽略报错）
 * @description: Batch insert data (ignore error)
 * @version: JDK 1.8
 * @see com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn
 */
public class InsertIgnoreBatchSomeColumn extends AbstractMethod {

  private static final long serialVersionUID = 8990291141150311918L;

  /**
   * 字段筛选条件
   */
  private Predicate<TableFieldInfo> predicate;

  /**
   * 默认方法名
   */
  public InsertIgnoreBatchSomeColumn() {
    super("insertIgnoreBatchSomeColumn");
  }

  /**
   * 默认方法名
   *
   * @param predicate 字段筛选条件
   */
  public InsertIgnoreBatchSomeColumn(Predicate<TableFieldInfo> predicate) {
    super("insertIgnoreBatchSomeColumn");
    this.predicate = predicate;
  }

  /**
   * @param name      方法名
   * @param predicate 字段筛选条件
   */
  public InsertIgnoreBatchSomeColumn(String name, Predicate<TableFieldInfo> predicate) {
    super(name);
    this.predicate = predicate;
  }

  @SuppressWarnings("Duplicates")
  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
    KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
    List<TableFieldInfo> fieldList = tableInfo.getFieldList();
    String insertSqlColumn =
        tableInfo.getKeyInsertSqlColumn(true, null, false) + this.filterTableFieldInfo(fieldList, predicate, TableFieldInfo::getInsertSqlColumn, EMPTY);
    String columnScript =
        LEFT_BRACKET + insertSqlColumn.substring(0, insertSqlColumn.length() - 1) + RIGHT_BRACKET;
    String insertSqlProperty =
        tableInfo.getKeyInsertSqlProperty(true, ENTITY_DOT, false) + this.filterTableFieldInfo(
            fieldList, predicate, i -> i.getInsertSqlProperty(ENTITY_DOT), EMPTY);
    insertSqlProperty = LEFT_BRACKET + insertSqlProperty.substring(0, insertSqlProperty.length() - 1) + RIGHT_BRACKET;
    String valuesScript = SqlScriptUtils.convertForeach(insertSqlProperty, "list", null, ENTITY, COMMA);
    String keyProperty = null;
    String keyColumn = null;
    // 表包含主键处理逻辑,如果不包含主键当普通字段处理
    if (tableInfo.havePK()) {
      if (tableInfo.getIdType() == IdType.AUTO) {
        /* 自增主键 */
        keyGenerator = Jdbc3KeyGenerator.INSTANCE;
        keyProperty = tableInfo.getKeyProperty();
        // 去除转义符
        keyColumn = SqlInjectionUtils.removeEscapeCharacter(tableInfo.getKeyColumn());
      } else {
        if (null != tableInfo.getKeySequence()) {
          keyGenerator = TableInfoHelper.genKeyGenerator(this.methodName, tableInfo, builderAssistant);
          keyProperty = tableInfo.getKeyProperty();
          keyColumn = tableInfo.getKeyColumn();
        }
      }
    }
    String sql = "<script>\nINSERT IGNORE INTO " + tableInfo.getTableName() + " " + columnScript + " VALUES " + valuesScript + "\n</script>";
    SqlSource sqlSource = super.createSqlSource(configuration, sql, modelClass);
    return this.addInsertMappedStatement(mapperClass, modelClass, methodName, sqlSource, keyGenerator, keyProperty, keyColumn);
  }

}
