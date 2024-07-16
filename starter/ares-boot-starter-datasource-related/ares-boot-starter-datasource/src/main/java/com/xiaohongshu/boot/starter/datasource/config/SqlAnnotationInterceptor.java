package com.xiaohongshu.boot.starter.datasource.config;

import cn.ares.boot.base.config.BootEnvironment;
import cn.ares.boot.base.log.util.LoggerUtil;
import cn.ares.boot.util.spring.ReflectionUtil;
import java.sql.Connection;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2024-07-02 17:36:29
 * @description: 用于为sql语句添加注释，标记语句的执行应用
 * @description: Used to add comments to SQL statements and mark the execution application of
 * statements
 * @version: JDK 1.8
 */
@Intercepts(
    {
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,
            Integer.class}),
    }
)
public class SqlAnnotationInterceptor implements Interceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqlAnnotationInterceptor.class);

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object target = invocation.getTarget();
    StatementHandler statementHandler = (StatementHandler) target;
    try {
      BoundSql boundSql = statementHandler.getBoundSql();
      String sql = boundSql.getSql();
      String annotation = String.format("/* appName=%s */", BootEnvironment.getAppName());
      // 在sql语句前面加上注释然后用反射修改boundSql的sql属性实现修改sql的目的
      // Add a comment in front of the SQL statement and then use reflection to modify the sql properties of boundSql
      sql = annotation + sql;
      ReflectionUtil.setFieldValue(boundSql, "sql", sql);
    } catch (Throwable throwable) {
      LOGGER.error("add sql annotation fail: ", throwable);
    }
    return invocation.proceed();
  }

}
