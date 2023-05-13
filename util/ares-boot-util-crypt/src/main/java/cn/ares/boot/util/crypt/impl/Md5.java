package cn.ares.boot.util.crypt.impl;

import cn.ares.boot.util.common.primitive.ByteUtil;
import cn.ares.boot.util.crypt.InReverseCrypt;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * @author: Ares
 * @time: 2021-11-22 17:42:00
 * @description: Md5
 * @version: JDK 1.8
 */
public class Md5 extends InReverseCrypt {

  private static final String KEY_ALGORITHM = "MD5";

  @Override
  public byte[] enCryptImpl(byte[] srcData, String salt) throws Exception {
    MessageDigest messageDigest = MessageDigest.getInstance(KEY_ALGORITHM);
    byte[] dataBytes = ByteUtil.merge(srcData, salt.getBytes(Charset.defaultCharset()));
    return messageDigest.digest(dataBytes);
  }


  private static class LazyHolder {

    private static final Md5 INSTANCE = new Md5();
  }

  private Md5() {
  }

  public static Md5 getInstance() {
    return LazyHolder.INSTANCE;
  }

}
