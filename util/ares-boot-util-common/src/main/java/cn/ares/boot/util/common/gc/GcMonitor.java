package cn.ares.boot.util.common.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author: Ares
 * @time: 2024-10-11 22:08:24
 * @description: 垃圾回收监控
 * @description: GC monitor
 * @version: JDK 1.8
 */
public class GcMonitor implements Runnable {

  /**
   * GC阈值
   */
  private final int gcThreshold;
  /**
   * GC有效次数队列
   */
  private final Queue<Long> gcCountQueue;
  /**
   * GC有效次数的有效大小 GC effective number of valid size
   */
  private final int gcCountSize;
  /**
   * 最近的Young GC次数
   */
  private long lastYoungGcCount;
  /**
   * 最近的Full GC次数
   */
  private long lastFullGcCount;
  /**
   * 当FullGc次数达到阈值时执行的操作 Operation to be executed when the number of Full GCs reaches the threshold
   */
  private final Runnable runnableWhenFullGc;
  /**
   * 当YoungGc次数达到阈值时执行的操作 Operation to be executed when the number of Young GCs reaches the
   * threshold
   */
  private final Runnable runnableWhenYoungGc;

  protected GcMonitor(Duration validatePeriod, Duration validateDuration, int gcThreshold,
      Runnable runnableWhenFullGc, Runnable runnableWhenYoungGc) {
    this.gcThreshold = gcThreshold;
    this.gcCountQueue = new ArrayDeque<>();
    if (validateDuration.compareTo(validatePeriod) < 0) {
      this.gcCountSize = 1;
    } else {
      this.gcCountSize = (int) (validateDuration.toMillis() / validatePeriod.toMillis());
    }
    this.runnableWhenFullGc = runnableWhenFullGc;
    this.runnableWhenYoungGc = runnableWhenYoungGc;
    for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
      String gcName = gcBean.getName();
      long collectionCount = gcBean.getCollectionCount();
      // 根据具体GC名称来判断是否是Full GC
      // Determine whether it is a Full GC based on the specific GC name
      if (isFullGc(gcName)) {
        this.lastFullGcCount += collectionCount;
      } else {
        this.lastYoungGcCount += collectionCount;
      }
    }
  }

  private boolean isFullGc(String gcName) {
    return gcName.contains("Old") || gcName.contains("Full") || gcName.contains("Tenured")
        || "PS MarkSweep".equals(gcName);
  }

  @Override
  public void run() {
    long currentFullGcCount = 0;
    long currentYoungGcCount = 0;
    for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
      String gcName = gcBean.getName();
      long collectionCount = gcBean.getCollectionCount();
      if (isFullGc(gcName)) {
        currentFullGcCount += collectionCount;
      } else {
        currentYoungGcCount += collectionCount;
      }
    }

    long fullGcDifference = currentFullGcCount - lastFullGcCount;
    long youngGcDifference = currentYoungGcCount - lastYoungGcCount;
    lastFullGcCount = currentFullGcCount;
    lastYoungGcCount = currentYoungGcCount;

    // 如果有效大小已满则移除最早的GC次数类似滑动窗口
    // If the valid size is full, remove the earliest GC count, similar to a sliding window
    if (gcCountQueue.size() >= gcCountSize) {
      gcCountQueue.poll();
    }
    gcCountQueue.add(runnableWhenFullGc != null ? fullGcDifference : youngGcDifference);

    long totalGcCount = gcCountQueue.stream().mapToLong(Long::longValue).sum();

    if (totalGcCount >= gcThreshold) {
      if (runnableWhenFullGc != null) {
        runnableWhenFullGc.run();
      }
      if (runnableWhenYoungGc != null) {
        runnableWhenYoungGc.run();
      }
    }
  }
}