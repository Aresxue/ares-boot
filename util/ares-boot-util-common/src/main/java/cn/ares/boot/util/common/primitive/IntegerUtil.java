package cn.ares.boot.util.common.primitive;

import cn.ares.boot.util.common.StringUtil;

/**
 * @author: Ares
 * @time: 2021-1202 12:56:00 下午
 * @description: Integer util
 * @version: JDK 1.8
 */
public class IntegerUtil {

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回整形
   * @description: Returns Integer when the parsed object is not empty
   * @time: 2022-06-07 11:08:00
   * @params: [object] 对象
   * @return: java.lang.Integer 解析结果
   */
  public static Integer parseInteger(Object object) {
    if (null == object) {
      return null;
    }
    return Integer.parseInt(object.toString());
  }

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回整形为空返回默认值
   * @description: When the parsing object is not empty, the return Integer is empty and the default
   * value is returned
   * @time: 2022-06-07 11:08:00
   * @params: [object, defaultValue] 对象，默认值
   * @return: java.lang.Integer 解析结果
   */
  public static Integer parseIntegerOrDefault(Object object, Integer defaultValue) {
    if (null == object) {
      return defaultValue;
    }
    return Integer.parseInt(object.toString());
  }

  /**
   * @author: Ares
   * @description: 将长整形转为合适的整形
   * @description: Convert long shape to proper shape
   * @time: 2022-06-07 17:43:12
   * @params: [value] 长整形
   * @return: int 整形
   */
  public static int saturatedCast(long value) {
    if (value > Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    } else {
      return value < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) value;
    }
  }

  /**
   * Parses the string argument as a signed decimal integer.
   * <p>
   * The input may be surrounded by whitespace.
   * </p>
   *
   * @param str            a {@code String} containing the {@code int} representation to parse, may be
   *                     {@code null} or {@code ""}
   * @param defaultValue the return value, use {@code defaultValue} if {@code str} is {@code null} or
   *                     {@code ""}
   * @return the integer value represented by the argument in decimal.
   * @throws NumberFormatException if the string does not contain a parsable integer.
   */
  public static int parseInt(final String str, final int defaultValue) {
    return StringUtil.isEmpty(str) ? defaultValue : Integer.parseInt(str.trim());
  }

  /**
   * Parses the string argument as a signed decimal integer.
   *
   * @param str a {@code String} containing the {@code int} representation to parse, may be
   *          {@code null} or {@code ""}
   * @return the integer value represented by the argument in decimal.
   * @throws NumberFormatException if the string does not contain a parsable integer.
   */
  public static int parseInt(final String str) {
    return parseInt(str, 0);
  }

}
