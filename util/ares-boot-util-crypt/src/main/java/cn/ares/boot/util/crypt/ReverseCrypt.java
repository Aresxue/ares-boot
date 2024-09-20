package cn.ares.boot.util.crypt;

import cn.ares.boot.util.common.ExceptionUtil;
import cn.ares.boot.util.common.structure.Tuple;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author: Ares
 * @time: 2021-11-22 20:50:00
 * @description: 可逆加密算法
 * @description: Reverse crypt
 * @version: JDK 1.8
 */
public abstract class ReverseCrypt extends AbstractCrypt {

  public byte[] enCrypt(byte[] srcData) {
    return enCrypt(srcData, getDefaultPublicKey());
  }

  public String enCrypt(String srcData) {
    return enCrypt(srcData, getDefaultPublicKey());
  }

  public byte[] deCrypt(byte[] targetData) {
    return deCrypt(targetData, getDefaultPrivateKey());
  }

  public String deCrypt(String targetData) {
    return deCrypt(targetData, getDefaultPrivateKey());
  }

  /**
   * @author: Ares
   * @description: Decrypt data with private key in default encoding
   * @description: 以默认编码使用私钥解密数据
   * @time: 2022-06-08 18:38:59
   * @params: [targetData, privateKey] 待解密数据，私钥
   * @return: java.lang.String 解密数据
   */
  public String deCrypt(String targetData, String privateKey) {
    byte[] dataBytes = null == targetData ? null : stringToBytes(targetData);
    byte[] result = deCrypt(dataBytes, privateKey);
    if (null == result) {
      return null;
    }
    return new String(result, Charset.defaultCharset());
  }

  /**
   * @author: Ares
   * @description: Decrypt data using private key
   * @description: 使用私钥解密数据
   * @time: 2021-11-22 12:52:00
   * @params: [targetData, privateKey] 待解密数据，私钥
   * @return: byte[] 解密后字节数组
   */
  public byte[] deCrypt(byte[] targetData, String privateKey) {
    if (null == targetData || targetData.length == 0) {
      throw new RuntimeException("Target data is null or empty");
    }
    if (null == privateKey) {
      throw new RuntimeException("Private key is null");
    }
    return ExceptionUtil.get(() -> deCryptImpl(targetData, privateKey));
  }

  /**
   * @author: Ares
   * @description: Verify signature with private key
   * @description: 使用私钥验签
   * @time: 2021-11-22 12:42:00
   * @params: [srcData, targetData, publicKey] 待解密字节数组，目标字节数组，私钥
   * @return: boolean 验签结果
   */
  @Override
  public boolean signatureVerify(byte[] srcData, byte[] targetData, String privateKey) {
    validSignatureData(srcData, targetData, privateKey);
    byte[] enCryptBytes = deCrypt(targetData, privateKey);
    return Arrays.equals(srcData, enCryptBytes);
  }

  /**
   * @author: Ares
   * @description: 验签(使用默认私钥)
   * @time: 2021-11-22 12:42:00
   * @params: [srcData, targetData] request
   * @return: boolean response
   */
  @Override
  public boolean signatureVerify(byte[] srcData, byte[] targetData) {
    return signatureVerify(srcData, targetData, getDefaultPrivateKey());
  }


  /**
   * @author: Ares
   * @description: DeCrypt impl
   * @description: 解密实现
   * @time: 2021-11-22 12:43:00
   * @params: [targetData, privateKey] 待解密字节数组，私钥
   * @return: byte[] 解密后字节数组
   */
  public abstract byte[] deCryptImpl(byte[] targetData, String privateKey) throws Exception;

  /**
   * @author: Ares
   * @description: Generate a key
   * @description: 生成秘钥
   * @time: 2021-11-23 13:46:00
   * @return: Tuple<String, String> 秘钥
   */
  public abstract Tuple<String, String> generateKey() throws Exception;

  /**
   * @author: Ares
   * @description: Get default publicKey
   * @description: 获取默认公钥
   * @time: 2021-11-22 16:51:00
   * @return: java.lang.String 公钥
   */
  public String getDefaultPublicKey() {
    throw new UnsupportedOperationException(
        "Please specify the default public key acquisition method");
  }

  /**
   * @author: Ares
   * @description: Get default private key
   * @description: 获取默认私钥
   * @time: 2021-11-22 17:01:00
   * @return: java.lang.String 私钥
   */
  public String getDefaultPrivateKey() {
    throw new UnsupportedOperationException(
        "Please specify the default private key acquisition method");
  }

}
