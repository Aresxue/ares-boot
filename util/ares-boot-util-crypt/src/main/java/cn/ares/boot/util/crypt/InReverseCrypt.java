package cn.ares.boot.util.crypt;

import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.MD5;

import cn.ares.boot.util.common.ExceptionUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.crypt.constant.CryptAlgorithm;
import cn.ares.boot.util.crypt.impl.HMacSm3;
import cn.ares.boot.util.crypt.impl.Md5;
import cn.ares.boot.util.crypt.impl.Sha1;
import cn.ares.boot.util.crypt.impl.Sha256;
import cn.ares.boot.util.crypt.impl.Sm3;
import cn.ares.boot.util.crypt.spi.DefaultSaltGetService;
import java.util.Arrays;
import java.util.Base64;
import java.util.ServiceLoader;

/**
 * @author: Ares
 * @time: 2021-11-22 20:50:00
 * @description: InReverse crypt
 * @description: 不可逆算法
 * @version: JDK 1.8
 */
public abstract class InReverseCrypt extends AbstractCrypt {

  public static final String IN_REVERSE_IMPL = "ares.crypt.in-reverse.impl";
  protected static String SALT = null;

  /*
   * 默认盐值获取服务
   */
  private final ServiceLoader<DefaultSaltGetService> defaultSaltGetServices = ServiceLoader.load(
      DefaultSaltGetService.class);

  /**
   * @author: Ares
   * @description: get default inReverse crypt instance
   * @description: 获取默认的不可逆算法加密实例
   * @time: 2021-12-23 20:55:20
   * @params: []
   * @return: InReverseCrypt 不可逆算法加密实例
   */
  protected static InReverseCrypt getInstance() {
    String cryptAlgorithmName = System.getProperty(IN_REVERSE_IMPL, MD5.getName());
    CryptAlgorithm cryptAlgorithm = CryptAlgorithm.getCryptAlgorithm(cryptAlgorithmName);
    return getInstance(cryptAlgorithm);
  }

  /**
   * @author: Ares
   * @description: get inReverse crypt instance by crypt algorithm
   * @description: 获取指定算法的不可逆算法加密实例
   * @time: 2021-11-22 15:23:00
   * @params: [cryptAlgorithm] 算法
   * @return: InReverseCrypt 不可逆算法加密实例
   */
  public static InReverseCrypt getInstance(CryptAlgorithm cryptAlgorithm) {
    if (null == cryptAlgorithm) {
      throw new RuntimeException("Crypt algorithm is null");
    }
    if (cryptAlgorithm.isReverse()) {
      throw new RuntimeException("Current crypt algorithm is reverse algorithm");
    }
    switch (cryptAlgorithm) {
      case SHA1:
        return Sha1.getInstance();
      case SHA256:
        return Sha256.getInstance();
      case SM3:
        return Sm3.getInstance();
      case HMAC_SM3:
        return HMacSm3.getInstance();
      default:
        return Md5.getInstance();
    }
  }

  /**
   * @author: Ares
   * @description: Encrypt data with default salt
   * @description: 使用默认盐加密数据
   * @time: 2021-11-22 12:41:00
   * @params: [srcData] 待加密byte数组
   * @return: byte[] 加密后byte数组
   */
  public byte[] enCrypt(byte[] srcData) {
    return enCrypt(srcData, getDefaultSalt());
  }

  /**
   * @author: Ares
   * @description: Encrypt data with default salt
   * @description: 使用默认盐加密数据
   * @time: 2021-11-23 13:07:00
   * @params: [srcData] 待加密字符串
   * @return: java.lang.String 加密后字符串
   */
  public String enCrypt(String srcData) {
    return enCrypt(srcData, getDefaultSalt());
  }


  /**
   * @author: Ares
   * @description: Use the default salt to verify the signature
   * @description: 使用默认盐验签
   * @time: 2021-11-22 12:42:00
   * @params: [srcData, targetData] 待加密byte数组，目标byte数组
   * @return: boolean 验签结果
   */
  @Override
  public boolean signatureVerify(byte[] srcData, byte[] targetData) {
    return signatureVerify(srcData, targetData, getDefaultSalt());
  }

  /**
   * @author: Ares
   * @description: Use the salt to verify the signature
   * @description: 加盐验签
   * @time: 2021-11-22 12:42:00
   * @params: [srcData, targetData, salt] 待加密byte数组，目标byte数组，盐
   * @return: boolean response
   */
  @Override
  public boolean signatureVerify(byte[] srcData, byte[] targetData, String salt) {
    validSignatureData(srcData, targetData, salt);

    byte[] enCryptBytes = ExceptionUtil.get(() -> enCryptImpl(srcData, salt));
    // base64
    targetData = Base64.getDecoder().decode(targetData);
    return Arrays.equals(targetData, enCryptBytes);
  }


  /**
   * @author: Ares
   * @description: Get default salt
   * @description: 获取默认盐值
   * @time: 2021-11-22 12:41:00
   * @params: []
   * @return: byte[] 盐
   */
  public String getDefaultSalt() {
    if (null == SALT) {
      for (DefaultSaltGetService defaultSaltGetService : defaultSaltGetServices) {
        try {
          SALT = defaultSaltGetService.getDefaultSalt();
          if (StringUtil.isNotEmpty(SALT)) {
            break;
          }
        } catch (Exception ignore) {
        }
      }
      if (StringUtil.isEmpty(SALT)) {
        throw new RuntimeException(
            "Get default salt don't found available cn.ares.boot.util.crypt.impl");
      }
    }
    return SALT;
  }

  /**
   * @author: Ares
   * @description: Generate salt
   * @description: 生成盐值
   * @time: 2021-11-22 17:39:00
   * @params: []
   * @return: java.lang.String 盐
   */
  public String generateSalt() {
    return AbstractCrypt.bytesToString(AbstractCrypt.generateSaltInner());
  }

  /**
   * @author: Ares
   * @description: Generates a salt value of the specified length
   * @description: 生成指定长度的盐值
   * @time: 2021-11-22 17:39:00
   * @params: [numBytes] num bytes
   * @params: [numBytes] 指定使用多少位的byte
   * @return: java.lang.String 盐
   */
  public String generateSalt(int numBytes) {
    return AbstractCrypt.bytesToString(AbstractCrypt.generateSaltInner(numBytes));
  }

}
