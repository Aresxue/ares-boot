package cn.ares.boot.base.log.util;

import cn.ares.boot.util.common.structure.ConcurrentHashSet;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Ares
 * @time: 2025-04-16 20:44:19
 * @description: 可追踪的线程池
 * @description: Trackable thread pool
 * @version: JDK 1.8
 */
public class TrackableThreadPoolExecutor extends ThreadPoolExecutor {

  /**
   * 工作线程集合
   * Working thread set
   */
  private final Set<Thread> workerThreadSet;

  public TrackableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue,
      @NotNull RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    workerThreadSet = initWorkerThreadSet(handler);
  }

  public TrackableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue,
      @NotNull ThreadFactory threadFactory, @NotNull RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    workerThreadSet = initWorkerThreadSet(handler);
  }


  public TrackableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    workerThreadSet = initWorkerThreadSet(null);
  }

  public TrackableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue,
      @NotNull ThreadFactory threadFactory) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    workerThreadSet = initWorkerThreadSet(null);
  }

  @NotNull
  protected Set<Thread> initWorkerThreadSet(RejectedExecutionHandler handler) {
    return new ConcurrentHashSet<>();
  }

  @Override
  protected void beforeExecute(Thread thread, Runnable runnable) {
    // 记录工作线程
    // Record working threads
    workerThreadSet.add(thread);
    super.beforeExecute(thread, runnable);
  }

  @Override
  protected void afterExecute(Runnable runnable, Throwable throwable) {
    try {
      super.afterExecute(runnable, throwable);
    } finally {
      workerThreadSet.remove(Thread.currentThread());
    }
  }

  @Override
  protected void terminated() {
    super.terminated();
    workerThreadSet.clear();
  }


  /**
   * 获取当前所有工作线程的集合
   * Get the set of all current working threads
   */
  public Set<Thread> getWorkerThreadSet() {
    return Collections.unmodifiableSet(workerThreadSet);
  }

  /**
   * 获取当前工作线程数
   * Get the number of current working threads
   */
  public int getWorkerThreadCount() {
    return workerThreadSet.size();
  }


}
