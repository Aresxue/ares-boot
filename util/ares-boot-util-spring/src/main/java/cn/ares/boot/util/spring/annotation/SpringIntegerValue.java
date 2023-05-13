package cn.ares.boot.util.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Ares
 * @time: 2022-07-27 15:40:32
 * @description: 支持注解中的int属性无法使用spring的配置动态赋值
 * @description: Supporting int properties in annotations cannot be dynamically assigned using spring's configuration
 * @version: JDK 1.8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpringIntegerValue {

  int value() default -1;

  String valueExpression() default "";

}
