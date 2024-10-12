package cn.ares.boot.util.common.gc;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.time.Duration;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2024-10-11 22:11:09
 * @description: GcUtil test
 * @version: JDK 1.8
 */
public class GcUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(GcUtilTest.class);

  public static void main(String[] args) throws Exception {
    Duration validatePeriod = Duration.ofSeconds(10);
    // 监控1分钟窗口
    Duration validateDuration = Duration.ofMinutes(1);

    GcUtil.runWhenFullGc("Test", validatePeriod, validateDuration, 1,
        () -> LOGGER.info("Full GC happened"));
//    GcUtil.shutdownGcMonitor("Test");
    Thread.sleep(Integer.MAX_VALUE);
  }

}
