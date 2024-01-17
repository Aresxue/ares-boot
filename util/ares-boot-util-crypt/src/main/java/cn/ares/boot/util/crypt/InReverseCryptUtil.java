package cn.ares.boot.util.crypt;

import java.util.function.Supplier;
import java.util.zip.CRC32;

/**
 * @author: Ares
 * @time: 2021-12-23 21:08:52
 * @description: InReverse crypt util
 * @description: 不可逆加密算法工具
 * @version: JDK 1.8
 */
public class InReverseCryptUtil {

  private static InReverseCrypt inReverseCrypt;


  public static byte[] enCrypt(byte[] srcData) {
    return doInReverseCrypt(() -> inReverseCrypt.enCrypt(srcData));
  }

  public static String enCrypt(String srcData) {
    return doInReverseCrypt(() -> inReverseCrypt.enCrypt(srcData));
  }

  public static boolean signatureVerify(byte[] srcData, byte[] targetData) {
    return doInReverseCrypt(
        () -> inReverseCrypt.signatureVerify(srcData, targetData));
  }


  public static boolean signatureVerify(byte[] srcData, byte[] targetData,
      String salt) {
    return doInReverseCrypt(() -> doInReverseCrypt(
        () -> inReverseCrypt.signatureVerify(srcData, targetData, salt)));
  }

  public static String getDefaultSalt() {
    return doInReverseCrypt(() -> inReverseCrypt.getDefaultSalt());
  }

  public static String generateSalt() {
    return doInReverseCrypt(() -> inReverseCrypt.generateSalt());
  }

  public static String generateSalt(int numBytes) {
    return doInReverseCrypt(() -> inReverseCrypt.generateSalt(numBytes));
  }

  public static String enCrypt(String srcData, String salt) {
    return doInReverseCrypt(() -> inReverseCrypt.enCrypt(srcData, salt));
  }

  public static byte[] enCrypt(byte[] srcData, String salt) {
    return doInReverseCrypt(() -> inReverseCrypt.enCrypt(srcData, salt));
  }

  public static boolean signatureVerify(String srcData, String targetData,
      String privateKeyOrSalt) {
    return doInReverseCrypt(
        () -> inReverseCrypt.signatureVerify(srcData, targetData, privateKeyOrSalt));
  }

  public static boolean signatureVerify(String srcData, String targetData) {
    return doInReverseCrypt(
        () -> inReverseCrypt.signatureVerify(srcData, targetData));
  }

  private static <T> T doInReverseCrypt(Supplier<T> inReverseCryptSupplier) {
    if (null == inReverseCrypt) {
      inReverseCrypt = InReverseCrypt.getInstance();
    }
    return inReverseCryptSupplier.get();
  }

  /**
   * @author: Ares
   * @description: 计算字节数组的校验和
   * @description: Calculate the checksum of a byte array
   * @time: 2024-01-17 17:09:40
   * @params: [byteArr] 字节数组
   * @return: int 校验和
   */
  public static int crc32(byte[] byteArr) {
    if (byteArr != null) {
      return crc32(byteArr, 0, byteArr.length);
    }

    return 0;
  }

  /**
   * @author: Ares
   * @description: 计算字节数组的校验和
   * @description: Calculate the checksum of a byte array
   * @time: 2024-01-17 17:09:40
   * @params: [byteArr, offset, length] 字节数组，偏移量，长度
   * @return: int 校验和
   */
  public static int crc32(byte[] byteArr, int offset, int length) {
    CRC32 crc32 = new CRC32();
    crc32.update(byteArr, offset, length);
    return (int) (crc32.getValue() & 0x7FFFFFFF);
  }

}
