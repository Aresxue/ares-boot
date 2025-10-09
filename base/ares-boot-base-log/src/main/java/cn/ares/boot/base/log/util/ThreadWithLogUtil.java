package cn.ares.boot.base.log.util;


import cn.ares.boot.util.common.thread.NameThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
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
          LoggerUtil.warn(executor + "%s didn't terminate!");
        }
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted.
      executor.shutdownNow();
      // Preserve interrupt status.
      Thread.currentThread().interrupt();
    }
  }

  /**
   * @author: Ares
   * @description: 获取队列满载时重新放入任务的线程池服务（设置工作线程数小于等于0时自动取线程核心数）
   * @description: Get the thread pool service that rePut task when the queue is full (automatically takes the core number of threads when the number of working threads is set to less than or equal to 0)
   * @time: 2025-04-17 10:52:16
   * @params: [threadNameFormat, workerNum, taskSize, rejectedExecutionHandler]
   * 线程命名格式，工作线程数，任务数量，拒绝策略
   * @return: java.util.concurrent.ExecutorService 线程池服务
   */
  public static ExecutorService getRePutExecutorService(String threadNameFormat, Integer workerNum,
      Integer taskSize, RejectedExecutionHandler rejectedExecutionHandler) {
    ThreadFactory threadFactory = new NameThreadFactory().setNameFormat(threadNameFormat).build();
    if (workerNum <= 0) {
      workerNum = Runtime.getRuntime().availableProcessors() * 2;
    }
    return new RePutThreadPoolExecutor(workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(taskSize), threadFactory, rejectedExecutionHandler);
  }

}
