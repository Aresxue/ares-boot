package cn.ares.boot.util.crypt.impl;

import cn.ares.boot.util.common.structure.Tuple;
import cn.ares.boot.util.crypt.ReverseCrypt;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * @author: Ares
 * @time: 2021-11-23 13:39:00
 * @description: Rsa
 * @version: JDK 1.8
 */
public class Rsa extends ReverseCrypt {

  private static final int DEFAULT_KEY_LENGTH = 1024;

  private static final String KEY_ALGORITHM = "RSA";

  /**
   * RSA maximum encrypted plaintext size RSA最大加密明文大小
   */
  private static final int MAX_ENCRYPT_BLOCK = 117;

  /**
   * RSA maximum decrypted ciphertext size RSA最大解密密文大小
   */
  private static final int MAX_DECRYPT_BLOCK = 128;

  @Override
  public byte[] enCryptImpl(byte[] srcData, String publicKey) throws Exception {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
      PublicKey key = buildPublicKey(publicKey);
      Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      int inputLen = srcData.length;
      int offSet = 0;
      int i = 0;
      return getBytes(srcData, outputStream, cipher, inputLen, offSet, i, MAX_ENCRYPT_BLOCK);
    }
  }

  @Override
  public byte[] deCryptImpl(byte[] targetData, String privateKey) throws Exception {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      PrivateKey key = buildPrivateKey(privateKey);
      Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key);
      int inputLen = targetData.length;
      int offSet = 0;
      int i = 0;
      // Decrypt data segments
      // 对数据分段解密
      return getBytes(targetData, outputStream, cipher, inputLen, offSet, i, MAX_DECRYPT_BLOCK);
    }
  }

  private byte[] getBytes(byte[] targetData, ByteArrayOutputStream outputStream, Cipher cipher,
      int inputLen, int offSet, int i, int maxDecryptBlock)
      throws IllegalBlockSizeException, BadPaddingException {
    byte[] cache;
    while (inputLen - offSet > 0) {
      if (inputLen - offSet > maxDecryptBlock) {
        cache = cipher.doFinal(targetData, offSet, maxDecryptBlock);
      } else {
        cache = cipher.doFinal(targetData, offSet, inputLen - offSet);
      }
      outputStream.write(cache, 0, cache.length);
      i++;
      offSet = i * maxDecryptBlock;
    }
    return outputStream.toByteArray();
  }

  @Override
  public Tuple<String, String> generateKey() throws Exception {
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
    keyPairGen.initialize(DEFAULT_KEY_LENGTH);
    KeyPair keyPair = keyPairGen.generateKeyPair();

    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    return Tuple.of(bytesToString(publicKey.getEncoded()), bytesToString(privateKey.getEncoded()));
  }

  private PublicKey buildPublicKey(String publicKey) throws Exception {
    byte[] keyBytes = stringToBytes(publicKey);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    return keyFactory.generatePublic(keySpec);
  }

  private PrivateKey buildPrivateKey(String privateKey) throws Exception {
    byte[] keyBytes = stringToBytes(privateKey);
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    return keyFactory.generatePrivate(keySpec);
  }

  private static class LazyHolder {

    private static final Rsa INSTANCE = new Rsa();
  }

  private Rsa() {
  }

  public static Rsa getInstance() {
    return LazyHolder.INSTANCE;
  }

}
