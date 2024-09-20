package cn.ares.boot.util.crypt.impl;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

import cn.ares.boot.util.common.structure.Tuple;
import cn.ares.boot.util.crypt.ReverseCrypt;
import java.security.Key;
import java.security.Security;
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
    Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", PROVIDER_NAME);
    Key sm4Key = new SecretKeySpec(stringToBytes(publicKey), ALGORITHM_NAME);
    cipher.init(Cipher.ENCRYPT_MODE, sm4Key);

    return cipher.doFinal(srcData);
  }

  @Override
  public byte[] deCryptImpl(byte[] targetData, String privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", PROVIDER_NAME);
    Key sm4Key = new SecretKeySpec(stringToBytes(privateKey), ALGORITHM_NAME);
    cipher.init(DECRYPT_MODE, sm4Key);
    return cipher.doFinal(targetData);
  }


  @Override
  public Tuple<String, String> generateKey() throws Exception {
    KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_NAME, PROVIDER_NAME);
    keyGenerator.init(DEFAULT_KEY_SIZE);
    byte[] bytes = keyGenerator.generateKey().getEncoded();
    String str = bytesToString(bytes);
    return Tuple.of(str, str);
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
