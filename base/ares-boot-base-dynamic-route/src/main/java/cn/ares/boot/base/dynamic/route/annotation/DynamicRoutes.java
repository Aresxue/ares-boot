package cn.ares.boot.base.dynamic.route.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Ares
 * @time: 2024-09-06 11:30:20
 * @description: 动态路由注解聚合
 * @description: Dynamic route annotation aggregation
 * @version: JDK 1.8
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DynamicRoutes {

  DynamicRoute[] value() default {};

}
