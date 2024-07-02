package cn.ares.boot.util.common;

import static cn.ares.boot.util.common.constant.SymbolConstant.SPOT;

import cn.ares.boot.util.common.structure.MapObject;
import java.util.List;
import java.util.Map;
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

  /**
   * @author: Ares
   * @description: 将属性转为嵌套映射（以.为分隔符）
   * @description: Convert attributes to nested mappings (to. Is delimiter)
   * @time: 2023-07-12 16:47:50
   * @params: [properties] 属性
   * @return: java.util.Map<java.lang.String, java.lang.Object> 嵌套映射
   */
  public static Map<String, Object> convertToNestedMap(Properties properties) {
    MapObject nestedMap = new MapObject();

    properties.forEach((key, value) -> {
      List<String> partList = StringUtil.listSplit(key.toString(), SPOT);
      MapObject currentMap = nestedMap;

      for (int i = 0; i < partList.size() - 1; i++) {
        String part = partList.get(i);
        currentMap.putIfAbsent(part, new MapObject());
        currentMap = currentMap.getObject(part);
      }

      String lastPart = partList.get(partList.size() - 1);
      currentMap.put(lastPart, value);
    });

    return nestedMap;
  }

  /**
   * @author: Ares
   * @description: 创建属性（不校验入参长度）
   * @description: Create new properties(Do not verify input parameter length)
   * @time: 2024-04-29 12:29:56
   * @params: [args] 参数
   * @return: java.util.Properties 属性
   */
  public static Properties newProperties(Object... args) {
    return newProperties(false, args);
  }

  /**
   * @author: Ares
   * @description: 创建属性（是否校验入参长度）
   * @description: Create new properties(Whether to verify the input parameter length)
   * @time: 2024-04-29 12:29:56
   * @params: [checkLength, args] 是否校验入参长度，参数
   * @return: java.util.Properties 属性
   */
  public static Properties newProperties(boolean checkLength, Object... args) {
    if (checkLength && ArrayUtil.isOddLength(args)) {
      throw new IllegalArgumentException("Args length must be even");
    }
    Properties properties = new Properties();
    for (int i = 0; i < args.length - 1; i = i + 2) {
      properties.put(args[i], args[i + 1]);
    }
    return properties;
  }

  /**
   * @author: Ares
   * @description: 获取系统属性
   * @description: Get system property
   * @time: 2024-07-02 17:54:36
   * @params: [key] 键
   * @return: java.lang.String 值
   */
  public static String getSystemProperty(String key) {
    return System.getProperty(key);
  }

  /**
   * @author: Ares
   * @description: 获取系统属性（不存在时取默认值）
   * @description: Get system property (takes the default value if it does not exist)
   * @time: 2024-07-02 17:55:15
   * @params: [key, defaultValue] 键，默认值
   * @return: java.lang.String 值
   */
  public static String getSystemProperty(String key, String defaultValue) {
    String propValue = System.getProperty(key);
    if (StringUtil.isBlank(propValue)) {
      propValue = defaultValue;
    }
    return propValue;
  }

}
