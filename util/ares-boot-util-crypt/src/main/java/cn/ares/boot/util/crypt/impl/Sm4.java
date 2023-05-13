package cn.ares.boot.util.crypt.impl;

import cn.ares.boot.util.crypt.AbstractCrypt;
import cn.ares.boot.util.crypt.ReverseCrypt;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author: Ares
 * @time: 2021-11-23 17:38:00
 * @description: Sm4
 * @version: JDK 1.8
 */
public class Sm4 extends ReverseCrypt {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  private static final String ALGORITHM_NAME = "SM4";
  /**
   * SThe SM4 algorithm currently only supports 128 bits (that is, the key is 16 bytes)
   * SM4算法目前只支持128位（即密钥16字节）
   */
  private static final int DEFAULT_KEY_SIZE = 128;

  @Override
  public byte[] enCryptImpl(byte[] srcData, String publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", BouncyCastleProvider.PROVIDER_NAME);
    Key sm4Key = new SecretKeySpec(AbstractCrypt.stringToBytes(publicKey), ALGORITHM_NAME);
    cipher.init(Cipher.ENCRYPT_MODE, sm4Key);

    return cipher.doFinal(srcData);
  }

  @Override
  public byte[] deCryptImpl(byte[] targetData, String privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", BouncyCastleProvider.PROVIDER_NAME);
    Key sm4Key = new SecretKeySpec(AbstractCrypt.stringToBytes(privateKey), ALGORITHM_NAME);
    cipher.init(Cipher.DECRYPT_MODE, sm4Key);
    return cipher.doFinal(targetData);
  }

  @Override
  public Map<String, String> generateKey(int length) throws Exception {
    Map<String, String> result = new HashMap<>(4);

    KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_NAME,
        BouncyCastleProvider.PROVIDER_NAME);
    SecureRandom secureRandom = new SecureRandom();
    keyGenerator.init(length, secureRandom);
    byte[] bytes = keyGenerator.generateKey().getEncoded();
    String str = AbstractCrypt.bytesToString(bytes);

    result.put(PUBLIC_KEY_NAME, str);
    result.put(PRIVATE_KEY_NAME, str);
    return result;
  }

  @Override
  public Map<String, String> generateKey() throws Exception {
    return generateKey(DEFAULT_KEY_SIZE);
  }

  private static class LazyHolder {

    private static final Sm4 INSTANCE = new Sm4();
  }

  private Sm4() {
  }

  public static Sm4 getInstance() {
    return LazyHolder.INSTANCE;
  }

}
