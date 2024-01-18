package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2024-01-18 18:53:01
 * @description: JreVersion test
 * @version: JDK 1.8
 */
public class JreVersionTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(JreVersionTest.class);

  public static void main(String[] args) {
    LOGGER.info("current version: " + JreVersion.currentVersion());
    RuntimeException ex = new RuntimeException();
    JdkLoggerUtil.warn(LOGGER, "current version: " + JreVersion.currentVersion() + ", ex: ", ex);
  }

}
