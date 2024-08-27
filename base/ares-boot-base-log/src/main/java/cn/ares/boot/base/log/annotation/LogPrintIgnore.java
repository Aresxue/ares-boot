package cn.ares.boot.base.log.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Ares
 * @time: 2024-08-26 19:26:52
 * @description: 忽略日志打印注解
 * @description: Ignore log print annotation
 * @see com.fasterxml.jackson.annotation.JsonIgnore
 * @version: JDK 1.8
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface LogPrintIgnore {

  boolean value() default true;

}
