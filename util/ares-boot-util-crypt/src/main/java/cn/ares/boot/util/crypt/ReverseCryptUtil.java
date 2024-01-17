package cn.ares.boot.util.crypt;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author: Ares
 * @time: 2021-12-23 21:08:52
 * @description: Reverse crypt util
 * @description: 可逆加密算法工具
 * @version: JDK 1.8
 */
public class ReverseCryptUtil {

  private static ReverseCrypt reverseCrypt;

  public static byte[] enCrypt(byte[] srcData) {
    return doReverseCrypt(() -> reverseCrypt.enCrypt(srcData));
  }

  public static String enCrypt(String srcData) {
    return doReverseCrypt(() -> reverseCrypt.enCrypt(srcData));
  }

  public static byte[] deCrypt(byte[] targetData) {
    return doReverseCrypt(() -> reverseCrypt.deCrypt(targetData));
  }

  public static String deCrypt(String targetData) {
    return doReverseCrypt(() -> reverseCrypt.deCrypt(targetData));
  }

  public static String deCrypt(String targetData, String privateKey) {
    return doReverseCrypt(() -> reverseCrypt.deCrypt(targetData, privateKey));
  }

  public static byte[] deCrypt(byte[] targetData, String privateKey) {
    return doReverseCrypt(() -> reverseCrypt.deCrypt(targetData, privateKey));
  }

  public static boolean signatureVerify(byte[] srcData, byte[] targetData) {
    return doReverseCrypt(
        () -> reverseCrypt.signatureVerify(srcData, targetData));
  }


  public static boolean signatureVerify(byte[] srcData, byte[] targetData,
      String privateKey) {
    return doReverseCrypt(() -> doReverseCrypt(
        () -> reverseCrypt.signatureVerify(srcData, targetData, privateKey)));
  }

  public static String getDefaultPrivateKey() {
    return doReverseCrypt(() -> reverseCrypt.getDefaultPrivateKey());
  }

  public static String getDefaultPublicKey() {
    return doReverseCrypt(() -> reverseCrypt.getDefaultPublicKey());
  }

  public static Map<String, String> generateKey() throws Exception {
    if (null == reverseCrypt) {
      reverseCrypt = ReverseCrypt.getInstance();
    }
    return reverseCrypt.generateKey();
  }

  public static Map<String, String> generateKey(int length) throws Exception {
    if (null == reverseCrypt) {
      reverseCrypt = ReverseCrypt.getInstance();
    }
    return reverseCrypt.generateKey(length);
  }

  public static String enCrypt(String srcData, String publicKey) {
    return doReverseCrypt(() -> reverseCrypt.enCrypt(srcData, publicKey));
  }

  public static byte[] enCrypt(byte[] srcData, String publicKey) {
    return doReverseCrypt(() -> reverseCrypt.enCrypt(srcData, publicKey));
  }

  public static boolean signatureVerify(String srcData, String targetData,
      String privateKey) {
    return doReverseCrypt(
        () -> reverseCrypt.signatureVerify(srcData, targetData, privateKey));
  }

  public static boolean signatureVerify(String srcData, String targetData) {
    return doReverseCrypt(
        () -> reverseCrypt.signatureVerify(srcData, targetData));
  }

  private static <T> T doReverseCrypt(Supplier<T> reverseCryptSupplier) {
    if (null == reverseCrypt) {
      reverseCrypt = ReverseCrypt.getInstance();
    }
    return reverseCryptSupplier.get();
  }

}
