package cn.ares.boot.util.common.thread;

import cn.ares.boot.util.common.ExceptionUtil;
import cn.ares.boot.util.common.MapUtil;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: Ares
 * @time: 2021-12-22 23:16:47
 * @description: 线程工具
 * @description: Thread util
 * @version: JDK 1.8
 */
public class ThreadUtil {

  private static final int THREAD_MULTIPLE = 2;
  /**
   * the upper limit for a relatively small amount of available cpu
   */
  private static final int AVAILABLE_PROCESSORS_LITTLE_UPPER = 2;
  private static Field threadLocalsField;
  private static Field inheritableThreadLocalsField;
  private static Field tableField;

  /**
   * @author: Ares
   * @description: 以默认并发度获取合适的线程数
   * @description: Get suitable thread count when thread multiple is two
   * @time: 2021-12-22 23:17:53
   * @params: []
   * @return: int thread count
   * @return: int 线程数
   */
  public static int getSuitableThreadCount() {
    return getSuitableThreadCount(THREAD_MULTIPLE);
  }

  /**
   * @author: Ares
   * @description: 根据并发度和io敏感性获取合适的线程数
   * @description: Get suitable thread count with thread multiple
   * @time: 2021-12-22 23:18:40
   * @params: [threadMultiple, ioIntensive] thread multiple, io intensive type
   * @params: [threadMultiple, ioIntensive] 线程并发度，io密集型
   * @return: int thread count
   * @return: int 线程数
   */
  public static int getSuitableThreadCount(int threadMultiple, boolean ioIntensive) {
    final int coreCount = Runtime.getRuntime().availableProcessors();
    int workerCount;
    if (ioIntensive) {
      workerCount = 1;
      while (workerCount < coreCount * threadMultiple) {
        workerCount <<= 1;
      }
    } else {
      workerCount = coreCount - 1;
    }

    return workerCount;
  }

  /**
   * @author: Ares
   * @description: 获取较大的线程数（当线程数小于等于2时取线程数的4倍）
   * @description: Get a large number of threads (4 times the number of threads if the number of
   * threads is 2 or less)
   * @time: 2023-05-08 11:12:28
   * @params: []
   * @return: int 线程数
   */
  public static int getLargeThreadCount() {
    final int coreCount = Runtime.getRuntime().availableProcessors();
    if (coreCount <= AVAILABLE_PROCESSORS_LITTLE_UPPER) {
      return coreCount << THREAD_MULTIPLE;
    } else {
      return coreCount << (THREAD_MULTIPLE - 1);
    }
  }

  /**
   * @author: Ares
   * @description: 获取合适的线程数(默认io密集型)
   * @description: Get suitable thread count with thread multiple(default type is io intensive)
   * @time: 2022-03-04 10:27:31
   * @params: [threadMultiple] 线程并发度
   * @return: int thread count
   * @return: int 线程数
   */
  public static int getSuitableThreadCount(int threadMultiple) {
    return getSuitableThreadCount(threadMultiple, true);
  }

  /**
   * @author: Ares
   * @description: 获取合适的线程数(使用默认并发度)
   * @description: Get suitable thread count with thread multiple(use default thread multiple)
   * @time: 2022-03-04 10:27:31
   * @params: [ioIntensive] is io intensive
   * @params: [ioIntensive] 是否io密集型
   * @return: int thread count
   * @return: int 线程数
   */
  public static int getSuitableThreadCount(boolean ioIntensive) {
    return getSuitableThreadCount(THREAD_MULTIPLE, ioIntensive);
  }

  /**
   * @author: Ares
   * @description: 根据资源名获取资源流
   * @description: Get resource steam based on resource name
   * @time: 2022-12-28 20:03:06
   * @params: [resourceName] 资源名
   * @return: java.io.InputStream 资源流
   */
  public static InputStream getResourceAsStream(String resourceName) {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
  }

  /**
   * @author: Ares
   * @description: 获取资源定位符
   * @description: get resource url
   * @time: 2022-12-28 20:03:06
   * @params: [resourceName] 资源名
   * @return: java.net.URL 资源定位符
   */
  public static URL getResource(String resourceName) {
    return Thread.currentThread().getContextClassLoader().getResource(resourceName);
  }

