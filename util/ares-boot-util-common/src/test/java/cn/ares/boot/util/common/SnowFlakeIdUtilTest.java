package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2023-06-19 14:07:11
 * @description: SnowFlakeIdUtil test
 * @version: JDK 1.8
 */
public class SnowFlakeIdUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(SnowFlakeIdUtilTest.class);

  public static void main(String[] args) {
    LOGGER.info("generate id: " + SnowFlakeIdUtil.nextIdByCacheWhenClockMoved());
  }

}
