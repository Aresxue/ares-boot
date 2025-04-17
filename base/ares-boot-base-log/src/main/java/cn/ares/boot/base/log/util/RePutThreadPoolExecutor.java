package cn.ares.boot.base.log.util;

import cn.ares.boot.util.common.structure.ConcurrentHashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Ares
 * @time: 2025-04-16 20:44:19
 * @description: 可追踪的线程池
 * @description: Trackable thread pool
 * @version: JDK 1.8
 */
public class RePutThreadPoolExecutor extends TrackableThreadPoolExecutor {


  public RePutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue,
      @NotNull RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
  }

  public RePutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue,
      @NotNull ThreadFactory threadFactory, @NotNull RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
  }

  public RePutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  public RePutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      @NotNull TimeUnit unit, @NotNull BlockingQueue<Runnable> workQueue,
      @NotNull ThreadFactory threadFactory) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
  }

  @NotNull
  @Override
  protected Set<Thread> initWorkerThreadSet(RejectedExecutionHandler handler) {
    Set<Thread> set =  new ConcurrentHashSet<>();
    RejectedExecutionHandler rejectedExecutionHandler = new RePutRejectedExecutionHandler(set, handler) ;
    super.setRejectedExecutionHandler(rejectedExecutionHandler);
    return set;
  }

}
