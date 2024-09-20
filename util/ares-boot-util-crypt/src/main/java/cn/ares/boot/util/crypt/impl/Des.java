package cn.ares.boot.util.crypt.impl;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

import cn.ares.boot.util.common.structure.Tuple;
import cn.ares.boot.util.crypt.ReverseCrypt;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: Ares
 * @time: 2021-11-23 14:45:00
 * @description: Des
 * @version: JDK 1.8
 */
public class Des extends ReverseCrypt {

  private static final String KEY_ALGORITHM = "DES";
  private static final int DEFAULT_KEY_LENGTH = 56;

  @Override
  public byte[] enCryptImpl(byte[] srcData, String password) throws Exception {
    Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
    SecretKeySpec secretKeySpec = new SecretKeySpec(stringToBytes(password), KEY_ALGORITHM);
    cipher.init(ENCRYPT_MODE, secretKeySpec);
    return cipher.doFinal(srcData);
  }


  @Override
  public byte[] deCryptImpl(byte[] targetData, String password) throws Exception {
    Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
    SecretKeySpec secretKeySpec = new SecretKeySpec(stringToBytes(password), KEY_ALGORITHM);
    cipher.init(DECRYPT_MODE, secretKeySpec);
    return cipher.doFinal(targetData);
  }

  @Override
  public Tuple<String, String> generateKey() throws Exception {
    KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
    keyGenerator.init(DEFAULT_KEY_LENGTH);
    SecretKey secretKey = keyGenerator.generateKey();
    String secretKeyStr = bytesToString(secretKey.getEncoded());
    return Tuple.of(secretKeyStr, secretKeyStr);
  }


  private static class LazyHolder {

    private static final Des INSTANCE = new Des();
  }

  private Des() {
  }

  public static Des getInstance() {
    return LazyHolder.INSTANCE;
  }

}
