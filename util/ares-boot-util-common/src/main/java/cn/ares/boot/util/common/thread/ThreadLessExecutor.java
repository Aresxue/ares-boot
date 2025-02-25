package cn.ares.boot.util.common.thread;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2025-02-25 19:47:21
 * @description: 在不需要额外线程的情况下执行回调和监听器，优化了资源使用。在一些同步调用场景下，它可以直接在调用线程中执行任务，从而避免了线程切换的开销，它可以使调用看起来像是同步的，但在底层，它仍遵循异步调用的编程模式，只是利用了同一线程来完成工作
 * @description: Execute callbacks and listeners without the need for additional threads, optimizing resource usage. In some synchronous call scenarios, it can execute tasks directly in the calling thread, thus avoiding the overhead of thread switching. It can make the call look like  synchronous, but at the bottom, it still follows the programming model of asynchronous calls, just using the same thread to complete the work.
 * @version: JDK 1.8
 */
public class ThreadLessExecutor extends ConcurrentLinkedQueue<Runnable> implements Executor {

  private static final long serialVersionUID = 19045519375836044L;

  private static final Logger LOGGER = Logger.getLogger(ThreadLessExecutor.class.getName());

  // sentinel
  private static final Object SHUTDOWN = new Object();

  // Set to the calling thread while it's parked, SHUTDOWN on RPC completion
  private volatile Object waiter;
  private final boolean rejectRunnableOnExecutor;

  // Non private to avoid synthetic class
  public ThreadLessExecutor(boolean rejectRunnableOnExecutor) {
    this.rejectRunnableOnExecutor = rejectRunnableOnExecutor;
  }

  /**
   * Waits until there is a Runnable, then executes it and all queued Runnables after it. Must only
   * be called by one thread at a time.
   */
  public void waitAndDrain() throws InterruptedException {
    throwIfInterrupted();
    Runnable runnable = poll();
    if (runnable == null) {
      waiter = Thread.currentThread();
      try {
        while ((runnable = poll()) == null) {
          LockSupport.park(this);
          throwIfInterrupted();
        }
      } finally {
        waiter = null;
      }
    }
    do {
      runQuietly(runnable);
    } while ((runnable = poll()) != null);
  }

  /**
   * Called after final call to {@link #waitAndDrain()}, from same thread.
   */
  public void shutdown() {
    waiter = SHUTDOWN;
    Runnable runnable;
    while ((runnable = poll()) != null) {
      runQuietly(runnable);
    }
  }

  private static void runQuietly(Runnable runnable) {
    try {
      runnable.run();
    } catch (Throwable t) {
      LOGGER.log(Level.WARNING, "Runnable threw exception", t);
    }
  }

  private static void throwIfInterrupted() throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }

  @Override
  public void execute(Runnable runnable) {
    add(runnable);
    Object waiter = this.waiter;
    if (waiter != SHUTDOWN) {
      // no-op if null
      LockSupport.unpark((Thread) waiter);
    } else if (remove(runnable) && rejectRunnableOnExecutor) {
      throw new RejectedExecutionException();
    }
  }

}
