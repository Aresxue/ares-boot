package cn.ares.boot.util.common.primitive;

/**
 * @author: Ares
 * @time: 2021-12-05 21:03:00
 * @description: Double util
 * @version: JDK 1.8
 */
public class DoubleUtil {

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回双精度浮点数
   * @description: Returns a Double when the parsed object is not empty
   * @time: 2022-06-07 11:08:00
   * @params: [object] 对象
   * @return: java.lang.Double 解析结果
   */
  public static Double parseDouble(Object object) {
    if (null == object) {
      return null;
    }
    return Double.parseDouble(object.toString());
  }

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回双精度浮点数为空返回默认值
   * @description: When the parsing object is not empty, the return Double is empty and the default value is returned
   * @time: 2022-06-07 11:08:00
   * @params: [object, defaultValue] 对象，默认值
   * @return: java.lang.Double 解析结果
   */
  public static Double parseDoubleOrDefault(Object object, Double defaultValue) {
    if (null == object) {
      return defaultValue;
    }
    return Double.parseDouble(object.toString());
  }

}
