package cn.ares.boot.util.common.thread;

import cn.ares.boot.util.common.MapUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Ares
 * @time: 2021-04-10 17:54:00
 * @description: 本地线程变量工具
 * @description: ThreadLocal map util
 * @version: JDK 1.8
 */
public class ThreadLocalMapUtil {

  private static final ThreadLocal<Map<Object, Object>> THREAD_LOCAL_MAP = new InheritableThreadLocal<>();

  public static Map<Object, Object> getThreadLocal() {
    return THREAD_LOCAL_MAP.get();
  }

  @SuppressWarnings("unchecked")
  public static <T> T get(Object key) {
    Map<Object, Object> map = THREAD_LOCAL_MAP.get();
    if (MapUtil.isEmpty(map)) {
      return null;
    }
    return (T) map.get(key);
  }

  @SuppressWarnings("unchecked")
  public static <T> T get(Object key, T defaultValue) {
    Map<Object, Object> map = THREAD_LOCAL_MAP.get();
    if (map == null) {
      return null;
    }
    return map.get(key) == null ? defaultValue : (T) map.get(key);
  }

  public static void set(Object key, Object value) {
    Map<Object, Object> map = THREAD_LOCAL_MAP.get();
    if (null == map) {
      map = new HashMap<>(4);
    }
    map.put(key, value);
    THREAD_LOCAL_MAP.set(map);
  }

  public static void set(Map<Object, Object> keyValueMap) {
    Map<Object, Object> map = THREAD_LOCAL_MAP.get();
    if (null == map) {
      map = new HashMap<>();
    }
    map.putAll(keyValueMap);
    THREAD_LOCAL_MAP.set(map);
  }

  public static void remove() {
    THREAD_LOCAL_MAP.remove();
  }

  @SuppressWarnings("unchecked")
  public static <T> T remove(String key) {
    Map<Object, Object> map = THREAD_LOCAL_MAP.get();
    if (map == null) {
      return null;
    }
    return (T) map.remove(key);
  }

  public static void clear(String prefix) {
    if (prefix == null) {
      return;
    }
    Map<Object, Object> map = THREAD_LOCAL_MAP.get();
    if (map == null) {
      return;
    }

    map.forEach((key, value) -> {
      String keyStr = String.valueOf(key);
      if (keyStr != null) {
        if (keyStr.startsWith(prefix)) {
          map.remove(key);
        }
      }
    });
  }

  /**
   * @author: Ares
   * @description: 获取指定前缀的值
   * @description: Get the value of the specified prefix
   * @time: 2022-06-08 15:01:01
   * @params: [prefix] 前缀
   * @return: java.util.Map<java.lang.Object, T>
   */
  @SuppressWarnings("unchecked")
  public static <T> Map<Object, T> fetchValuesByPrefix(String prefix) {
    Map<Object, T> values = new HashMap<>();
    if (prefix == null) {
      return values;
    }
    Map<Object, Object> map = THREAD_LOCAL_MAP.get();
    map.forEach((key, value) -> {
      String keyStr = String.valueOf(key);
      if (keyStr != null) {
        if (keyStr.startsWith(prefix)) {
          values.put(key, (T) value);
        }
      }
    });

    return values;
  }

}
