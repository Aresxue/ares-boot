package cn.ares.boot.util.common.reflect;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2024-01-19 13:52:33
 * @description: SunReflectionUtil test
 * @version: JDK 1.8
 */
public class SunReflectionUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(SunReflectionUtilTest.class);

  public static void main(String[] args) {
    JdkLoggerUtil.info(LOGGER, SunReflectionUtil.adaptiveGetCallerClass(1));
  }

}
