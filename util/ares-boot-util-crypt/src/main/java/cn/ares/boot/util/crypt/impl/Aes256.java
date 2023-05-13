package cn.ares.boot.util.crypt.impl;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

import cn.ares.boot.util.common.primitive.ByteUtil;
import cn.ares.boot.util.crypt.AbstractCrypt;
import cn.ares.boot.util.crypt.ReverseCrypt;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: Ares
 * @time: 2021-11-22 12:56:00
 * @description: Aes, MessageDigest and Cipher is not thread safe
 * @version: JDK 1.8
 */
public class Aes256 extends ReverseCrypt {

  private static final String KEY_ALGORITHM = "AES";

  /**
   * 加密次数
   */
  private static final int CRYPT_TIMES = 3;
  private static final int SALT_LENGTH = 8;
  private static final int DATA_START = MAGIC_NUMBER.length + SALT_LENGTH;

  public static final int DEFAULT_KEY_LENGTH = 128;

  @Override
  public byte[] enCryptImpl(byte[] srcData, String publicKey) throws Exception {
    byte[] salt = AbstractCrypt.generateSaltInner(SALT_LENGTH);

    Cipher cipher = initCipher(publicKey, salt, ENCRYPT_MODE);

    byte[] enCryptBytes = cipher.doFinal(srcData);
    return ByteUtil.merge(MAGIC_NUMBER, salt, enCryptBytes);
  }

  @Override
  public byte[] deCryptImpl(byte[] targetData, String privateKey) throws Exception {
    byte[] salt = Arrays.copyOfRange(targetData, MAGIC_NUMBER.length, DATA_START);

    if (!Arrays.equals(Arrays.copyOfRange(targetData, 0, MAGIC_NUMBER.length), MAGIC_NUMBER)) {
      throw new IllegalArgumentException("Invalid crypt data");
    }

    Cipher cipher = initCipher(privateKey, salt, DECRYPT_MODE);
    return cipher.doFinal(targetData, DATA_START, targetData.length - DATA_START);
  }

  @Override
  public Map<String, String> generateKey(int length) throws Exception {
    Map<String, String> result = new HashMap<>(4);
    KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);

    SecureRandom secureRandom = new SecureRandom(MAGIC_NUMBER);
    keyGenerator.init(DEFAULT_KEY_LENGTH, secureRandom);
    SecretKey secretKey = keyGenerator.generateKey();
    byte[] bytes = secretKey.getEncoded();
    String str = AbstractCrypt.bytesToString(bytes);

    result.put(PUBLIC_KEY_NAME, str);
    result.put(PRIVATE_KEY_NAME, str);
    return result;
  }

  @Override
  public Map<String, String> generateKey() throws Exception {
    return generateKey(DEFAULT_KEY_LENGTH);
  }

  @Override
  public String getDefaultPublicKey() {
    return super.getDefaultPublicKey();
  }

  @Override
  public String getDefaultPrivateKey() {
    return super.getDefaultPrivateKey();
  }

  private Cipher initCipher(String key, byte[] salt, int mode) throws Exception {
    byte[] keyBytes = key.getBytes(Charset.defaultCharset());
    byte[][] skAndIv = generateSkAndIv(keyBytes, salt);

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec secretKeySpec = new SecretKeySpec(skAndIv[0], KEY_ALGORITHM);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(skAndIv[1]);
    cipher.init(mode, secretKeySpec, ivParameterSpec);
    return cipher;
  }

  private static byte[][] generateSkAndIv(byte[] key, byte[] salt) throws Exception {
    final MessageDigest md5 = MessageDigest.getInstance("MD5");
    final byte[] finalSalt = ByteUtil.merge(key, salt);
    byte[] dx = new byte[0];
    byte[] di = new byte[0];

    for (int i = 0; i < CRYPT_TIMES; i++) {
      di = md5.digest(ByteUtil.merge(di, finalSalt));
      dx = ByteUtil.merge(dx, di);
    }

    return new byte[][]{Arrays.copyOfRange(dx, 0, 32), Arrays.copyOfRange(dx, 32, 48)};
  }


  private static class LazyHolder {

    private static final Aes256 INSTANCE = new Aes256();
  }

  private Aes256() {
  }

  public static Aes256 getInstance() {
    return LazyHolder.INSTANCE;
  }

}
