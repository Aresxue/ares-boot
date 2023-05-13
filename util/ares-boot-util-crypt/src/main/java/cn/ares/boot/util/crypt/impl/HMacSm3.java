package cn.ares.boot.util.crypt.impl;

import cn.ares.boot.util.crypt.InReverseCrypt;
import java.nio.charset.Charset;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * @author: Ares
 * @time: 2022-01-13 10:28:05
 * @description: HMac sm3
 * @version: JDK 1.8
 */
public class HMacSm3 extends InReverseCrypt {

  @Override
  public byte[] enCryptImpl(byte[] srcData, String salt) throws Exception {
    HMac hMac = new HMac(new SM3Digest());
    CipherParameters cipherParameters = new KeyParameter(salt.getBytes(Charset.defaultCharset()));
    hMac.init(cipherParameters);
    hMac.update(srcData, 0, srcData.length);
    byte[] hash = new byte[hMac.getMacSize()];
    hMac.doFinal(hash, 0);
    return hash;
  }

  private static class LazyHolder {

    private static final HMacSm3 INSTANCE = new HMacSm3();
  }

  private HMacSm3() {
  }

  public static HMacSm3 getInstance() {
    return LazyHolder.INSTANCE;
  }


}
