package cn.ares.boot.util.spring.entity;

/**
 * @author: Ares
 * @time: 2022-11-20 00:19:43
 * @description: Reflection entity
 * @version: JDK 1.8
 */
public class ReflectionEntity {

  public static String sayStatic(String word) {
    return word;
  }
  public static String sayStaticHello() {
    return "static hello";
  }

  public String sayHello() {
    return "hello";
  }

  public String say(String word) {
    return word;
  }

}
