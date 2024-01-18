package cn.ares.boot.util.common.thread;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2022-11-21 14:01:42
 * @description: Thread util
 * @version: JDK 1.8
 */
public class ThreadUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(ThreadUtilTest.class);

  public static void main(String[] args) {
    LOGGER.info("largeThreadCount: " + ThreadUtil.getLargeThreadCount());
    LOGGER.info("suitableThreadCount: " + ThreadUtil.getSuitableThreadCount());
    ThreadUtil.getExecutorService("Test-Case-Repeat-Thread-%d", -1,
        100_000, new CallerRunsPolicy());
  }

}
