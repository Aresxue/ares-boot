package cn.ares.boot.util.crypt;

import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.AES256;
import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.DES;
import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.RSA;
import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.SM4;

import cn.ares.boot.util.common.MapUtil;
import cn.ares.boot.util.common.structure.Tuple;
import cn.ares.boot.util.crypt.constant.CryptAlgorithm;
import cn.ares.boot.util.crypt.impl.Aes256;
import cn.ares.boot.util.crypt.impl.Des;
import cn.ares.boot.util.crypt.impl.Rsa;
import cn.ares.boot.util.crypt.impl.Sm4;
import java.util.Map;

/**
 * @author: Ares
 * @time: 2021-12-23 21:08:52
 * @description: Reverse crypt util
 * @description: 可逆加密算法工具
 * @version: JDK 1.8
 */
public class ReverseCryptUtil {

  private static final String REVERSE_IMPL = "ares.crypt.reverse.impl";
  private static final Map<CryptAlgorithm, ReverseCrypt> REVERSE_CRYPT_MAP = MapUtil.newConcurrentMap();
  private static ReverseCrypt reverseCrypt;

  static {
    REVERSE_CRYPT_MAP.put(AES256, Aes256.getInstance());
    REVERSE_CRYPT_MAP.put(DES, Des.getInstance());
    REVERSE_CRYPT_MAP.put(RSA, Rsa.getInstance());
    REVERSE_CRYPT_MAP.put(SM4, Sm4.getInstance());

    setReverseCryptInstance();
  }

  private static void setReverseCryptInstance() {
    String cryptAlgorithmName = System.getProperty(REVERSE_IMPL, AES256.getName());
    CryptAlgorithm cryptAlgorithm = CryptAlgorithm.getCryptAlgorithm(cryptAlgorithmName);
    reverseCrypt = getInstance(cryptAlgorithm);
  }

  public static byte[] enCrypt(byte[] srcData) {
    return reverseCrypt.enCrypt(srcData);
  }

  public static String enCrypt(String srcData) {
    return reverseCrypt.enCrypt(srcData);
  }

  public static byte[] deCrypt(byte[] targetData) {
    return reverseCrypt.deCrypt(targetData);
  }

  public static String deCrypt(String targetData) {
    return reverseCrypt.deCrypt(targetData);
  }

  public static String deCrypt(String targetData, String privateKey) {
    return reverseCrypt.deCrypt(targetData, privateKey);
  }

  public static byte[] deCrypt(byte[] targetData, String privateKey) {
    return reverseCrypt.deCrypt(targetData, privateKey);
  }

  public static boolean signatureVerify(byte[] srcData, byte[] targetData) {
    return reverseCrypt.signatureVerify(srcData, targetData);
  }


  public static boolean signatureVerify(byte[] srcData, byte[] targetData, String privateKey) {
    return reverseCrypt.signatureVerify(srcData, targetData, privateKey);
  }

  public static String getDefaultPrivateKey() {
    return reverseCrypt.getDefaultPrivateKey();
  }

  public static String getDefaultPublicKey() {
    return reverseCrypt.getDefaultPublicKey();
  }

  public static Tuple<String, String> generateKey() throws Exception {
    return reverseCrypt.generateKey();
  }

  public static String enCrypt(String srcData, String publicKey) {
    return reverseCrypt.enCrypt(srcData, publicKey);
  }

  public static byte[] enCrypt(byte[] srcData, String publicKey) {
    return reverseCrypt.enCrypt(srcData, publicKey);
  }

  public static boolean signatureVerify(String srcData, String targetData,
      String privateKey) {
    return reverseCrypt.signatureVerify(srcData, targetData, privateKey);
  }

  public static boolean signatureVerify(String srcData, String targetData) {
    return reverseCrypt.signatureVerify(srcData, targetData);
  }

  /**
   * @author: Ares
   * @description: Get default reverse crypt instance
   * @description: 获取可逆算法加密实例
   * @time: 2021-11-22 14:06:00
   * @params: [cryptAlgorithm] 算法
   * @return: ReverseCrypt 可逆算法加密实例
   */
  public static ReverseCrypt getInstance(CryptAlgorithm cryptAlgorithm) {
    if (null == cryptAlgorithm) {
      throw new RuntimeException("Crypt algorithm is null");
    }
    if (!cryptAlgorithm.isReverse()) {
      throw new RuntimeException("Current crypt algorithm is not reverse algorithm");
    }
    return REVERSE_CRYPT_MAP.get(cryptAlgorithm);
  }

  public static boolean registerAlgorithm(CryptAlgorithm algorithm, ReverseCrypt crypt) {
    if (null == algorithm) {
      return false;
    }
    REVERSE_CRYPT_MAP.putIfAbsent(algorithm, crypt);
    setReverseCryptInstance();
    return true;
  }

  public static void setReverseCrypt(ReverseCrypt reverseCrypt) {
    ReverseCryptUtil.reverseCrypt = reverseCrypt;
  }

}
