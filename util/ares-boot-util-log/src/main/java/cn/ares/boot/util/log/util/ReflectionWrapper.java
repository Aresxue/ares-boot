package cn.ares.boot.util.log.util;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2023-10-30 19:51:33
 * @description: Reflection util
 * @description: 只支持jdk8不考虑jdk7
 * @version: JDK 1.8
 */
public class ReflectionWrapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionWrapper.class);

  private static Method GET_CALLER_CLASS_METHOD;

  static {
    try {
      Class<?> sunReflectionClass = Class.forName("sun.reflect.Reflection");
      GET_CALLER_CLASS_METHOD = sunReflectionClass.getDeclaredMethod("getCallerClass", int.class);
    } catch (ClassNotFoundException | NoSuchMethodException | LinkageError e) {
      LOGGER.info(
          "sun.reflect.Reflection#getCallerClass is not found, maybe jdk version is higher than 1.8");
    }
  }

  /**
   * @author: Ares
   * @description: 是否存在getCallerClass方法
   * @description: Whether the getCallerClass method exists
   * @time: 2023-11-09 10:35:43
   * @params: []
   * @return: boolean 是否存在
   */
  public static boolean existGetCallerClassMethod() {
    return null != GET_CALLER_CLASS_METHOD;
  }

  /**
   * @author: Ares
   * @description: 获取调用类
   * @description: Get the calling class
   * @time: 2023-11-09 10:35:18
   * @params: [depth] 堆栈深度
   * @return: java.lang.Class<?> 调用类 invoke class
   */
  public static Class<?> getCallerClass(final int depth) {
    if (depth < 0) {
      throw new IndexOutOfBoundsException(Integer.toString(depth));
    }
    try {
      return (Class<?>) GET_CALLER_CLASS_METHOD.invoke(null, depth);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @author: Ares
   * @description: 适应性地获取调用类
   * @description: Adaptively get the calling class
   * @time: 2023-11-09 10:34:31
   * @params: [depth] 堆栈深度
   * @return: java.lang.Class<?> 调用类 invoke class
   */
  public static Class<?> adaptiveGetCallerClass(final int depth) {
    if (depth < 0) {
      throw new IndexOutOfBoundsException(Integer.toString(depth));
    }
    // getCallerClass的性能在7个方式中是最好的，但目前实现仅支持jdk8，更高jdk版本需使用其他方式
    // The performance of getCallerClass is the best of the seven modes, but the current implementation only supports jdk8, and later jdk versions need to use other modes
    if (null == GET_CALLER_CLASS_METHOD) {
      return new SecurityManager() {
        public Class<?> securityGetClazz() {
          return getClassContext()[depth + 1];
        }
      }.securityGetClazz();
    } else {
      try {
        return (Class<?>) GET_CALLER_CLASS_METHOD.invoke(null, depth);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

}
