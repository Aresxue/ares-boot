package cn.ares.boot.util.common;

import cn.ares.boot.util.common.primitive.ByteUtil;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author: Ares
 * @time: 2023-11-30 17:00:20
 * @description: MD5 util
 * @version: JDK 1.8
 */
public class Md5Util {

  private static final ThreadLocal<MessageDigest> MESSAGE_DIGEST_LOCAL = ThreadLocal.withInitial(
      () -> {
        try {
          return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
          return null;
        }
      });

  /**
   * Calculate MD5 hex string.
   *
   * @param bytes byte arrays
   * @return MD5 hex string of input
   * @throws NoSuchAlgorithmException if can't load md5 digest spi.
   */
  public static String md5Hex(byte[] bytes) throws NoSuchAlgorithmException {
    try {
      MessageDigest messageDigest = MESSAGE_DIGEST_LOCAL.get();
      if (messageDigest != null) {
        return ByteUtil.bytesToHexString(messageDigest.digest(bytes));
      }
      throw new NoSuchAlgorithmException("MessageDigest get MD5 instance error");
    } finally {
      MESSAGE_DIGEST_LOCAL.remove();
    }
  }

  /**
   * Calculate MD5 hex string with encode charset.
   *
   * @param value  value
   * @param encode encode charset of input
   * @return MD5 hex string of input
   */
  public static String md5Hex(String value, String encode) {
    return ExceptionUtil.get(() -> md5Hex(value.getBytes(encode)));
  }

  /**
   * Calculate MD5 hex string with encode charset.
   *
   * @param value   value
   * @param charset charset of input
   * @return MD5 hex string of input
   */
  public static String md5Hex(String value, Charset charset) {
    return ExceptionUtil.get(() -> md5Hex(value.getBytes(charset)));
  }


}
