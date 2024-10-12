package cn.ares.boot.util.common.gc;

import cn.ares.boot.util.common.thread.NameThreadFactory;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author: Ares
 * @time: 2024-10-11 22:09:13
 * @description: 垃圾回收工具
 * @description: GC util
 * @version: JDK 1.8
 */
public class GcUtil {

  private static final Map<String, ExecutorService> EXECUTOR_MAP = new ConcurrentHashMap<>();

  /**
   * @author: Ares
   * @description: 以指定运行周期运行监控如果指定监控时间范围内Full GC次数达到阈值则执行指定操作
   * @description: Run the monitor at the specified running period. If the number of Full GCsreaches the threshold within the specified monitoring time range, execute the specified operation
   * @time: 2024-10-11 22:01:04
   * @params: [type, validatePeriod, validateDuration, gcThreshold, runnableWhenFullGc]
   * 类型，监控运行周期，监控时间范围（从当前时间起算），GC阈值，Full GC时执行的操作
   */
  public static void runWhenFullGc(String type, Duration validatePeriod, Duration validateDuration,
      int gcThreshold, Runnable runnableWhenFullGc) {
    runWhenGc(type, validatePeriod,
        new GcMonitor(validatePeriod, validateDuration, gcThreshold, runnableWhenFullGc, null));
  }

  /**
   * @author: Ares
   * @description: 以指定运行周期运行监控如果指定监控时间范围内Young GC次数达到阈值则执行指定操作
   * @description: Run the monitor at the specified running period. If the number of Young GCs reaches the threshold within the specified monitoring time range, execute the specified operation
   * @time: 2024-10-11 22:02:42
   * @params: [type, validatePeriod, validateDuration, gcThreshold, runnableWhenYoungGc]
   * 类型，监控运行周期，监控时间范围（从当前时间起算），GC阈值，Young GC时执行的操作
   */
  public static void runWhenYoungGc(String type, Duration validatePeriod, Duration validateDuration,
      int gcThreshold, Runnable runnableWhenYoungGc) {
    runWhenGc(type, validatePeriod,
        new GcMonitor(validatePeriod, validateDuration, gcThreshold, null, runnableWhenYoungGc));
  }

  private static void runWhenGc(String type, Duration validatePeriod, GcMonitor gcMonitor) {
    ThreadFactory gcMonitorFactory = new NameThreadFactory()
        .setNameFormat(type + "-Gc-Monitor-Thread-%d")
        .setDaemon(true)
        .build();
    ScheduledExecutorService monitorExecutor = new ScheduledThreadPoolExecutor(1, gcMonitorFactory);
    EXECUTOR_MAP.putIfAbsent(type, monitorExecutor);
    long millis = validatePeriod.toMillis();
    monitorExecutor.scheduleAtFixedRate(gcMonitor, millis, millis, TimeUnit.MILLISECONDS);
  }

  /**
   * @author: Ares
   * @description: 关闭指定类型的垃圾回收监控
   * @description: Shut down the garbage collection monitor of the specified type
   * @time: 2024-10-12 17:02:30
   * @params: [type] 类型
   */
  public static void shutdownGcMonitor(String type) {
    Optional.ofNullable(EXECUTOR_MAP.get(type)).ifPresent(executorService -> {
      executorService.shutdown();
      EXECUTOR_MAP.remove(type);
    });
  }

}
