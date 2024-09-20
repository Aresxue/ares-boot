package cn.ares.boot.util.crypt;


import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.DES;
import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.SM4;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import cn.ares.boot.util.common.structure.Tuple;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 * @author: Ares
 * @time: 2021-11-22 14:51
 * @description: 可逆加密测试
 * @description: Reverse crypt test
 * @version: JDK 1.8
 */
public class ReverseCryptTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(ReverseCryptTest.class);
  private static final ReverseCrypt reverseCrypt = ReverseCryptUtil.getInstance(DES);

  @Test
  public void test() throws Exception {
    // 生成公钥和私钥
    Tuple<String, String> tuple = reverseCrypt.generateKey();
    String publicKey = tuple.getFirst();
    String privateKey = tuple.getSecond();
    JdkLoggerUtil.info(LOGGER, "公钥: " + publicKey);
    JdkLoggerUtil.info(LOGGER, "私钥: " + privateKey);

    String enCryptStr = reverseCrypt.enCrypt("kele", publicKey);
    JdkLoggerUtil.info(LOGGER, "加密后字符串: " + enCryptStr);
    String deCryptStr = reverseCrypt.deCrypt(enCryptStr, privateKey);
    JdkLoggerUtil.info(LOGGER, "解密出的字符串: " + deCryptStr);

    // 获取默认公钥
//    JdkLoggerUtil.info(LOGGER, reverseCrypt.getDefaultPublicKey());
    // 获取默认私钥
//    JdkLoggerUtil.info(LOGGER, reverseCrypt.getDefaultPrivateKey());
  }

  private void crypt() {
    String enCryptStr = reverseCrypt.enCrypt("kele");
    JdkLoggerUtil.info(LOGGER, "加密后字符串: " + enCryptStr);
    String deCryptStr = reverseCrypt.deCrypt(enCryptStr);
    JdkLoggerUtil.info(LOGGER, "解密出的字符串: " + deCryptStr);
  }

}
