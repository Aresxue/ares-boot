package cn.ares.boot.starter.datasource.jdbc.interceptor;

/**
 * @author: Ares
 * @time: 2022-09-08 18:26:24
 * @description: Print final execute sql
 * @description: 打印最终的执行sql
 * @version: JDK 1.8
 */
public class SqlPrintInterceptor extends AbstractQueryInterceptor {

  @Override
  public void handle(String sql) {
    sql = pretty(sql);
    SQL_LOGGER.info("execute sql: {}", sql);
  }

  protected static String pretty(String sql) {
//    if (isSqlPrintPretty()) {
//      sql = SQLUtils.formatMySql(sql);
//    }
//    // 非换行时替换换行符
//    if (!isSqlPrintNewline()) {
//      sql = StringUtil.replace(sql, LINE_BREAK, SPACE);
//    }
    return sql;
  }

}
