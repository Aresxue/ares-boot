package cn.ares.boot.base.log.util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: Ares
 * @time: 2022-12-19 16:04:32
 * @description: Thread util with log
 * @version: JDK 1.8
 */
public class ThreadWithLogUtil {

  /**
   * Shutdown passed thread using isAlive and join.
   *
   * @param thread Thread to stop
   */
  public static void shutdownGracefully(final Thread thread) {
    shutdownGracefully(thread, 0);
  }

  /**
   * Shutdown passed thread using isAlive and join.
   *
   * @param millis Pass 0 if we're to wait forever.
   * @param thread Thread to stop
   */
  public static void shutdownGracefully(final Thread thread, final long millis) {
    if (thread == null) {
      return;
    }
    while (thread.isAlive()) {
      try {
        thread.interrupt();
        thread.join(millis);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  public static void shutdownGracefully(ExecutorService executor) {
    shutdownGracefully(executor, 0, TimeUnit.MILLISECONDS);
  }

  /**
   * An implementation of the graceful stop sequence recommended by {@link ExecutorService}.
   *
   * @param executor executor
   * @param timeout  timeout
   * @param timeUnit timeUnit
   */
  public static void shutdownGracefully(ExecutorService executor, long timeout, TimeUnit timeUnit) {
    // Disable new tasks from being submitted.
    executor.shutdown();
    try {
      // Wait a while for existing tasks to terminate.
      if (!executor.awaitTermination(timeout, timeUnit)) {
        executor.shutdownNow();
        // Wait a while for tasks to respond to being cancelled.
        if (!executor.awaitTermination(timeout, timeUnit)) {
          LoggerUtil.warn(String.format("%s didn't terminate!", executor));
        }
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted.
      executor.shutdownNow();
      // Preserve interrupt status.
      Thread.currentThread().interrupt();
    }
  }

}
