package cn.ares.boot.util.common.network;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2024-05-31 15:53:05
 * @description: NetworkUtil test
 * @version: JDK 1.8
 */
public class NetworkUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(NetworkUtilTest.class);

  public static void main(String[] args) {
    String hexIp = NetworkUtil.hexIp("127.0.0.1");
    JdkLoggerUtil.info(LOGGER, "current ip hex str: " + hexIp);
    JdkLoggerUtil.info(LOGGER, "current ip str: " + NetworkUtil.restoreHexIp(hexIp));
  }

}
