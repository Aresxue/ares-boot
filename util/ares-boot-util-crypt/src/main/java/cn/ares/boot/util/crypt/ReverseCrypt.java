package cn.ares.boot.util.crypt;

import static cn.ares.boot.util.common.StringUtil.isEmpty;
import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.AES256;
import static java.nio.charset.StandardCharsets.US_ASCII;

import cn.ares.boot.util.common.ExceptionUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.crypt.constant.CryptAlgorithm;
import cn.ares.boot.util.crypt.impl.Aes256;
import cn.ares.boot.util.crypt.impl.Des;
import cn.ares.boot.util.crypt.impl.Rsa;
import cn.ares.boot.util.crypt.impl.Sm4;
import cn.ares.boot.util.crypt.spi.DefaultKeyGetService;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Ares
 * @time: 2021-11-22 20:50:00
 * @description: Reverse crypt
 * @description: 可逆算法
 * @version: JDK 1.8
 */
public abstract class ReverseCrypt extends AbstractCrypt {

  public static final String REVERSE_IMPL = "ares.crypt.reverse.impl";

  /**
   * 魔法数, 校验前缀
   */
  protected static final byte[] MAGIC_NUMBER = "kele".getBytes(US_ASCII);

  protected static final String PUBLIC_KEY_NAME = "publicKey";
  protected static final String PRIVATE_KEY_NAME = "privateKey";

  private static final Map<String, String> KEY_MAP = new ConcurrentHashMap<>();

  /*
   * 默认公私钥获取服务
   */
  private final ServiceLoader<DefaultKeyGetService> defaultKeyGetServices = ServiceLoader.load(
      DefaultKeyGetService.class);

  /**
   * @author: Ares
   * @description: Get default reverse crypt instance
   * @description: 获取默认的可逆算法加密实例
   * @time: 2021-12-23 20:55:55
   * @params: []
   * @return: ReverseCrypt 可逆算法加密实例
   */
  protected static ReverseCrypt getInstance() {
    String cryptAlgorithmName = System.getProperty(REVERSE_IMPL, AES256.getName());
    CryptAlgorithm cryptAlgorithm = CryptAlgorithm.getCryptAlgorithm(cryptAlgorithmName);
    return getInstance(cryptAlgorithm);
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
    switch (cryptAlgorithm) {
      case DES:
        return Des.getInstance();
      case RSA:
        return Rsa.getInstance();
      case SM4:
        return Sm4.getInstance();
      default:
        return Aes256.getInstance();
    }
  }

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
    byte[] dataBytes = null == targetData ? null : targetData.getBytes(Charset.defaultCharset());
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
   * @return: byte[] 解密后byte数组
   */
  public byte[] deCrypt(byte[] targetData, String privateKey) {
    if (null == targetData || targetData.length == 0) {
      throw new RuntimeException("Target data is null or empty");
    }
    if (null == privateKey) {
      throw new RuntimeException("Private key is null");
    }
    // base64
    byte[] finalTargetData = Base64.getDecoder().decode(targetData);
    return ExceptionUtil.get(() -> deCryptImpl(finalTargetData, privateKey));
  }

  /**
   * @author: Ares
   * @description: Verify signature with private key
   * @description: 使用私钥验签
   * @time: 2021-11-22 12:42:00
   * @params: [srcData, targetData, publicKey] 待解密byte数组，目标byte数组，私钥
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
   * @params: [targetData, privateKey] 待解密byte数组，私钥
   * @return: byte[] 解密后byte数组
   */
  public abstract byte[] deCryptImpl(byte[] targetData, String privateKey) throws Exception;

  /**
   * @author: Ares
   * @description: Generate a key of specified length
   * @description: 生成指定长度的秘钥
   * @time: 2021-11-23 13:46:00
   * @params: [length] 指定长度
   * @return: java.util.Map<java.lang.String, java.lang.String> 秘钥
   */
  public abstract Map<String, String> generateKey(int length) throws Exception;

  /**
   * @author: Ares
   * @description: Generate a key
   * @description: 生成秘钥
   * @time: 2021-11-23 13:46:00
   * @params: []
   * @return: java.util.Map<java.lang.String, java.lang.String> 秘钥
   */
  public abstract Map<String, String> generateKey() throws Exception;

  /**
   * @author: Ares
   * @description: Get default publicKey
   * @description: 获取默认公钥
   * @time: 2021-11-22 16:51:00
   * @params: []
   * @return: java.lang.String 公钥
   */
  public String getDefaultPublicKey() {
    String publicKey = getPublicKey();
    if (null == publicKey) {
      for (DefaultKeyGetService defaultKeyGetService : defaultKeyGetServices) {
        try {
          publicKey = defaultKeyGetService.getDefaultPublicKey();
          if (StringUtil.isNotEmpty(publicKey)) {
            break;
          }
        } catch (Exception ignore) {
        }
      }
      if (isEmpty(publicKey)) {
        throw new RuntimeException(
            "Get default publicKey don't found available cn.ares.boot.util.crypt.impl");
      }
      putPublicKey(publicKey);
    }
    return publicKey;
  }

  /**
   * @author: Ares
   * @description: Get default private key
   * @description: 获取默认私钥
   * @time: 2021-11-22 17:01:00
   * @params: []
   * @return: java.lang.String 私钥
   */
  public String getDefaultPrivateKey() {
    String privateKey = getPrivateKey();
    if (null == privateKey) {
      for (DefaultKeyGetService defaultKeyGetService : defaultKeyGetServices) {
        try {
          privateKey = defaultKeyGetService.getDefaultPrivateKey();
          if (StringUtil.isNotEmpty(privateKey)) {
            break;
          }
        } catch (Exception ignore) {
        }
      }
      if (isEmpty(privateKey)) {
        throw new RuntimeException(
            "Get default privateKey don't found available cn.ares.boot.util.crypt.impl");
      }
      putPrivateKey(privateKey);
    }
    return privateKey;
  }

  protected String getPublicKey() {
    return KEY_MAP.get(PUBLIC_KEY_NAME);
  }

  protected String getPrivateKey() {
    return KEY_MAP.get(PRIVATE_KEY_NAME);
  }

  protected void putPublicKey(String publicKey) {
    KEY_MAP.putIfAbsent(PUBLIC_KEY_NAME, publicKey);
  }

  protected void putPrivateKey(String privateKey) {
    KEY_MAP.putIfAbsent(PRIVATE_KEY_NAME, privateKey);
  }

}
