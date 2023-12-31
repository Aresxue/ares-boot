package cn.ares.boot.util.common.primitive;

/**
 * @author: Ares
 * @time: 2021-12-16 20:06:00
 * @description: Boolean util
 * @version: JDK 1.8
 */
public class BooleanUtil {

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回Boolean
   * @description: Returns a Boolean when the parsed object is not empty
   * @time: 2022-06-07 11:08:00
   * @params: [object] 对象
   * @return: java.lang.Boolean 解析结果
   */
  public static Boolean parseBoolean(Object object) {
    if (null == object) {
      return null;
    }
    return Boolean.parseBoolean(object.toString());
  }

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回Boolean为空返回默认值
   * @description: When the parsing object is not empty, the return Boolean is empty and the default
   * value is returned
   * @time: 2022-06-07 11:08:00
   * @params: [object, defaultValue] 对象，默认值
   * @return: java.lang.Boolean 解析结果
   */
  public static Boolean parseBooleanOrDefault(Object object, Boolean defaultValue) {
    if (null == object) {
      return defaultValue;
    }
    return Boolean.parseBoolean(object.toString());
  }

  /**
   * @author: Ares
   * @description: 是否为true
   * @time: 2023-12-30 17:27:40
   * @params: [object] 对象
   * @return: boolean 是否为true
   */
  public static boolean isTrue(Object object) {
    if (null == object) {
      return false;
    }
    return Boolean.parseBoolean(object.toString());
  }

  /**
   * @author: Ares
   * @description: 是否是true
   * @time: 2024-12-31 13:37:30
   * @params: [booleanValue] 布尔值包装对象
   * @return: boolean
   */
  public static boolean isTrue(Boolean booleanWrap) {
    return null != booleanWrap && booleanWrap;
  }

  /**
   * @author: Ares
   * @description: 是否是false
   * @time: 2024-12-31 13:37:30
   * @params: [booleanValue] 布尔值包装对象
   * @return: boolean
   */
  public static boolean isFalse(Boolean booleanWrap) {
    return null == booleanWrap || !booleanWrap;
  }

  /**
   * @author: Ares
   * @description: 是否为false
   * @time: 2023-12-30 20:30:36
   * @params: [object] 对象
   * @return: boolean 是否为false
   */
  public static boolean isFalse(Object object) {
    return !isTrue(object);
  }
  /**
   * @author: Ares
   * @description: 比较两个boolean值是否一致
   * @description: Compares two {@code boolean} values
   * @time: 2022-06-08 10:24:20
   * @params: [x, y] 值
   * @return: int 是否一致
   */
  public static int compare(final boolean x, final boolean y) {
    if (x == y) {
      return 0;
    }
    return x ? 1 : -1;
  }

}
