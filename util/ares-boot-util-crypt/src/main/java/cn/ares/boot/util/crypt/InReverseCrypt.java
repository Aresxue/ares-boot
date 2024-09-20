package cn.ares.boot.util.crypt;

import cn.ares.boot.util.common.ExceptionUtil;
import java.util.Arrays;

/**
 * @author: Ares
 * @time: 2021-11-22 20:50:00
 * @description: 不可逆加密算法
 * @description: InReverse crypt
 * @version: JDK 1.8
 */
public abstract class InReverseCrypt extends AbstractCrypt {

  /**
   * @author: Ares
   * @description: Encrypt data with default salt
   * @description: 使用默认盐加密数据
   * @time: 2021-11-22 12:41:00
   * @params: [srcData] 待加密字节数组
   * @return: byte[] 加密后字节数组
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
   * @params: [srcData, targetData] 待加密字节数组，目标字节数组
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
   * @params: [srcData, targetData, salt] 待加密字节数组，目标字节数组，盐
   * @return: boolean response
   */
  @Override
  public boolean signatureVerify(byte[] srcData, byte[] targetData, String salt) {
    validSignatureData(srcData, targetData, salt);

    byte[] enCryptBytes = ExceptionUtil.get(() -> enCryptImpl(srcData, salt));
    return Arrays.equals(targetData, enCryptBytes);
  }


  /**
   * @author: Ares
   * @description: Get default salt
   * @description: 获取默认盐值
   * @time: 2021-11-22 12:41:00
   * @return: byte[] 盐
   */
  public String getDefaultSalt() {
    return "";
  }

  /**
   * @author: Ares
   * @description: Generate salt
   * @description: 生成盐值
   * @time: 2021-11-22 17:39:00
   * @return: java.lang.String 盐
   */
  public String generateSalt() {
    return bytesToString(AbstractCrypt.generateSaltInner());
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
    return bytesToString(AbstractCrypt.generateSaltInner(numBytes));
  }

}
