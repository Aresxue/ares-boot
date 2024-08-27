package cn.ares.boot.util.common.structure;

import cn.ares.boot.util.common.MapUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * @author: Ares
 * @time: 2021-11-06 17:21:00
 * @description: 扩展对象
 * @description: Expand object
 * @version: JDK 1.8
 */
public class ExpandObject implements Serializable {

  private static final long serialVersionUID = -7925759494583339810L;

  /**
   * expand map
   */
  private Map<String, Object> expandMap;

  /**
   * @author: Ares
   * @description: Remove value for key
   * @time: 2021-11-06 17:24:00
   * @params: [key] 键
   * @return: java.lang.Object 值
   */
  public Object remove(String key) {
    if (null != this.expandMap) {
      return this.expandMap.remove(key);
    }
    return null;
  }

  /**
   * @author: Ares
   * @description: Clear all key and value
   * @time: 2022-06-08 14:42:55
   * @params: []
   */
  public void clear() {
    if (null != this.expandMap) {
      this.expandMap.clear();
    }
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值不存在时使用默认值
   * @description: Gets the default value when the value corresponding to the key in the extension
   * does not exist
   * @time: 2021-11-06 17:24:00
   * @params: [key, defaultValue] 键, 默认值
   * @return: java.lang.Object 值
   */
  public Object getOrDefault(String key, String defaultValue) {
    if (null != this.expandMap) {
      return this.expandMap.getOrDefault(key, defaultValue);
    }
    return defaultValue;
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值
   * @description: Get expand value
   * @time: 2021-11-06 17:24:00
   * @params: [key] 键
   * @return: java.lang.Object 值
   */
  public Object get(String key) {
    if (null == this.expandMap) {
      return null;
    }
    return this.expandMap.get(key);
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值的字符串
   * @description: Gets the string that corresponds to the value of the key in the extension
   * @time: 2022-06-08 14:44:10
   * @params: [key] 键
   * @return: java.lang.String 字符串值
   */
  public String getString(String key) {
    return Optional.ofNullable(get(key)).map(Object::toString).orElse(null);
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值的长整形
   * @description: Gets the long integer of the value corresponding to the key in the extension
   * @time: 2022-06-08 14:44:10
   * @params: [key] 键
   * @return: java.lang.Long 长整形值
   */
  public Long getLong(String key) {
    return Optional.ofNullable(get(key)).map(value -> Long.parseLong(value.toString()))
        .orElse(null);
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值的整形
   * @description: Gets the integer of the value corresponding to the key in the extension
   * @time: 2022-06-08 14:44:10
   * @params: [key] 键
   * @return: java.lang.Integer 整形值
   */
  public Integer getInteger(String key) {
    return Optional.ofNullable(get(key)).map(value -> Integer.parseInt(value.toString()))
        .orElse(null);
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值的短整形
   * @description: Gets the short integer of the value corresponding to the key in the extension
   * @time: 2022-06-08 14:44:10
   * @params: [key] 键
   * @return: java.lang.Short 短整形值
   */
  public Short getShort(String key) {
    return Optional.ofNullable(get(key)).map(value -> Short.parseShort(value.toString()))
        .orElse(null);
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值的byte
   * @description: Gets the byte for the value corresponding to the key in the extension
   * @time: 2022-06-08 14:44:10
   * @params: [key] 键
   * @return: java.lang.Byte byte值
   */
  public Byte getByte(String key) {
    return Optional.ofNullable(get(key)).map(value -> Byte.parseByte(value.toString()))
        .orElse(null);
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值的双精度
   * @description: Gets the double precision of the value corresponding to the key in the extension
   * @time: 2022-06-08 14:44:10
   * @params: [key] 键
   * @return: java.lang.Double 双精度值
   */
  public Double getDouble(String key) {
    return Optional.ofNullable(get(key)).map(value -> Double.parseDouble(value.toString()))
        .orElse(null);
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值的单精度
   * @description: Gets the single precision of the value corresponding to the key in the extension
   * @time: 2022-06-08 14:44:10
   * @params: [key] 键
   * @return: java.lang.Float 单精度值
   */
  public Float getFloat(String key) {
    return Optional.ofNullable(get(key)).map(value -> Float.parseFloat(value.toString()))
        .orElse(null);
  }

  /**
   * @author: Ares
   * @description: 获取布尔值
   * @time: 2023-06-26 17:44:38
   * @params: [key] 键
   * @return: java.lang.Boolean 布尔值
   */
  public Boolean getBoolean(String key) {
    return Optional.ofNullable(get(key)).map(value -> Boolean.parseBoolean(value.toString()))
        .orElse(null);
  }

  /**
   * @author: Ares
   * @description: 获取扩展中键对应的值
   * @description: Gets the value corresponding to the key in the extension
   * @time: 2022-06-08 14:47:11
   * @params: [key] 键
   * @return: T
   */
  @SuppressWarnings("unchecked")
  public <T> T getObject(String key) {
    return (T) get(key);
  }


  /**
   * @author: Ares
   * @description: Put expand value
   * @time: 2021-11-06 17:24
   * @params: [key, value] 键，值
   */
  public void put(String key, Object value) {
    if (null == this.expandMap) {
      this.expandMap = MapUtil.newHashMap(8);
    }
    this.expandMap.put(key, value);
  }

  /**
   * @author: Ares
   * @description: Put expand value if key not exist
   * @time: 2023-06-26 17:31:44
   * @params: [key, value] 键，值
   */
  public void putIfAbsent(String key, Object value) {
    if (null == this.expandMap) {
      this.expandMap = new HashMap<>(8);
    }
    this.expandMap.putIfAbsent(key, value);
  }

  /**
   * @author: Ares
   * @description: 将扩展对象的键值对放入
   * @description: Put the key-value pair of the extended object into
   * @time: 2023-06-26 17:33:35
   * @params: [expandObject] 扩展对象
   */
  public void putAll(ExpandObject expandObject) {
    if (null == this.expandMap) {
      this.expandMap = MapUtil.newHashMap(8);
    }
    this.expandMap.putAll(expandObject.getExpandMap());
  }

  /**
   * @author: Ares
   * @description: 遍历键值对
   * @description: Iterate over key-value pairs
   * @time: 2023-06-26 17:36:21
   * @params: [action] 遍历处理
   */
  public void forEach(BiConsumer<? super String, ? super Object> action) {
    if (MapUtil.isNotEmpty(expandMap)) {
      expandMap.forEach(action);
    }
  }

  /**
   * @author: Ares
   * @description: 是否包含某个键
   * @description: Whether a key is included
   * @time: 2023-06-26 17:46:43
   * @params: [key] 键
   * @return: boolean 包含
   */
  public boolean containsKey(String key) {
    if (null == this.expandMap) {
      return false;
    }
    return expandMap.containsKey(key);
  }

  /**
   * @author: Ares
   * @description: 是否包含某个值
   * @description: Whether a value is included
   * @time: 2023-06-26 17:46:43
   * @params: [value] 值
   * @return: boolean 包含
   */
  public boolean containsValue(Object value) {
    if (null == this.expandMap) {
      return false;
    }
    return expandMap.containsValue(value);
  }

  private Map<String, Object> getExpandMap() {
    return expandMap;
  }

}

