package cn.ares.boot.util.crypt.impl;

import cn.ares.boot.util.common.primitive.ByteUtil;
import cn.ares.boot.util.crypt.InReverseCrypt;
import java.nio.charset.Charset;
import org.bouncycastle.crypto.digests.SM3Digest;

/**
 * @author: Ares
 * @time: 2021-11-23 17:38:00
 * @description: Sm3
 * @version: JDK 1.8
 */
public class Sm3 extends InReverseCrypt {

  @Override
  public byte[] enCryptImpl(byte[] srcData, String salt) throws Exception {
    SM3Digest digest = new SM3Digest();
    byte[] dataBytes = ByteUtil.merge(srcData, salt.getBytes(Charset.defaultCharset()));
    digest.update(dataBytes, 0, srcData.length);
    byte[] hash = new byte[digest.getDigestSize()];
    digest.doFinal(hash, 0);
    return hash;
  }


  private static class LazyHolder {

    private static final Sm3 INSTANCE = new Sm3();
  }

  private Sm3() {
  }

  public static Sm3 getInstance() {
    return LazyHolder.INSTANCE;
  }

}
