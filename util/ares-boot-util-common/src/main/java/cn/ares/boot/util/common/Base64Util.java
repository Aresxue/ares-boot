package cn.ares.boot.util.common;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * @author: Ares
 * @time: 2021-12-11 12:24:00
 * @description: Base64 util
 * @version: JDK 1.8
 */
public class Base64Util {

  private static final Decoder DECODER = Base64.getDecoder();
  private static final Encoder ENCODER = Base64.getEncoder();

  /**
   * @author: Ares
   * @description: 以默认编码编码文本
   * @description: Encode text in default encoding
   * @time: 2022-06-08 11:08:23
   * @params: [text] 文本
   * @return: java.lang.String 解码后文本
   */
  public static String encode(String text) {
    return encode(text, Charset.defaultCharset());
  }

  /**
   * @author: Ares
   * @description: 编码byte数组返回字符串
   * @description: Encode byte array and return string
   * @time: 2022-06-08 11:08:23
   * @params: [bytes] byte数组
   * @return: java.lang.String 解码后文本
   */
  public static String encode(byte[] bytes) {
    return ENCODER.encodeToString(bytes);
  }

  /**
   * @author: Ares
   * @description: 以指定编码编码文本
   * @description: Encode text in specified encoding
   * @time: 2022-06-08 11:08:23
   * @params: [text, charset] 文本，编码
   * @return: java.lang.String 解码后文本
   */
  public static String encode(String text, Charset charset) {
    return ENCODER.encodeToString(text.getBytes(charset));
  }


  /**
   * @author: Ares
   * @description: 把文本以默认编码编码成byte数组
   * @description: Encode the text into a byte array with the default encoding
   * @time: 2022-06-08 11:10:49
   * @params: [text] 文本
   * @return: byte[] 编码后byte数组
   */
  public static byte[] encodeByte(String text) {
    return encodeByte(text, Charset.defaultCharset());
  }

  /**
   * @author: Ares
   * @description: 把文本以指定编码编码成byte数组
   * @description: Encode the text into a byte array with the specified encoding
   * @time: 2022-06-08 11:10:49
   * @params: [text, charset] 文本，编码
   * @return: byte[] 编码后byte数组
   */
  public static byte[] encodeByte(String text, Charset charset) {
    return ENCODER.encode(text.getBytes(charset));
  }

  /**
   * @author: Ares
   * @description: 编码byte数组返回byte数组
   * @description: Encode byte array and return byte array
   * @time: 2022-06-08 11:12:27
   * @params: [bytes] byte数组
   * @return: byte[] 编码后byte数组
   */
  public static byte[] encodeByte(byte[] bytes) {
    return ENCODER.encode(bytes);
  }

  /**
   * @author: Ares
   * @description: 以默认编码解码字符串
   * @description: Decode string in default encoding
   * @time: 2022-06-08 11:13:48
   * @params: [src] 源字符串
   * @return: java.lang.String 解码后字符串
   */
  public static String decode(String src) {
    return decode(src.getBytes(Charset.defaultCharset()));
  }

  /**
   * @author: Ares
   * @description: 以默认编码解码byte数组
   * @description: Decode byte array with default encoding
   * @time: 2022-06-08 11:14:21
   * @params: [src] 源byte数组
   * @return: java.lang.String 解码后字符串
   */
  public static String decode(byte[] src) {
    return decode(src, Charset.defaultCharset());
  }

  /**
   * @author: Ares
   * @description: 以指定编码解码byte数组
   * @description: Decode byte array with specified encoding
   * @time: 2022-06-08 11:14:51
   * @params: [src, charset] 源byte数组，编码
   * @return: java.lang.String 解码后字符串
   */
  public static String decode(byte[] src, Charset charset) {
    return new String(decodeByte(src), charset);
  }

  /**
   * @author: Ares
   * @description: 解码byte数组
   * @description: Decode byte array
   * @time: 2022-06-08 11:16:01
   * @params: [src] 源byte数组
   * @return: byte[] 解码后byte数组
   */
  public static byte[] decodeByte(byte[] src) {
    return DECODER.decode(src);
  }

  /**
   * @author: Ares
   * @description: 以默认编码解码字符串
   * @description: Decode string with default encoding
   * @time: 2022-06-08 11:16:01
   * @params: [src] 源字符串
   * @return: byte[] 解码后byte数组
   */
  public static byte[] decodeByte(String src) {
    return DECODER.decode(src.getBytes(Charset.defaultCharset()));
  }

}
