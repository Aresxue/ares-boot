package cn.ares.boot.util.common.primitive;

/**
 * @author: Ares
 * @time: 2021-12-02 13:09:00
 * @description: Short util
 * @version: JDK 1.8
 */
public class ShortUtil {

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回短整型
   * @description: Returns a Short when the parsed object is not empty
   * @time: 2022-06-07 11:08:00
   * @params: [object] 对象
   * @return: java.lang.Short 解析结果
   */
  public static Short parseShort(Object object) {
    if (null == object) {
      return null;
    }
    return Short.parseShort(object.toString());
  }


  /**
   * @author: Ares
   * @description: 解析对象不为空时返回短整型为空返回默认值
   * @description: When the parsing object is not empty, the return Short is empty and the default value is returned
   * @time: 2022-06-07 11:08:00
   * @params: [object, defaultValue] 对象，默认值
   * @return: java.lang.Short 解析结果
   */
  public static Short parseShortOrDefault(Object object, Short defaultValue) {
    if (null == object) {
      return defaultValue;
    }
    return Short.parseShort(object.toString());
  }

}
