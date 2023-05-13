package cn.ares.boot.util.common.thread;

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

/**
 * @author: Ares
 * @time: 2022-11-21 14:01:42
 * @description: Thread util
 * @version: JDK 1.8
 */
public class ThreadUtilTest {

  public static void main(String[] args) {
    System.out.println("largeThreadCount: " + ThreadUtil.getLargeThreadCount());
    System.out.println("suitableThreadCount: " + ThreadUtil.getSuitableThreadCount());
    ThreadUtil.getExecutorService("Test-Case-Repeat-Thread-%d", -1,
        100_000, new CallerRunsPolicy());
  }

}
