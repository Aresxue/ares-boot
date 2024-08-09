package cn.ares.boot.base.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Ares
 * @time: 2021-11-11 15:19:00
 * @description: 打印方法出入注解
 * @version: JDK 1.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LogPrint {

  /**
   * 是否打印请求
   * print request or not
   */
  boolean printRequest() default true;

  /**
   * 是否打印响应
   * print response or not
   */
  boolean printResponse() default true;

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
