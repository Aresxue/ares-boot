package cn.ares.boot.util.common.primitive;

/**
 * @author: Ares
 * @time: 2021-11-23 16:24:00
 * @description: Byte util
 * @version: JDK 1.8
 */
public class ByteUtil {

  /**
   * @author: Ares
   * @description: 合并多个字节数组
   * @description: Merge multiple byte array
   * @time: 2021-11-22 13:21:00
   * @params: [origin, extra] 原始字节数组，额外字节数组
   * @return: byte[] 合并后数组
   */
  public static byte[] merge(byte[] origin, byte[]... extra) {
    if (null == origin) {
      throw new RuntimeException("Origin data is null");
    }
    int newLength = origin.length;
    for (byte[] bytes : extra) {
      if (null != bytes) {
        newLength += bytes.length;
      }
    }
    byte[] result = new byte[newLength];
    int position = origin.length;
    System.arraycopy(origin, 0, result, 0, position);
    for (byte[] bytes : extra) {
      if (null != bytes) {
        int currentLength = bytes.length;
        System.arraycopy(bytes, 0, result, position, currentLength);
        position += currentLength;
      }
    }

    return result;
  }

  /**
   * @author: Ares
   * @description: 字节数组右填充0到指定长度
   * @description: The byte array is right-padded with 0 to the specified length
   * @time: 2022-06-07 16:59:53
   * @params: [data, length] 字节数组，长度
   * @return: byte[] 填充后数组
   */
  public static byte[] rightPaddingZero(byte[] data, int length) {
    if (length == 0) {
      throw new RuntimeException("Length don't allow is zero");
    }
    byte[] dataByte = data;

    if (data.length % length != 0) {
      byte[] blankBytes = new byte[length - data.length % length];
      dataByte = merge(data, blankBytes);
    }
    return dataByte;
  }

  /**
   * @author: Ares
   * @description: 把字节数组转为16进制字符串
   * @description: Convert byte array to hexadecimal string
   * @time: 2022-06-07 17:41:09
   * @params: [src] 字节数组
   * @return: java.lang.String 16进制字符串
   */
  public static String bytesToHexString(byte[] src) {
    StringBuilder stringBuilder = new StringBuilder();
    if (src == null || src.length == 0) {
      return null;
    }
    for (byte b : src) {
      int value = b & 0xFF;
      String hexValue = Integer.toHexString(value);
      if (hexValue.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hexValue);
    }
    return stringBuilder.toString();
  }

  /**
   * @author: Ares
   * @description: 把16进制字符串转为字节数组
   * @description: Convert hexadecimal string to byte array
   * @time: 2022-06-07 17:41:51
   * @params: [hexString] 16进制字符串
   * @return: byte[] 字节数组
   */
  public static byte[] hexStringToBytes(String hexString) {
    if (null == hexString || hexString.isEmpty()) {
      return null;
    }
    hexString = hexString.toLowerCase();
    final byte[] byteArray = new byte[hexString.length() >> 1];
    int index = 0;
    for (int i = 0; i < hexString.length(); i++) {
      if (index > hexString.length() - 1) {
        return byteArray;
      }
      byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
      byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
      byteArray[i] = (byte) (highDit << 4 | lowDit);
      index += 2;
    }
    return byteArray;
  }

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回Byte
   * @description: Returns a Byte when the parsed object is not empty
   * @time: 2022-06-07 11:08:00
   * @params: [object] 对象
   * @return: java.lang.Byte 解析结果
   */
  public static Byte parseByte(Object object) {
    if (null == object) {
      return null;
    }
    return Byte.parseByte(object.toString());
  }

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回Byte为空返回默认值
   * @description: When the parsing object is not empty, the return Byte is empty and the default
   * value is returned
   * @time: 2022-06-07 11:08:00
   * @params: [object, defaultValue] 对象，默认值
   * @return: java.lang.Byte 解析结果
   */
  public static Byte parseByteOrDefault(Object object, Byte defaultValue) {
    if (null == object) {
      return defaultValue;
    }
    return Byte.parseByte(object.toString());
  }

}
