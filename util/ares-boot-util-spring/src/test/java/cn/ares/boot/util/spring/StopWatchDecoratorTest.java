package cn.ares.boot.util.spring;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2023-05-11 12:40:19
 * @description: StopWatchDecorator test
 * @version: JDK 1.8
 */
public class StopWatchDecoratorTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(StopWatchDecoratorTest.class);


  @Test
  public void test() throws InterruptedException {
    StopWatchDecorator stopWatch = StopWatchDecorator.buildWithStart();
    TimeUnit.MILLISECONDS.sleep(521);
    LOGGER.info("耗时毫秒数: {}", stopWatch.stopAndGetTotalMillis());
    stopWatch.start();
    TimeUnit.MILLISECONDS.sleep(888);
    LOGGER.info("耗时毫秒数: {}", stopWatch.stopAndGetTotalMillis());
    LOGGER.info("总耗时毫秒数: {}", stopWatch.stopAndGetTotalMillis());

    long startTime = System.currentTimeMillis();
    TimeUnit.MILLISECONDS.sleep(521);
    LOGGER.info("耗时毫秒数: {}", System.currentTimeMillis() - startTime);
  }

}
