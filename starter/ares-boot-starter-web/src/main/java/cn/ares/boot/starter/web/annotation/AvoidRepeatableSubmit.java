package cn.ares.boot.starter.web.annotation;

import static cn.ares.boot.util.common.constant.ScriptLang.GROOVY;

import cn.ares.boot.util.common.annotation.TrafficLimit;
import cn.ares.boot.util.common.constant.ScriptLang;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.springframework.core.annotation.AliasFor;

/**
 * @author: Ares
 * @time: 2024-01-16 16:28:25
 * @description: 防止重复的http提交请求
 * @description: Avoid repeatable http submit request
 * @version: JDK 1.8
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AvoidRepeatableSubmit {

  /**
   * 限流周期
   * period for traffic limit
   */
  @AliasFor(
      annotation = TrafficLimit.class
  )
  long period() default 3_000;

  /**
   * 限流周期单位
   */
  @AliasFor(
      annotation = TrafficLimit.class
  )
  TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

  /**
   * 用于限流的消息头
   */
  @AliasFor(
      annotation = TrafficLimit.class
  )
  String[] headers() default {};

  /**
   * 限流超时时间
   * timeout when acquire permit
   */
  @AliasFor(
      annotation = TrafficLimit.class
  )
  long timeout() default -1;

  /**
   * 如果表达式非空则仅当入参满足表达式才进行限流
   * If expression is not empty, the current limit will only be performed if the input parameter satisfies the expression
   */
  @AliasFor(
      annotation = TrafficLimit.class
  )
  String validExpression() default "";

  /**
   * 限流键的表达式（不存在默认使用整个请求入参作为键）
   * Expression of traffic limit key (if not exist, the entire request parameter is used as the key by default)
   */
  @AliasFor(
      annotation = TrafficLimit.class
  )
  String keyExpression() default "";

  /**
   * 例如js、groovy
   * Such as js, groovy
   */
  @AliasFor(
      annotation = TrafficLimit.class
  )
  ScriptLang expressionLang() default GROOVY;

}