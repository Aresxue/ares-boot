package cn.ares.boot.util.crypt;

import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.HMAC_SM3;
import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.MD5;
import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.SHA1;
import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.SHA256;
import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.SM3;

import cn.ares.boot.util.common.MapUtil;
import cn.ares.boot.util.crypt.constant.CryptAlgorithm;
import cn.ares.boot.util.crypt.impl.HMacSm3;
import cn.ares.boot.util.crypt.impl.Md5;
import cn.ares.boot.util.crypt.impl.Sha1;
import cn.ares.boot.util.crypt.impl.Sha256;
import cn.ares.boot.util.crypt.impl.Sm3;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * @author: Ares
 * @time: 2021-12-23 21:08:52
 * @description: InReverse crypt util
 * @description: 不可逆加密算法工具
 * @version: JDK 1.8
 */
public class InReverseCryptUtil {

  private static final String IN_REVERSE_IMPL = "ares.crypt.in-reverse.impl";
  private static final Map<CryptAlgorithm, InReverseCrypt> IN_REVERSE_CRYPT_MAP = MapUtil.newConcurrentMap();
  private static InReverseCrypt inReverseCrypt;

  static {
    IN_REVERSE_CRYPT_MAP.put(MD5, Md5.getInstance());
    IN_REVERSE_CRYPT_MAP.put(SHA1, Sha1.getInstance());
    IN_REVERSE_CRYPT_MAP.put(SHA256, Sha256.getInstance());
    IN_REVERSE_CRYPT_MAP.put(SM3, Sm3.getInstance());
    IN_REVERSE_CRYPT_MAP.put(HMAC_SM3, HMacSm3.getInstance());

    setInReverseCryptInstance();
  }

  private static void setInReverseCryptInstance() {
    String cryptAlgorithmName = System.getProperty(IN_REVERSE_IMPL, MD5.getName());
    CryptAlgorithm cryptAlgorithm = CryptAlgorithm.getCryptAlgorithm(cryptAlgorithmName);
    inReverseCrypt = getInstance(cryptAlgorithm);
  }

  public static byte[] enCrypt(byte[] srcData) {
    return inReverseCrypt.enCrypt(srcData);
  }

  public static String enCrypt(String srcData) {
    return inReverseCrypt.enCrypt(srcData);
  }

  public static boolean signatureVerify(byte[] srcData, byte[] targetData) {
    return inReverseCrypt.signatureVerify(srcData, targetData);
  }


  public static boolean signatureVerify(byte[] srcData, byte[] targetData,
      String salt) {
    return inReverseCrypt.signatureVerify(srcData, targetData, salt);
  }

  public static String getDefaultSalt() {
    return inReverseCrypt.getDefaultSalt();
  }

  public static String generateSalt() {
    return inReverseCrypt.generateSalt();
  }

  public static String generateSalt(int numBytes) {
    return inReverseCrypt.generateSalt(numBytes);
  }

  public static String enCrypt(String srcData, String salt) {
    return inReverseCrypt.enCrypt(srcData, salt);
  }

  public static byte[] enCrypt(byte[] srcData, String salt) {
    return inReverseCrypt.enCrypt(srcData, salt);
  }

  public static boolean signatureVerify(String srcData, String targetData,
      String privateKeyOrSalt) {
    return inReverseCrypt.signatureVerify(srcData, targetData, privateKeyOrSalt);
  }

  public static boolean signatureVerify(String srcData, String targetData) {
    return inReverseCrypt.signatureVerify(srcData, targetData);
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

  /**
   * @author: Ares
   * @description: get inReverse crypt instance by crypt algorithm
   * @description: 获取指定算法的不可逆算法加密实例
   * @time: 2021-11-22 15:23:00
   * @params: [cryptAlgorithm] 加密算法
   * @return: InReverseCrypt 不可逆算法加密实例
   */
  public static InReverseCrypt getInstance(CryptAlgorithm cryptAlgorithm) {
    if (null == cryptAlgorithm) {
      throw new RuntimeException("Crypt algorithm is null");
    }
    if (cryptAlgorithm.isReverse()) {
      throw new RuntimeException("Current crypt algorithm is reverse algorithm");
    }
    return IN_REVERSE_CRYPT_MAP.get(cryptAlgorithm);
  }


  /**
   * @description: 注册算法
   * @description: Register algorithm
   * @time: 2021-11-22 15:23:00
   * @params: [algorithm, crypt] 算法，加密实例
   * @return: boolean 是否注册成功
   */
  public static boolean registerAlgorithm(CryptAlgorithm algorithm, InReverseCrypt crypt) {
    if (null == algorithm) {
      return false;
    }
    IN_REVERSE_CRYPT_MAP.putIfAbsent(algorithm, crypt);
    setInReverseCryptInstance();
    return true;
  }

  public static void setInReverseCrypt(InReverseCrypt inReverseCrypt) {
    InReverseCryptUtil.inReverseCrypt = inReverseCrypt;
  }

}
