package cn.ares.boot.util.common;

import java.util.Optional;

/**
 * @author: Ares
 * @time: 2024-08-14 15:17:21
 * @description: 对象工具类
 * @description: Object util
 * @version: JDK 1.8
 */
public class ObjectUtil {

  /**
   * @author: Ares
   * @description: 返回第一个非空值
   * @description: Returns the first non-null value
   * @time: 2024-01-29 10:20:47
   * @params: [values] 值数组
   * @return: java.util.Optional<T> 结果
   */
  @SafeVarargs
  public static <T> Optional<T> coalesce(final T... values) {
    if (ArrayUtil.isNotEmpty(values)) {
      for (T value : values) {
        if (null != value) {
          return Optional.of(value);
        }
      }
    }
    return Optional.empty();
  }

}