  /**
   * @author: Ares
   * @description: 基于线程工厂创建单线程池
   * @description: Create a single-threaded pool based on a thread factory
   * @time: 2022-07-26 11:39:25
   * @params: [threadFactory] 线程工厂
   * @return: java.util.concurrent.Executor 线程池
   */
  public static Executor newSingleExecutor(ThreadFactory threadFactory) {
    return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(), threadFactory);
  }

  /**
   * @author: Ares
   * @description: 获取当前线程的所有ThreadLocalMap
   * @description: Get all ThreadLocalMap of current thread
   * @time: 2022-10-11 11:22:45
   * @params: []
   * @return: java.util.Map<java.lang.ThreadLocal < ?>,java.lang.Object>
   */
  public static Map<ThreadLocal<?>, Object> getAllThreadLocalMap() {
    Map<ThreadLocal<?>, Object> result = getThreadLocalMap();
    result.putAll(getInheritableThreadLocalsMap());
    return result;
  }

  /**
   * @author: Ares
   * @description: 获取当前线程的ThreadLocalMap
   * @description: Get ThreadLocalMap of current thread
   * @time: 2022-10-11 11:22:45
   * @params: []
   * @return: java.util.Map<java.lang.ThreadLocal < ?>,java.lang.Object>
   */
  public static Map<ThreadLocal<?>, Object> getThreadLocalMap() {
    return getThreadLocalMap(false);
  }

  /**
   * @author: Ares
   * @description: 获取当前线程的InheritableThreadLocalMap
   * @description: Get InheritableThreadLocalMap of current thread
   * @time: 2022-10-11 11:22:45
   * @params: []
   * @return: java.util.Map<java.lang.ThreadLocal < ?>,java.lang.Object>
   */
  public static Map<ThreadLocal<?>, Object> getInheritableThreadLocalsMap() {
    return getThreadLocalMap(true);
  }

  /**
   * @author: Ares
   * @description: 获取当前线程的ThreadLocalMap
   * @description: Get ThreadLocalMap of current thread
   * @time: 2022-10-11 11:22:45
   * @params: [inheritable] 是否为inheritable
   * @return: java.util.Map<java.lang.ThreadLocal < ?>,java.lang.Object>
   */
  public static Map<ThreadLocal<?>, Object> getThreadLocalMap(boolean inheritable) {
    return getThreadLocalMap(Thread.currentThread(), inheritable);
  }

  /**
   * @author: Ares
   * @description: 获取线程的ThreadLocalMap
   * @description: Get ThreadLocalMap of thread
   * @time: 2022-10-11 11:22:45
   * @params: [thread, inheritable] 线程, 是否为inheritable
   * @return: java.util.Map<java.lang.ThreadLocal < ?>,java.lang.Object>
   */
  public static Map<ThreadLocal<?>, Object> getThreadLocalMap(Thread thread, boolean inheritable) {
    Map<ThreadLocal<?>, Object> threadLocalMap = MapUtil.newHashMap();
    return ExceptionUtil.get(() -> {
      Field field;
      if (inheritable) {
        if (null == inheritableThreadLocalsField) {
          inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
          inheritableThreadLocalsField.setAccessible(true);
        }
        field = inheritableThreadLocalsField;
      } else {
        if (null == threadLocalsField) {
          threadLocalsField = Thread.class.getDeclaredField("threadLocals");
          threadLocalsField.setAccessible(true);
        }
        field = threadLocalsField;
      }

      Object threadLocals = field.get(thread);
      if (null == threadLocals) {
        return threadLocalMap;
      }
      if (null == tableField) {
        tableField = threadLocals.getClass().getDeclaredField("table");
        tableField.setAccessible(true);
      }

      Object[] table = (Object[]) tableField.get(threadLocals);
      for (Object entry : table) {
        if (entry != null) {
          WeakReference<ThreadLocal<?>> threadLocalRef = (WeakReference<ThreadLocal<?>>) entry;
          ThreadLocal<?> threadLocal = threadLocalRef.get();
          if (threadLocal != null) {
            Object threadLocalValue = threadLocal.get();
            threadLocalMap.put(threadLocal, threadLocalValue);
          }
        }
      }
      return threadLocalMap;
    });
  }

  /**
   * @author: Ares
   * @description: 无限大小的同步线程池
   * @description: Synchronous thread pool of unlimited size
   * @time: 2023-05-08 13:25:26
   * @params: [threadNameFormat] 线程命名格式
   * @return: java.util.concurrent.ExecutorService
   */
  public static ExecutorService newCachedThreadPool(String threadNameFormat) {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
        new SynchronousQueue<>(), new NameThreadFactory().setNameFormat(threadNameFormat).build());
  }

  /**
   * @author: Ares
   * @description: 创建定时任务线程池
   * @time: 2023-06-06 15:33:13
   * @params: [corePoolSize] 核心线程数
   * @return: java.util.concurrent.ScheduledExecutorService 定时任务线程池
   */
  public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
    return new ScheduledThreadPoolExecutor(corePoolSize);
  }

  /**
   * @author: Ares
   * @description: 创建定时任务线程池
   * @time: 2023-06-06 15:33:13
   * @params: [corePoolSize, threadNameFormat] 核心线程数，线程名称格式
   * @return: java.util.concurrent.ScheduledExecutorService 定时任务线程池
   */
  public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize,
      String threadNameFormat) {
    return new ScheduledThreadPoolExecutor(corePoolSize,
        new NameThreadFactory().setNameFormat(threadNameFormat).build());
  }

  /**
   * @author: Ares
   * @description: 获取线程池服务（设置工作线程数小于等于0时自动取线程核心数）
   * @description: Get thread pool service (Set the number of thread cores to be automatically
   * fetched when the number of worker threads is less than or equal to 0)
   * @time: 2023-05-08 10:52:16
   * @params: [threadNameFormat, workerNum, taskSize, rejectedExecutionHandler]
   * 线程命名格式，工作线程数，任务数量，拒绝策略
   * @return: java.util.concurrent.ExecutorService 线程池服务
   */
  public static ExecutorService getExecutorService(String threadNameFormat, Integer workerNum,
      Integer taskSize, RejectedExecutionHandler rejectedExecutionHandler) {
    ThreadFactory threadFactory = new NameThreadFactory().setNameFormat(threadNameFormat).build();
    if (workerNum <= 0) {
      workerNum = Runtime.getRuntime().availableProcessors() * 2;
    }
    return new ThreadPoolExecutor(workerNum, workerNum, 0L,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(taskSize),
        threadFactory, rejectedExecutionHandler);
  }

  /**
   * @author: Ares
   * @description: 移除java虚拟机关闭的钩子
   * @description: Remove jvm shutDown hook
   * @time: 2023-05-08 11:31:15
   * @params: [shutDownHook]
   * @return: void
   */
  public static void removeShutdownHook(Thread shutDownHook) {
    if (shutDownHook != null) {
      try {
        Runtime.getRuntime().removeShutdownHook(shutDownHook);
      } catch (IllegalStateException e) {
        // ignore - VM is already shutting down
      }
    }
  }

}
