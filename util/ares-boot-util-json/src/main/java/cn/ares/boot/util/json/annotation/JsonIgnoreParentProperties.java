package cn.ares.boot.util.json.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Ares
 * @time: 2022-06-16 11:20:08
 * @description: 忽略父类字段，只序列化自己的字段
 * @description: Ignore parent class fields and only serialize own fields
 * @version: JDK 1.8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotation
public @interface JsonIgnoreParentProperties {

  /**
   * Fields not ignored
   */
  String[] exclude() default {};

}
