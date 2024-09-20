package cn.ares.boot.util.crypt;


import static cn.ares.boot.util.crypt.constant.CryptAlgorithm.MD5;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 * @author: Ares
 * @time: 2021-11-23 11:19
 * @description: 不可逆加密测试
 * @description: InReverse crypt test
 * @version: JDK 1.8
 */
public class InReverseCryptTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(InReverseCryptTest.class);
  private static final InReverseCrypt inReverseCrypt = InReverseCryptUtil.getInstance(MD5);

  @Test
  public void test() {
    crypt();

//    JdkLoggerUtil.info(LOGGER, inReverseCrypt.generateSalt());
//    JdkLoggerUtil.info(LOGGER, inReverseCrypt.getDefaultSalt());
  }

  private void crypt() {
    String inReverseCryptStr = inReverseCrypt.enCrypt("kele");
    JdkLoggerUtil.info(LOGGER, "加密后字符串: " + inReverseCryptStr);
    boolean verifyResult = inReverseCrypt.signatureVerify("kele", inReverseCryptStr);
    JdkLoggerUtil.info(LOGGER, "验签结果: " + verifyResult);
  }

}
