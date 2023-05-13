package cn.ares.boot.util.crypt.impl;

import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.common.primitive.ByteUtil;
import cn.ares.boot.util.crypt.AbstractCrypt;
import cn.ares.boot.util.crypt.ReverseCrypt;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author: Ares
 * @time: 2021-11-23 14:45:00
 * @description: Des
 * @version: JDK 1.8
 */
public class Des extends ReverseCrypt {

  private static final int DEFAULT_KEY_LENGTH = 8;

  private static final String KEY_ALGORITHM = "DES";

  @Override
  public byte[] enCryptImpl(byte[] srcData, String password) throws Exception {
    SecureRandom random = new SecureRandom();
    DESKeySpec desKey = new DESKeySpec(password.getBytes());
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
    SecretKey secretKey = keyFactory.generateSecret(desKey);
    Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, random);
    return cipher.doFinal(ByteUtil.rightPaddingZero(srcData, 8));
  }


  @Override
  public byte[] deCryptImpl(byte[] targetData, String password) throws Exception {
    SecureRandom random = new SecureRandom();
    DESKeySpec desKey = new DESKeySpec((password.getBytes()));
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
    SecretKey secretKey = keyFactory.generateSecret(desKey);
    Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
    cipher.init(Cipher.DECRYPT_MODE, secretKey, random);
    return cipher.doFinal(targetData);
  }

  @Override
  public Map<String, String> generateKey(int length) throws Exception {
    Map<String, String> keyMap = new HashMap<>(4);

    String randomStr = StringUtil.random(length);
    DESKeySpec desKeySpec = new DESKeySpec(randomStr.getBytes(Charset.defaultCharset()));
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
    secretKeyFactory.generateSecret(desKeySpec);
    String password = AbstractCrypt.bytesToString(desKeySpec.getKey());

    keyMap.put(PUBLIC_KEY_NAME, password);
    keyMap.put(PRIVATE_KEY_NAME, password);
    return keyMap;
  }

  @Override
  public Map<String, String> generateKey() throws Exception {
    return generateKey(DEFAULT_KEY_LENGTH);
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
