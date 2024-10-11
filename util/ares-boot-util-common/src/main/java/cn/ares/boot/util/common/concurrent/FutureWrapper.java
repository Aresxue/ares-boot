package cn.ares.boot.util.common.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author: Ares
 * @time: 2024-10-11 15:08:21
 * @description: Future包装类
 * @description: Future Wrapper
 * @version: JDK 1.8
 */
public class FutureWrapper<V> implements Future<V> {

  private final Future<V> future;

  private FutureWrapper(Future<V> future) {
    this.future = future;
  }

  public static <V> FutureWrapper<V> of(Future<V> future) {
    return new FutureWrapper<>(future);
  }

  public Future<V> unwrap() {
    return future;
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return future.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return future.isCancelled();
  }

  @Override
  public boolean isDone() {
    return future.isDone();
  }

  @Override
  public V get() throws InterruptedException, ExecutionException {
    return future.get();
  }

  @Override
  public V get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return future.get(timeout, unit);
  }

}
