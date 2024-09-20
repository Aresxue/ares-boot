package cn.ares.boot.util.test;

import cn.ares.boot.util.common.throwable.CheckedExceptionWrapper;

/**
 * @author: Ares
 * @time: 2023-05-04 14:56:47
 * @description: Boot test util
 * @version: JDK 1.8
 */
public class TestUtil {

  /**
   * @author: Ares
   * @description: 一直等待
   * @description: await
   * @time: 2023-05-04 14:57:55
   */
  public static void await() {
    await(Integer.MAX_VALUE);
  }

  public static void await(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException interruptedException) {
      throw new CheckedExceptionWrapper(interruptedException);
    }
  }

}
