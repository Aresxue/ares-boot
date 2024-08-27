package cn.ares.boot.base.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.logging.LogLevel;

/**
 * @author: Ares
 * @time: 2021-11-11 15:19:00
 * @description: 打印方法出入注解
 * @version: JDK 1.8
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LogPrint {

  /**
   * 日志打印级别
   * Log print level
   */
  LogLevel logLevel() default LogLevel.INFO;

  /**
   * 是否打印请求
   * print params or not
   */
  boolean printParams() default true;

  /**
   * 是否打印结果
   * print result or not
   */
  boolean printResult() default true;

  /**
   * 入参中额外的信息
   * Info of extra parameters in the request
   */
  String requestOtherMessage() default "";

  /**
   * 响应中额外的信息
   * Info of extra parameters in the response
   */
  String responseOtherMessage() default "";

}
