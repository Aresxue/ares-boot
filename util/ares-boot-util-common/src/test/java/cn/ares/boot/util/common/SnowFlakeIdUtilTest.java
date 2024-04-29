package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2023-06-19 14:07:11
 * @description: SnowFlakeIdUtil test
 * @version: JDK 1.8
 */
public class SnowFlakeIdUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(SnowFlakeIdUtilTest.class);

  public static void main(String[] args) throws InterruptedException {
    JdkLoggerUtil.info(LOGGER, "generate id: " + SnowFlakeIdUtil.nextIdByCacheWhenClockMoved());

    // 测试数据倾斜
    // test data skew
    testDataSkew();
  }

  private static void testDataSkew() throws InterruptedException {
    int shardCount = 32;
    Map<Long, AtomicInteger> map = new HashMap<>();
    for (int i = 0; i < 1024; i++) {
      Thread.sleep(2);
      long id = SnowFlakeIdUtil.nextId();
      AtomicInteger atomicInteger = map.computeIfAbsent(id % shardCount, k -> new AtomicInteger(0));
      atomicInteger.incrementAndGet();
    }
    map.forEach((key, value) -> JdkLoggerUtil.info(LOGGER, key + ": " + value));

    map = new HashMap<>();
    for (int i = 0; i < 1024; i++) {
      Thread.sleep(2);
      long id = SnowFlakeIdUtil.nextId(shardCount);
      AtomicInteger atomicInteger = map.computeIfAbsent(id % shardCount, k -> new AtomicInteger(0));
      atomicInteger.incrementAndGet();
    }
    JdkLoggerUtil.info(LOGGER, "after set sequence start offset");
    map.forEach((key, value) -> JdkLoggerUtil.info(LOGGER, key + ": " + value));

    map = new HashMap<>();
    for (int i = 0; i < 1024; i++) {
      Thread.sleep(2);
      long id = SnowFlakeIdUtil.nextIdByCacheWhenClockMoved(shardCount);
      AtomicInteger atomicInteger = map.computeIfAbsent(id % shardCount, k -> new AtomicInteger(0));
      atomicInteger.incrementAndGet();
    }
    JdkLoggerUtil.info(LOGGER, "clock moved after set sequence start offset");
    map.forEach((key, value) -> JdkLoggerUtil.info(LOGGER, key + ": " + value));
  }

}
