package cn.ares.boot.starter.cache.operation;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import cn.ares.boot.util.test.BootBaseTest;
import cn.ares.boot.util.test.TestUtil;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;

/**
 * @author: Ares
 * @time: 2024-09-19 16:30:11
 * @description: CacheOperation test
 * @version: JDK 1.8
 */
public class CacheOperationTest extends BootBaseTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(CacheOperationTest.class);

  @Resource
  private CacheOperation<Object> cacheOperation;

  @Test
  public void testCache() {
    String key = "CACHE:TEST";
    cacheOperation.set(key, "kele");
    JdkLoggerUtil.info(LOGGER, "cache value: " + cacheOperation.get(key));
    cacheOperation.set(key, new Object());
    JdkLoggerUtil.info(LOGGER, "cache value: " + cacheOperation.get(key));

    key = "CACHE:TEST:HASH";
    cacheOperation.hPut(key, "name", "kele");
    JdkLoggerUtil.info(LOGGER, "cache hash value: " + cacheOperation.hGet(key, "name"));
  }

  @Test
  public void testLock() {
    AtomicBoolean flag = new AtomicBoolean(false);
    for (int i = 0; i < 8; i++) {
      CompletableFuture.runAsync(() -> {
        while (true) {
          Boolean result = cacheOperation.getWithTryLock("LOCK:TEST", () -> {
            // 测试重入锁只有当最后一次释放锁时其它线程才能取到锁
            if (flag.get()) {
              JdkLoggerUtil.warn(LOGGER, "lock was acquired repeatedly");
            }
            JdkLoggerUtil.info(LOGGER, "lock success");
            flag.set(true);
            cacheOperation.runWithTryLock("LOCK:TEST",
                () -> JdkLoggerUtil.info(LOGGER, "lock reentrant success"));
            // 测试重入锁留出足够的时间给其它线程抢锁
            TestUtil.await(500);
            // 最后设置标志为false
            flag.set(false);
            return true;
          });
          if (null == result) {
            JdkLoggerUtil.info(LOGGER, "lock fail");
          }
          TestUtil.await(100);
        }
      });
    }
    TestUtil.await();
  }

}
