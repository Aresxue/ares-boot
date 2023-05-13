package cn.ares.boot.util.common.primitive;

/**
 * @author: Ares
 * @time: 2021-12-02 12:57:00
 * @description: Long util
 * @version: JDK 1.8
 */
public class LongUtil {

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回长整形
   * @description: Returns a Long when the parsed object is not empty
   * @time: 2022-06-07 11:08:00
   * @params: [object] 对象
   * @return: java.lang.Long 解析结果
   */
  public static Long parseLong(Object object) {
    if (null == object) {
      return null;
    }
    return Long.parseLong(object.toString());
  }

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回长整形为空返回默认值
   * @description: When the parsing object is not empty, the return Long is empty and the default value is returned
   * @time: 2022-06-07 11:08:00
   * @params: [object, defaultValue] 对象，默认值
   * @return: java.lang.Long 解析结果
   */
  public static Long parseLongOrDefault(Object object, Long defaultValue) {
    if (null == object) {
      return defaultValue;
    }
    return Long.parseLong(object.toString());
  }

  /**
   * @author: Ares
   * @description: 返回长整形长度
   * @description: Returns the length of the long integer
   * @time: 2022-06-07 17:44:09
   * @params: [value] 长整形
   * @return: int 长度
   */
  public static int length(Long value){
    if(null == value){
      return 0;
    }
    return String.valueOf(value).length();
  }

}
