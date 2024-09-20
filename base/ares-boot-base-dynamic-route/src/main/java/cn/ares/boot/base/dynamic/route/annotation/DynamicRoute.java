package cn.ares.boot.base.dynamic.route.annotation;

import static cn.ares.boot.base.dynamic.route.constant.DynamicRouteConstant.MASTER;

import cn.ares.boot.base.dynamic.route.constant.DynamicRouteType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Ares
 * @time: 2023-05-31 16:53:00
 * @description: 用于路由到不同实例，比如数据库、缓存、消息队列等等
 * @description: Used to route to different instances, such as cache, datasource, mq, and so on
 * @version: JDK 1.8
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DynamicRoutes.class)
@Documented
@Inherited
public @interface DynamicRoute {

  /**
   * 路由值
   */
  String value() default MASTER;

  /**
   * 路由类型
   */
  DynamicRouteType type();

}
