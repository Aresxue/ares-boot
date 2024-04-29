package cn.ares.boot.util.common;


import static cn.ares.boot.util.common.SnowFlakeIdWorker.DEFAULT_SEQUENCE_START_OFFSET;

import java.util.Map;

/**
 * @author: Ares
 * @time: 2021-05-13 10:14:00
 * @description: SnowFlake id util
 * @version: JDK 1.8
 */
public class SnowFlakeIdUtil {

  private static final Map<Long, SnowFlakeIdWorker> SNOWFLAKE_ID_WORKER_CACHE = MapUtil.newConcurrentMap();

  /**
   * @author: Ares
   * @description: 获取分布式标识 (该方法是线程安全的)
   * @description: Get the distributed identity (this method is thread-safe)
   * @time: 2023-05-08 13:10:23
   * @params: []
   * @return: long 分布式标识
   */
  public static long nextId() {
    return nextId(DEFAULT_SEQUENCE_START_OFFSET);
  }

  /**
   * @author: Ares
   * @description: 取分布式标识 (该方法是线程安全的)
   * @description: Get the distributed identity (this method is thread-safe)
   * @time: 2024-04-29 19:41:30
   * @params: [sequenceStartOffset] 毫秒内序列起始值
   * @return: long 分布式标识
   */
  public static long nextId(long sequenceStartOffset) {
    SnowFlakeIdWorker snowFlakeIdWorker = getSnowFlakeIdWorker(sequenceStartOffset);
    return snowFlakeIdWorker.nextId();
  }

  /**
   * @author: Ares
   * @description: 获取分布式标识字符串
   * @description: Gets the distributed identity string
   * @time: 2023-05-08 13:08:45
   * @params: []
   * @return: java.lang.String 分布式标识
   */
  public static String stringNextId() {
    return String.valueOf(nextId());
  }

  /**
   * @author: Ares
   * @description: 兼容短暂时钟回拨的获取分布式标识的方式 (该方法是线程安全的)
   * @description: A way to obtain distributed identities that is compatible with short clock backticks (the method is thread-safe)
   * @time: 2023-05-08 13:09:45
   * @params: []
   * @return: long 分布式标识
   */
  public static long nextIdByCacheWhenClockMoved() {
    return nextIdByCacheWhenClockMoved(DEFAULT_SEQUENCE_START_OFFSET);
  }

  /**
   * @author: Ares
   * @description: 兼容短暂时钟回拨的获取分布式标识的方式 (该方法是线程安全的)
   * @description: A way to obtain distributed identities that is compatible with short clock backticks (the method is thread-safe)
   * @time: 2024-04-29 19:42:51
   * @params: [sequenceStartOffset] 毫秒内序列起始值
   * @return: long 分布式标识
   */
  public static long nextIdByCacheWhenClockMoved(long sequenceStartOffset) {
    SnowFlakeIdWorker snowFlakeIdWorker = getSnowFlakeIdWorker(sequenceStartOffset);
    return snowFlakeIdWorker.nextIdByCacheWhenClockMoved();
  }

  private static SnowFlakeIdWorker getSnowFlakeIdWorker(long sequenceStartOffset) {
    return SNOWFLAKE_ID_WORKER_CACHE.computeIfAbsent(
        sequenceStartOffset, value -> {
          SnowFlakeIdWorker worker = new SnowFlakeIdWorker();
          worker.sequenceStartOffset(sequenceStartOffset);
          return worker;
        });
  }

  /**
   * @author: Ares
   * @description: 兼容短暂时钟回拨的获取分布式标识字符串的方式
   * @description: A method of obtaining distributed identity strings compatible with short clock
   * backs
   * @time: 2023-05-08 13:11:16
   * @params: []
   * @return: java.lang.String 分布式标识
   */
  public static String stringNextIdByCacheWhenClockMoved() {
    return String.valueOf(nextIdByCacheWhenClockMoved());
  }

}
