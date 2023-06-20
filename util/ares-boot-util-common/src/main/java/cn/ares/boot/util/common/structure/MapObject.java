package cn.ares.boot.util.common.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author: Ares
 * @time: 2021-12-05 19:56:00
 * @description: Map object
 * @version: JDK 1.8
 */
public class MapObject<V> extends HashMap<String, V> {

  private static final long serialVersionUID = 8446384530352600792L;

  public MapObject() {
  }

  public MapObject(int initialCapacity) {
    super(initialCapacity);
  }

  public MapObject(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  public MapObject(Map<? extends String, ? extends V> m) {
    super(m);
  }

  public String getString(String key) {
    return Optional.ofNullable(get(key)).map(Object::toString).orElse(null);
  }

  public Long getLong(String key) {
    return Optional.ofNullable(get(key)).map(value -> Long.parseLong(value.toString()))
        .orElse(null);
  }

  public Integer getInteger(String key) {
    return Optional.ofNullable(get(key)).map(value -> Integer.parseInt(value.toString()))
        .orElse(null);
  }

  public Short getShort(String key) {
    return Optional.ofNullable(get(key)).map(value -> Short.parseShort(value.toString()))
        .orElse(null);
  }

  public Byte getByte(String key) {
    return Optional.ofNullable(get(key)).map(value -> Byte.parseByte(value.toString()))
        .orElse(null);
  }

  public Double getDouble(String key) {
    return Optional.ofNullable(get(key)).map(value -> Double.parseDouble(value.toString()))
        .orElse(null);
  }

  public Float getFloat(String key) {
    return Optional.ofNullable(get(key)).map(value -> Float.parseFloat(value.toString()))
        .orElse(null);
  }

  public <T> T getObject(String key) {
    return (T) Optional.ofNullable(get(key)).orElse(null);
  }

}
