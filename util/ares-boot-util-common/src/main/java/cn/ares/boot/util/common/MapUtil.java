package cn.ares.boot.util.common;

import cn.ares.boot.util.common.primitive.ByteUtil;
import cn.ares.boot.util.common.primitive.IntegerUtil;
import cn.ares.boot.util.common.primitive.LongUtil;
import cn.ares.boot.util.common.primitive.ShortUtil;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: Ares
 * @time: 2021-12-02 20:08:00
 * @description: Map util
 * @version: JDK 1.8
 */
public class MapUtil {

  public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

  /**
   * @author: Ares
   * @description: 合并属性到map
   * @description: Merge properties into map
   * @time: 2022-06-07 16:52:41
   * @params: [props, map] 属性，map
   * @return: void
   */
  public static <K, V> void mergePropertiesIntoMap(Properties props, Map<K, V> map) {
    if (props != null) {
      for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements(); ) {
        String key = (String) en.nextElement();
        Object value = props.get(key);
        if (value == null) {
          // Allow for defaults fallback or potentially overridden accessor...
          value = props.getProperty(key);
        }
        map.put((K) key, (V) value);
      }
    }
  }

  /**
   * @author: Ares
   * @description: 以期望的元素个数创建HashMap
   * @description: New HashMap with expected size
   * @time: 2021-12-24 11:21:07
   * @params: [expectedSize] 期望的元素个数
   * @return: java.util.HashMap<K, V>
   */
  public static <K, V> HashMap<K, V> newHashMap(int expectedSize) {
    return new HashMap<>(capacity(expectedSize));
  }

  /**
   * @author: Ares
   * @description: 根据入参构建一个新映射（不校验入参长度）
   * @description: Build a new map based on the input(Do not verify input parameter length)
   * @time: 2024-01-18 14:03:14
   * @params: [args] 参数
   * @return: java.util.Map<K, V> 映射
   */
  public static <K, V> Map<K, V> newMap(Object... args) {
    return newMap(false, args);
  }

  /**
   * @author: Ares
   * @description: 根据入参构建一个新映射（是否校验入参长度）
   * @description: Build a new map based on the input(Whether to verify the input parameter length)
   * @time: 2024-01-18 14:03:14
   * @params: [checkLength, args] 是否校验入参长度，参数
   * @return: java.util.Map<K, V> 映射
   */
  public static <K, V> Map<K, V> newMap(boolean checkLength, Object... args) {
    if (checkLength && ArrayUtil.isOddLength(args)) {
      throw new IllegalArgumentException("Args length must be even");
    }
    return newMap(null, args);
  }

  /**
   * @author: Ares
   * @description: 根据入参构建一个新的有序映射（不校验入参长度）
   * @description: Build a new linked map based on the input(Do not verify input parameter length)
   * @time: 2024-01-18 14:03:14
   * @params: [args] 参数
   * @return: java.util.Map<K, V> 映射
   */
  public static <K, V> Map<K, V> newLinkedHashMap(Object... args) {
    return newLinkedHashMap(false, args);
  }

  /**
   * @author: Ares
   * @description: 根据入参构建一个新的有序映射（是否校验入参长度）
   * @description: Build a new  linked map based on the input(Whether to verify the input parameter length)
   * @time: 2024-01-18 14:03:14
   * @params: [checkLength, args] 是否校验入参长度，参数
   * @return: java.util.Map<K, V> 映射
   */
  public static <K, V> Map<K, V> newLinkedHashMap(boolean checkLength, Object... args) {
    return newMap(LinkedHashMap.class, args);
  }

  /**
   * @author: Ares
   * @description: 根据映射实现类型和入参构建一个新映射
   * @description: Build a new map based on the input
   * @time: 2024-01-11 14:03:14
   * @params: [mapImplClass, args] 映射实现实现类，参数
   * @return: java.util.Map<K, V> 映射
   */
  @SuppressWarnings("unchecked")
  public static <M, K, V> Map<K, V> newMap(Class<M> mapImplClass, Object... args) {
    if (ArrayUtil.isEmpty(args)) {
      return Collections.emptyMap();
    }
    int capacityLength = capacity(args.length);
    Map<K, V> map;
    if (null == mapImplClass) {
      map = new HashMap<>(capacityLength);
    } else {
      map = ExceptionUtil.get(() -> {
        Constructor<?>[] constructors = mapImplClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
          if (constructor.getParameterTypes().length == 1
              && int.class == constructor.getParameterTypes()[0]) {
            return (Map<K, V>) constructor.newInstance(capacityLength);
          }
        }
        return (Map<K, V>) constructors[0].newInstance();
      });
    }

    for (int i = 0; i < args.length -1; i = i + 2) {
      map.put((K) args[i], (V) args[i + 1]);
    }
    return map;
  }

  /**
   * @author: Ares
   * @description: 以默认的元素个数创建HashMap
   * @description: New HashMap with default size
   * @time: 2022-06-07 16:54:01
   * @params: []
   * @return: java.util.HashMap<K, V>
   */
  public static <K, V> HashMap<K, V> newHashMap() {
    return new HashMap<>(16);
  }

  /**
   * @author: Ares
   * @description: 以期望的元素个数创建HashMap
   * @description: New ConcurrentMap with expected size
   * @time: 2023-05-08 11:46:35
   * @params: [expectedSize] 期望的元素个数
   * @return: java.util.concurrent.ConcurrentMap<K, V>
   */
  public static <K, V> ConcurrentMap<K, V> newConcurrentMap(int expectedSize) {
    return new ConcurrentHashMap<>(capacity(expectedSize));
  }

  /**
   * @author: Ares
   * @description: 以默认的元素个数创建HashMap
   * @description: New ConcurrentMap with default size
   * @time: 2023-05-08 11:46:35
   * @params: []
   * @return: java.util.concurrent.ConcurrentMap<K, V>
   */
  public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
    return new ConcurrentHashMap<>(16);
  }


  /**
   * @author: Ares
   * @description: 以期望的元素个数创建LinkedHashMap
   * @description: New LinkedHashMap with expected size
   * @time: 2021-12-24 11:21:07
   * @params: [expectedSize] expected size
   * @return: java.util.LinkedHashMap<K, V> out 出参
   */
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int expectedSize) {
    return new LinkedHashMap<>(capacity(expectedSize));
  }

  /**
   * @author: Ares
   * @description: 根据期望的容量为映射选取合适的容量
   * @description: Choose the appropriate capacity for the mapping based on the desired capacity
   * @time: 2023-12-25 17:25:59
   * @params: [expectedSize] 期望的元素容量
   * @return: int 合适的容量
   */
  public static int capacity(int expectedSize) {
    if (expectedSize < 3) {
      checkNonNegative(expectedSize, "expectedSize");
      return expectedSize + 1;
    }
    if (expectedSize < MAX_POWER_OF_TWO) {
      // This is the calculation used in JDK8 to resize when a putAll
      // happens; it seems to be the most conservative calculation we
      // can make.  0.75 is the default load factor.
      return (int) ((float) expectedSize / 0.75F + 1.0F);
    }
    return Integer.MAX_VALUE;
  }

  private static int checkNonNegative(int value, String name) {
    if (value < 0) {
      throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
    }
    return value;
  }

  /**
   * @author: Ares
   * @description: 判断map是否为空
   * @description: Determine map is empty
   * @time: 2022-06-07 16:56:08
   * @params: [map] map
   * @return: boolean 是否非空
   */
  public static <K, V> boolean isEmpty(Map<K, V> map) {
    return (map == null || map.isEmpty());
  }

  /**
   * @author: Ares
   * @description: 判断map是否非空
   * @description: Determine map is not empty
   * @time: 2022-06-07 16:56:08
   * @params: [map] map
   * @return: boolean 是否非空
   */
  public static <K, V> boolean isNotEmpty(Map<K, V> map) {
    return !isEmpty(map);
  }

  /**
   * @author: Ares
   * @description: 获取映射中键对应的值
   * @description: Get value from map
   * @time: 2022-06-07 16:56:55
   * @params: [map, key] 映射, 键
   * @return: V 值
   */
  public static <K, V> V parseValue(Map<K, V> map, K key) {
    if (isEmpty(map)) {
      return null;
    }
    return map.get(key);
  }

  /**
   * @author: Ares
   * @description: 获取映射中键对应的值并转为字符串
   * @description: Get value from map and convert to string
   * @time: 2022-06-07 16:57:56
   * @params: [map, key] map, 键
   * @return: java.lang.String 字符串
   */
  public static <K, V> String parseStringValue(Map<K, V> map, K key) {
    if (isEmpty(map)) {
      return null;
    }
    return StringUtil.parseString(map.get(key));
  }

  /**
   * @author: Ares
   * @description: 获取映射中键对应的值并转为Byte
   * @description: Get value from map and convert to Byte
   * @time: 2022-06-07 16:57:56
   * @params: [map, key] map, 键
   * @return: java.lang.String 字符串
   */
  public static <K, V> Byte parseByteValue(Map<K, V> map, K key) {
    if (isEmpty(map)) {
      return null;
    }
    return ByteUtil.parseByte(map.get(key));
  }

  /**
   * @author: Ares
   * @description: 获取映射中键对应的值并转为Short
   * @description: Get value from map and convert to Short
   * @time: 2022-06-07 16:57:56
   * @params: [map, key] map, 键
   * @return: java.lang.String 字符串
   */
  public static <K, V> Short parseShortValue(Map<K, V> map, K key) {
    if (isEmpty(map)) {
      return null;
    }
    return ShortUtil.parseShort(map.get(key));
  }

  /**
   * @author: Ares
   * @description: 获取映射中键对应的值并转为Integer
   * @description: Get value from map and convert to Integer
   * @time: 2022-06-07 16:57:56
   * @params: [map, key] map, 键
   * @return: java.lang.String 字符串
   */
  public static <K, V> Integer parseIntegerValue(Map<K, V> map, K key) {
    if (isEmpty(map)) {
      return null;
    }
    return IntegerUtil.parseInteger(map.get(key));
  }

  /**
   * @author: Ares
   * @description: 获取映射中键对应的值并转为Long
   * @description: Get value from map and convert to Long
   * @time: 2022-06-07 16:57:56
   * @params: [map, key] map, 键
   * @return: java.lang.String 字符串
   */
  public static <K, V> Long parseLongValue(Map<K, V> map, K key) {
    if (isEmpty(map)) {
      return null;
    }
    return LongUtil.parseLong(map.get(key));
  }

  /**
   * @author: Ares
   * @description: 获取映射中键对应的值如果map为空返回默认值
   * @description: Get the value in the map and return the default value if the map is empty
   * @time: 2022-06-07 16:57:56
   * @params: [map, key] map, 键
   * @return: java.lang.String 字符串
   */
  public static <K, V> V parseValueOrDefault(Map<K, V> map, K key, V defaultValue) {
    if (isEmpty(map)) {
      return defaultValue;
    }
    return map.get(key);
  }


  /**
   * @author: Ares
   * @description: 根据属性创建map
   * @description: Create map from properties
   * @time: 2021-12-24 11:19:30
   * @params: [properties] properties
   * @return: java.util.Map<java.lang.String, java.lang.String> properties map
   */
  public static Map<String, String> fromProperties(Properties properties) {
    if (null == properties) {
      return Collections.emptyMap();
    }
    Map<String, String> map = newHashMap(properties.size());
    Enumeration<?> enumeration = properties.propertyNames();

    while (enumeration.hasMoreElements()) {
      String key = (String) enumeration.nextElement();
      map.put(key, properties.getProperty(key));
    }

    return map;
  }

  /**
   * @author: Ares
   * @description: 将映射的键替换指定字符串为另一个字符串
   * @description: Replaces the specified string with another string by the mapped key
   * @time: 2023-05-08 10:56:08
   * @params: [map, search, replace] 映射，搜索字符串，替换字符串
   * @return: java.util.Map<java.lang.String, java.lang.Object> 替换后映射
   */
  public static <T> Map<String, T> replaceKey(Map<String, T> map, String search,
      String replace) {
    Map<String, T> newMap = MapUtil.newHashMap(map.size());
    map.forEach((key, value) -> {
      String newKey;
      if (key.contains(search)) {
        newKey = StringUtil.replace(key, search, replace);
      } else {
        newKey = key;
      }
      newMap.put(newKey, value);
    });
    return newMap;
  }

  /**
   * @author: Ares
   * @description: 剔除值为null的键值对
   * @description: Trim value
   * @time: 2022-11-28 22:52:07
   * @params: [sourceMap] source map
   * @return: java.util.Map<java.lang.String, T> result map
   */
  public static <T> Map<String, T> trimValue(Map<String, T> sourceMap) {
    if (isEmpty(sourceMap)) {
      return sourceMap;
    }
    List<String> removeKeyList = new ArrayList<>();
    sourceMap.forEach((key, value) -> {
      if (null == value) {
        removeKeyList.add(key);
      }
    });
    for (String key : removeKeyList) {
      sourceMap.remove(key);
    }
    return sourceMap;
  }

}
