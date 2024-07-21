package cn.ares.boot.base.log.util;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2022-10-25 14:03:55
 * @description: Re-put the task when thread pool task queue is fully
 * @description: 线程池满载时重新放入任务
 * @version: JDK 1.8
 */
public class RePutRejectedExecutionHandler implements RejectedExecutionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RePutRejectedExecutionHandler.class);

  @Override
  public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
    LOGGER.warn("process that the thread pool is fully loaded and re-put tasks");
    // put is blocking
    // put是阻塞的
    try {
      executor.getQueue().put(runnable);
    } catch (Exception e) {
      LOGGER.error("re-put the task exception: ", e);
    }
  }

  private static class LazyHolder {

    private static final RePutRejectedExecutionHandler INSTANCE = new RePutRejectedExecutionHandler();
  }

  private RePutRejectedExecutionHandler() {

  }

  public static RePutRejectedExecutionHandler getInstance() {
    return RePutRejectedExecutionHandler.LazyHolder.INSTANCE;
  }

}
