package cn.ares.boot.util.crypt;

import cn.ares.boot.util.common.ExceptionUtil;
import cn.ares.boot.util.common.primitive.ByteUtil;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author: Ares
 * @time: 2021-11-22 13:19:00
 * @description: Abstract crypt
 * @version: JDK 1.8
 */
public abstract class AbstractCrypt {

  /**
   * Salt byte array length
   */
  private static final int SALT_LENGTH = 16;

  /**
   * @author: Ares
   * @description: Encrypt data with public key or salt in default encoding
   * @description: 以默认编码使用公钥或盐加密数据
   * @time: 2022-06-08 18:24:18
   * @params: [srcData, publicKeyOrSalt] 待加密字符串，公钥或盐
   * @return: java.lang.String 加密后字符串
   */
  public String enCrypt(String srcData, String publicKeyOrSalt) {
    byte[] dataBytes = null == srcData ? null : srcData.getBytes(Charset.defaultCharset());
    byte[] result = enCrypt(dataBytes, publicKeyOrSalt);
    if (null == result) {
      return null;
    }
    return new String(result, Charset.defaultCharset());
  }

  /**
   * @author: Ares
   * @description: Encrypt data with public key or salt
   * @description: 使用公钥或盐加密数据
   * @time: 2021-11-22 12:51:00
   * @params: [srcData, publicKey] 待加密byte数组，公钥或盐
   * @return: byte[] 加密后byte数组
   */
  public byte[] enCrypt(byte[] srcData, String publicKeyOrSalt) {
    if (null == srcData || srcData.length == 0) {
      throw new RuntimeException("Src data is null or empty");
    }
    if (null == publicKeyOrSalt) {
      throw new RuntimeException("Public key is null");
    }
    byte[] result = ExceptionUtil.get(() -> enCryptImpl(srcData, publicKeyOrSalt));

    // base64
    return Base64.getEncoder().encode(result);
  }

  /**
   * @author: Ares
   * @description: Verify signature with private key or salt (compare byte array)
   * @description: 使用私钥或盐验签（比对byte数组）
   * @time: 2021-11-23 13:02:00
   * @params: [srcData, targetData, privateKeyOrSalt] 待解密字符串，目标字符串，私钥或盐
   * @return: boolean 验签结果
   */
  public boolean signatureVerify(String srcData, String targetData, String privateKeyOrSalt) {
    byte[] srcDataBytes = null == srcData ? null : srcData.getBytes(Charset.defaultCharset());
    byte[] targetBytes = null == targetData ? null : targetData.getBytes(Charset.defaultCharset());

    return ExceptionUtil.get(() -> signatureVerify(srcDataBytes, targetBytes, privateKeyOrSalt));
  }

  /**
   * @author: Ares
   * @description: Verify signature with default private key or salt
   * @description: 使用默认私钥或盐验签（比对byte数组）
   * @time: 2021-11-23 13:02:00
   * @params: [srcData, targetData] 待解密字符串，目标字符串
   * @return: boolean 验签结果
   */
  public boolean signatureVerify(String srcData, String targetData) {
    byte[] srcDataBytes = null == srcData ? null : srcData.getBytes(Charset.defaultCharset());
    byte[] targetBytes = null == targetData ? null : targetData.getBytes(Charset.defaultCharset());

    return signatureVerify(srcDataBytes, targetBytes);
  }

  /**
   * @author: Ares
   * @description: Encrypt with public key or salt
   * @description: 使用公钥或盐加密
   * @time: 2021-11-22 12:43:00
   * @params: [srcData, publicKeyOrSalt] 待加密byte数组，公钥或盐
   * @return: byte[] 加密后byte数组
   */
  public abstract byte[] enCryptImpl(byte[] srcData, String publicKeyOrSalt) throws Exception;

  /**
   * @author: Ares
   * @description: Verify signature with private key or salt (compare byte array)
   * @description: 使用私钥或盐验签（比对byte数组）
   * @time: 2021-11-23 13:28:00
   * @params: [srcData, targetData, privateKeyOrSalt] 待解密byte数组，目标byte数组，私钥或盐
   * @return: boolean 验签结果
   */
  public abstract boolean signatureVerify(byte[] srcData, byte[] targetData,
      String privateKeyOrSalt) throws Exception;

  /**
   * @author: Ares
   * @description: Verify signatures with default public key or salt
   * @description: 使用默认公钥或盐验签
   * @time: 2021-11-23 13:28:00
   * @params: [srcData, targetData,] 待解密byte数组，目标byte数组
   * @return: boolean response
   */
  public abstract boolean signatureVerify(byte[] srcData, byte[] targetData);

  protected static byte[] generateSaltInner(int numBytes) {
    SecureRandom secureRandom = new SecureRandom();
    return secureRandom.generateSeed(numBytes);
  }

  protected static byte[] generateSaltInner() {
    return generateSaltInner(SALT_LENGTH);
  }

  protected static String bytesToString(byte[] src) {
    return ByteUtil.bytesToHexString(src);
  }

  protected static byte[] stringToBytes(String src) {
    return ByteUtil.hexStringToBytes(src);
  }

  protected void validSignatureData(byte[] srcData, byte[] targetData, String privateKeyOrSalt) {
    if (null == srcData || srcData.length == 0) {
      throw new RuntimeException("Src data is null or empty");
    }
    if (null == targetData || targetData.length == 0) {
      throw new RuntimeException("Target data is null or empty");
    }
    if (null == privateKeyOrSalt) {
      throw new RuntimeException("private key or salt is null");
    }
  }

}
