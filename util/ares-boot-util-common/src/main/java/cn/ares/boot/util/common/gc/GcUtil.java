package cn.ares.boot.util.common.gc;

import cn.ares.boot.util.common.thread.NameThreadFactory;
import java.time.Duration;
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

  /**
   * @author: Ares
   * @description: 以指定运行周期运行监控如果指定监控时间范围内Full GC次数达到阈值则执行指定操作
   * @description: Run the monitor at the specified running period. If the number of Full GCs
   * reaches the threshold within the specified monitoring time range, execute the specified
   * operation
   * @time: 2024-10-11 22:01:04
   * @params: [validatePeriod, validateDuration, gcThreshold, runnableWhenFullGc]
   * 监控运行周期，监控时间范围（从当前时间起算），GC阈值，Full GC时执行的操作
   */
  public static void runWhenFullGc(Duration validatePeriod, Duration validateDuration,
      int gcThreshold, Runnable runnableWhenFullGc) {
    ThreadFactory gcMonitorFactory = new NameThreadFactory().setNameFormat("Gc-Monitor-Thread-%d")
        .setDaemon(true).build();
    ScheduledExecutorService monitorExecutor = new ScheduledThreadPoolExecutor(1, gcMonitorFactory);
    long millis = validatePeriod.toMillis();
    monitorExecutor.scheduleAtFixedRate(
        new GcMonitor(validatePeriod, validateDuration, gcThreshold, runnableWhenFullGc, null),
        millis, millis, TimeUnit.MILLISECONDS);
  }

  /**
   * @author: Ares
   * @description: 以指定运行周期运行监控如果指定监控时间范围内Young GC次数达到阈值则执行指定操作
   * @description: Run the monitor at the specified running period. If the number of Young GCs
   * reaches the threshold within the specified monitoring time range, execute the specified
   * operation
   * @time: 2024-10-11 22:02:42
   * @params: [validatePeriod, validateDuration, gcThreshold, runnableWhenYoungGc]
   * 监控运行周期，监控时间范围（从当前时间起算），GC阈值，Young GC时执行的操作
   */
  public static void runWhenYoungGc(Duration validatePeriod, Duration validateDuration,
      int gcThreshold, Runnable runnableWhenYoungGc) {
    ThreadFactory gcMonitorFactory = new NameThreadFactory().setNameFormat("Gc-Monitor-Thread-%d")
        .setDaemon(true).build();
    ScheduledExecutorService monitorExecutor = new ScheduledThreadPoolExecutor(1, gcMonitorFactory);
    long millis = validatePeriod.toMillis();
    monitorExecutor.scheduleAtFixedRate(
        new GcMonitor(validatePeriod, validateDuration, gcThreshold, null, runnableWhenYoungGc),
        millis, millis, TimeUnit.MILLISECONDS);
  }

}
