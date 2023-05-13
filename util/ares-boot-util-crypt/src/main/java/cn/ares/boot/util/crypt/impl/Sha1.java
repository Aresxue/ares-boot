package cn.ares.boot.util.crypt.impl;

import cn.ares.boot.util.common.primitive.ByteUtil;
import cn.ares.boot.util.crypt.InReverseCrypt;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * @author: Ares
 * @time: 2021-11-23 17:05:00
 * @description: Sha1
 * @version: JDK 1.8
 */
public class Sha1 extends InReverseCrypt {

  private static final String KEY_ALGORITHM = "SHA-1";

  @Override
  public byte[] enCryptImpl(byte[] srcData, String salt) throws Exception {
    MessageDigest digest = MessageDigest.getInstance(KEY_ALGORITHM);
    byte[] dataBytes = ByteUtil.merge(srcData, salt.getBytes(Charset.defaultCharset()));
    digest.update(dataBytes);
    return digest.digest();
  }

  private static class LazyHolder {

    private static final Sha1 INSTANCE = new Sha1();
  }

  private Sha1() {
  }

  public static Sha1 getInstance() {
    return LazyHolder.INSTANCE;
  }


}
