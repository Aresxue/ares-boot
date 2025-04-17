package cn.ares.boot.base.log.util;

import java.util.Set;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2022-10-25 14:03:55
 * @description: 线程池队列满载时重新放入任务
 * @description: Re-put the task when thread pool task queue is fully
 * @version: JDK 1.8
 */
public class RePutRejectedExecutionHandler implements RejectedExecutionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RePutRejectedExecutionHandler.class);

  /**
   * 使用当前拒绝策略的线程的工作线程集合 The set of worker threads that use the current rejection policy
   */
  private final Set<Thread> workerThreadSet;
  /**
   * 备选的拒绝策略 Backup rejection policy
   */
  private final RejectedExecutionHandler backupRejectedExecutionHandler;

  public RePutRejectedExecutionHandler(Set<Thread> workerThreadSet, RejectedExecutionHandler handler) {
    this.workerThreadSet = workerThreadSet;
    // 默认使用拒绝策略和jdk
    // 默认使用拒绝策略
    this.backupRejectedExecutionHandler = null == handler ? new AbortPolicy() : handler;
  }

  @Override
  public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
    if (executor.isShutdown()) {
      LOGGER.warn("thread pool is shutdown");
      return;
    }
    // 检查当前线程是否属于线程池的工作线程
    // Check whether the current thread belongs to the worker thread of the thread pool
    if (workerThreadSet.contains(Thread.currentThread())) {
      // 直接使用备份的拒绝策略
      // Directly use the backup rejection policy
      backupRejectedExecutionHandler.rejectedExecution(runnable, executor);
      return;
    }
    LOGGER.warn("process that the thread pool is fully loaded and re-put tasks");
    // put是阻塞的
    // put is blocking
    try {
      executor.getQueue().put(runnable);
    } catch (InterruptedException interruptedException) {
      LOGGER.error("re-put the task exception:", interruptedException);
      Thread.currentThread().interrupt();
    }
  }

}
