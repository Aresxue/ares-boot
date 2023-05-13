package cn.ares.boot.util.common;

import java.util.Properties;
import java.util.function.Function;

/**
 * @author: Ares
 * @time: 2023-03-16 10:28:29
 * @description: Property util
 * @description: 属性工具类
 * @version: JDK 1.8
 */
public class PropertyUtil {

  /**
   * @author: Ares
   * @description: 获取属性（不存在时取默认值的字符串）
   * @description: Gets a string(takes the default value if the property does not exist)
   * @time: 2023-03-16 10:37:25
   * @params: [properties, key, defaultValue] 属性，键，默认值
   * @return: java.lang.String 结果
   */
  public static String getProperty(Properties properties, String key, Object defaultValue) {
    String propertyValue = properties.getProperty(key);
    if (null != propertyValue) {
      return propertyValue;
    }
    if (null == defaultValue) {
      return null;
    }
    return String.valueOf(defaultValue);
  }

  /**
   * @author: Ares
   * @description: 从属性中（不存在时取默认值）获取双精度浮点形
   * @description: Gets a Double value from the property (takes the default value if none exists)
   * @time: 2023-03-16 10:42:35
   * @params: [properties, key, defaultValue] 属性，键，默认值
   * @return: java.lang.Double 结果
   */
  public static Double getDoubleProperty(Properties properties, String key, Object defaultValue) {
    return getProperty(properties, key, defaultValue, Double::parseDouble);
  }

  /**
   * @author: Ares
   * @description: 从属性中（不存在时取默认值）获取整形
   * @description: Gets an Integer value from the property (takes the default value if none exists)
   * @time: 2023-03-16 10:42:13
   * @params: [properties, key, defaultValue] 属性，键，默认值
   * @return: java.lang.Integer 结果
   */
  public static Integer getIntegerProperty(Properties properties, String key, Object defaultValue) {
    return getProperty(properties, key, defaultValue, Integer::parseInt);
  }

  /**
   * @author: Ares
   * @description: 从属性中（不存在时取默认值）获取长整形
   * @description: Gets a Long value from the property (takes the default value if none exists)
   * @time: 2023-03-16 10:41:48
   * @params: [properties, key, defaultValue] 属性，键，默认值
   * @return: java.lang.Long 结果
   */
  public static Long getLongProperty(Properties properties, String key, Object defaultValue) {
    return getProperty(properties, key, defaultValue, Long::parseLong);
  }

  /**
   * @author: Ares
   * @description: 从属性中（不存在时取默认值）获取布尔值
   * @description: Gets a Boolean value from the property (takes the default value if none exists)
   * @time: 2023-03-16 10:39:42
   * @params: [properties, key, defaultValue] 属性，键，默认值
   * @return: java.lang.Boolean 结果
   */
  public static Boolean getBooleanProperty(Properties properties, String key, Object defaultValue) {
    return getProperty(properties, key, defaultValue, Boolean::parseBoolean);
  }

  /**
   * @author: Ares
   * @description: 获取属性（不存在时取默认值）并返回函数处理后结果
   * @description: Gets the property (default if it does not exist) and returns the result of the
   * function processing
   * @time: 2023-03-16 10:38:21
   * @params: [properties, key, defaultValue, function] 属性，键，默认值
   * @return: T 结果
   */
  public static <T> T getProperty(Properties properties, String key, Object defaultValue,
      Function<String, T> function) {
    String propertyValue = getProperty(properties, key, defaultValue);
    if (StringUtil.isEmpty(propertyValue)) {
      return null;
    }
    return function.apply(propertyValue);
  }

}
