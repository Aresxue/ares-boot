package cn.ares.boot.util.common.primitive;

/**
 * @author: Ares
 * @time: 2021-12-05 21:03:00
 * @description: Float util
 * @version: JDK 1.8
 */
public class FloatUtil {

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回单精度浮点数
   * @description: Returns a Float when the parsed object is not empty
   * @time: 2022-06-07 11:08:00
   * @params: [object] 对象
   * @return: java.lang.Float 解析结果
   */
  public static Float parseFloat(Object object) {
    if (null == object) {
      return null;
    }
    return Float.parseFloat(object.toString());
  }

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回单精度浮点数为空返回默认值
   * @description: When the parsing object is not empty, the return Float is empty and the default value is returned
   * @time: 2022-06-07 11:08:00
   * @params: [object, defaultValue] 对象，默认值
   * @return: java.lang.Float 解析结果
   */
  public static Float parseFloatOrDefault(Object object, Float defaultValue) {
    if (null == object) {
      return defaultValue;
    }
    return Float.parseFloat(object.toString());
  }

}
