package cn.ares.boot.util.crypt;

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

  private static <T> T doInReverseCrypt(InReverseCryptCallBack<T> cryptCallBack) {
    if (null == inReverseCrypt) {
      inReverseCrypt = InReverseCrypt.getInstance();
    }
    return cryptCallBack.doInReverseCrypt();
  }

  private interface InReverseCryptCallBack<T> {

    T doInReverseCrypt();
  }

}
