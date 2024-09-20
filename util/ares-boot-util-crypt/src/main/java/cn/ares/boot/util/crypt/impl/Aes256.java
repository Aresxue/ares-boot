package cn.ares.boot.util.crypt.impl;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

import cn.ares.boot.util.common.primitive.ByteUtil;
import cn.ares.boot.util.common.structure.Tuple;
import cn.ares.boot.util.crypt.ReverseCrypt;
import java.security.SecureRandom;
import java.util.Arrays;
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

  private static final int DEFAULT_KEY_LENGTH = 256;
  private static final int IV_PARAMETER_SPEC_BYTE_LENGTH = 16;

  @Override
  public byte[] enCryptImpl(byte[] srcData, String publicKey) throws Exception {
    Cipher cipher = initCipher(publicKey, ENCRYPT_MODE);
    return cipher.doFinal(srcData);
  }

  @Override
  public byte[] deCryptImpl(byte[] targetData, String privateKey) throws Exception {
    Cipher cipher = initCipher(privateKey, DECRYPT_MODE);
    return cipher.doFinal(targetData);
  }

  @Override
  public Tuple<String, String> generateKey() throws Exception {
    KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
    keyGenerator.init(DEFAULT_KEY_LENGTH);
    SecretKey secretKey = keyGenerator.generateKey();
    byte[] secretKeyBytes = secretKey.getEncoded();
    // 生成初始化向量
    byte[] ivParameterSpecBytes = new byte[IV_PARAMETER_SPEC_BYTE_LENGTH];
    new SecureRandom().nextBytes(ivParameterSpecBytes);
    String mergeStr = bytesToString(ByteUtil.merge(ivParameterSpecBytes, secretKeyBytes));
    return Tuple.of(mergeStr, mergeStr);
  }

  @Override
  public String getDefaultPublicKey() {
    return super.getDefaultPublicKey();
  }

  @Override
  public String getDefaultPrivateKey() {
    return super.getDefaultPrivateKey();
  }

  private Cipher initCipher(String key, int mode) throws Exception {
    byte[] keyBytes = stringToBytes(key);
    byte[] ivParameterSpecBytes = Arrays.copyOfRange(keyBytes, 0, IV_PARAMETER_SPEC_BYTE_LENGTH);
    byte[] secretKeySpecBytes = Arrays.copyOfRange(keyBytes, IV_PARAMETER_SPEC_BYTE_LENGTH,
        keyBytes.length);
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeySpecBytes, KEY_ALGORITHM);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(ivParameterSpecBytes);
    cipher.init(mode, secretKeySpec, ivParameterSpec);
    return cipher;
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
