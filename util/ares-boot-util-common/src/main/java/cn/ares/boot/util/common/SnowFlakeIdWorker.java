package cn.ares.boot.util.common;

import cn.ares.boot.util.common.network.NetworkUtil;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: Ares
 * @time: 2019-09-17 19:26:00
 * @description: SnowFlakeIdWorker
 */
public class SnowFlakeIdWorker {

  /**
   * 默认开始时间截 (2021-09-06)
   * Default start timestamp (2021-09-06)
   */
  private static final long ARES_EPOCH = DateUtil.getMicrosecond("2021-09-06 00:00:00") / 1000;
  /**
   * 机器id所占的位数
   * The number of digits occupied by the machine id
   */
  private static final long WORKER_ID_BITS = 5L;
  /**
   * 数据标识id所占的位数
   * The number of digits occupied by the data identification id
   */
  private static final long DATA_CENTER_ID_BITS = 5L;
  /**
   * 支持的最大机器id，结果是31(这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
   * The largest supported machine id, the result is 31 (this shift algorithm can quickly calculate the largest decimal number that can be represented by a few binary digits)
   */
  private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
  /**
   * 支持的最大数据标识id，结果是31
   * The maximum supported data identifier id, the result is 31
   */
  private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
  /**
   * 序列在id中占的位数
   * The number of digits the sequence occupies in the id
   */
  private static final long SEQUENCE_BITS = 12L;
  /**
   * 机器ID向左移12位
   * Machine ID is shifted 12 bits to the left
   */
  private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
  /**
   * 数据标识id向左移17位(12+5)
   * The data identification id is shifted to the left by 17 bits (12+5)
   */
  private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
  /**
   * 时间截向左移22位(5+5+12)
   * Shift the time cut to the left by 22 bits (5+5+12)
   */
  private static final long TIMESTAMP_LEFT_SHIFT =
      SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
  /**
   * 生成序列的掩码，这里为4095(0b111111111111=0xfff=4095)
   * Mask of the generated sequence, here 4095 (0b111111111111=0xfff=4095)
   */
  private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
  /**
   * 序列缓存默认大小
   * Sequence cache default size
   */
  private static final int DEFAULT_SEQUENCE_CACHE_SIZE = 2000;
  /**
   * 默认毫秒内序列起始值
   * Sequence default start offset
   */
  protected static final long DEFAULT_SEQUENCE_START_OFFSET = 0L;
  /**
   * 工作机器ID(0~31)
   * Work machine ID (0~31)
   */
  private final long workerId;
  /**
   * 数据中心ID(0~31)
   * Data center ID (0~31)
   */
  private final long dataCenterId;
  /**
   * 时间起点
   * Start timestamp
   */
  private final long epoch;
  /**
   * 序列缓存
   * Sequence cache
   */
  private final long[] sequenceCache;
  /**
   * 毫秒内序列(0~4095)
   * Sequence within milliseconds (0~4095)
   */
  private long sequence = 0L;
  /**
   * 上次生成ID的时间截
   * The last time the ID was generated
   */
  private long lastTimestamp = -1L;

  private final Lock nextIdLock = new ReentrantLock();
  private final Lock nextIdByCacheWhenClockMovedLock = new ReentrantLock();
  /**
   * 毫秒内序列起始值（主要解决可以处理主键取余的数据倾斜问题）
   * The starting value of the sequence within milliseconds (mainly solves the problem of data skew that can handle the remainder of the primary key)
   */
  private long sequenceStartOffset = DEFAULT_SEQUENCE_START_OFFSET;

  /**
   * 构造函数 Constructor
   */
  protected SnowFlakeIdWorker() {
    this(getWorkId());
  }

  /**
   * 构造函数 Constructor
   *
   * @param workerId 工作ID (0~31)
   */
  private SnowFlakeIdWorker(long workerId) {
    this(workerId, 0);
  }

  /**
   * Constructor 构造函数
   *
   * @param workerId     工作ID (0~31)
   * @param dataCenterId 数据中心ID (0~31)
   */
  public SnowFlakeIdWorker(long workerId, long dataCenterId) {
    this(workerId, dataCenterId, ARES_EPOCH);
  }

  public SnowFlakeIdWorker(long workerId, long dataCenterId, long epoch) {
    this(workerId, dataCenterId, epoch, DEFAULT_SEQUENCE_CACHE_SIZE);
  }

  public SnowFlakeIdWorker(long workerId, long dataCenterId, int sequenceCacheSize) {
    this(workerId, dataCenterId, ARES_EPOCH, sequenceCacheSize);
  }

  public SnowFlakeIdWorker(long workerId, long dataCenterId, long epoch, int sequenceCacheSize) {
    if (workerId > MAX_WORKER_ID || workerId < 0) {
      throw new IllegalArgumentException(
          String.format("Worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
    }
    if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
      throw new IllegalArgumentException(
          String.format("DataCenter Id can't be greater than %d or less than 0",
              MAX_DATA_CENTER_ID));
    }
    this.workerId = workerId;
    this.dataCenterId = dataCenterId;
    this.epoch = epoch;
    this.sequenceCache = new long[sequenceCacheSize];
  }

  /**
   * @author: Ares
   * @description: 获取工作ID
   * @time: 2020-09-01 15:26:00
   * @params: []
   * @return: java.lang.Long 工作标识
   */
  private static Long getWorkId() {
    int[] macArr;
    try {
      macArr = toCodePoints(NetworkUtil.getMac());
    } catch (SocketException | UnknownHostException e) {
      throw new RuntimeException("SnowFlake Id worker get mac fail", e);
    }
    int sums = 0;
    for (int i : Objects.requireNonNull(macArr)) {
      sums += i;
    }
    return (long) (sums % 32);
  }

  /**
   * @author: Ares
   * @description: 获取分布式标识 (该方法是线程安全的)
   * @description: Get the distributed identity (this method is thread-safe)
   * @time: 2023-05-08 13:10:23
   * @params: []
   * @return: long 分布式标识
   */
  public long nextId() {
    nextIdLock.lock();
    try {
      long timestamp = timeGen();

      // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
      // If the current time is less than the timestamp of the last ID generation,
      // it means that the system clock should be thrown back after this time.
      if (timestamp < this.lastTimestamp) {
        throw new RuntimeException(String.format(
            "Clock moved backwards, refusing to generate id for %d ms, last time is %d ms, current time is %d ms.",
            lastTimestamp - timestamp, lastTimestamp, timestamp));
      }

      // 如果是同一时间生成的，则进行毫秒内序列重置
      // If generated at the same time, reset the sequence within milliseconds
      if (this.lastTimestamp == timestamp) {
        this.sequence = (this.sequence + 1) & SEQUENCE_MASK;
        // 毫秒内序列溢出
        // Sequence overflow in milliseconds
        if (this.sequence == 0) {
          // 阻塞到下一个毫秒,获得新的时间戳
          // Block until the next millisecond, get a new timestamp
          sequence = getSequenceStartOffset();
          timestamp = tilNextMillis(lastTimestamp);
        }
        // 时间戳改变，毫秒内序列重置
      }
      // Timestamp changed, sequence reset in milliseconds
      else {
        this.sequence = getSequenceStartOffset();
      }

      // 上次生成ID的时间截
      // The last time the ID was generated
      lastTimestamp = timestamp;

      // 移位并通过或运算拼到一起组成64位的ID
      // Shift and OR together form a 64-bit ID
      return allocate(timestamp - this.epoch);
    } finally {
      nextIdLock.unlock();
    }
  }

  /**
   * @author: Ares
   * @description: 兼容短暂时钟回拨的获取分布式标识的方式 (该方法是线程安全的)
   * @description: A way to obtain distributed identities that is compatible with short clock
   * backticks (the method is thread-safe)
   * @time: 2023-05-08 13:09:45
   * @params: []
   * @return: long 分布式标识
   */
  public long nextIdByCacheWhenClockMoved() {
    nextIdByCacheWhenClockMovedLock.lock();
    try {
      long timestamp = timeGen();
      int sequenceCacheSize = this.sequenceCache.length;
      int index = (int) (timestamp % sequenceCacheSize);
      // 出现时钟回拨问题，获取历史序列号自增
      // There is a clock callback problem, and the historical serial number is automatically incremented
      if (timestamp < this.lastTimestamp) {
        long tempSequence;
        do {
          if ((this.lastTimestamp - timestamp) > sequenceCacheSize) {
            // 可自定义异常、告警等，短暂不能对外提供，故障转移，将请求转发到正常机器
            // Can customize exceptions, alarms, etc., can not be provided externally for a short time,
            // failover, and forward requests to normal machines
            throw new UnsupportedOperationException(
                String.format("The time back range is too large and exceeds %dms caches",
                    sequenceCacheSize));
          }
          long preSequence = this.sequenceCache[index];
          tempSequence = (preSequence + 1) & SEQUENCE_MASK;
          if (tempSequence == 0) {
            // 如果取出的历史序列号+1后已经达到超过最大值，则重新获取timestamp,重新拿其他位置的缓存
            // If the retrieved historical serial number +1 has reached the maximum value,
            // re-acquire the timestamp and re-fetch the cache in other locations
            timestamp = tilNextMillis(this.lastTimestamp);
            index = (int) (timestamp % sequenceCacheSize);
          } else {
            // 更新缓存
            // Refresh cache
            this.sequenceCache[index] = tempSequence;
            return allocate((timestamp - this.epoch), tempSequence);
          }
        } while (timestamp < this.lastTimestamp);
        // 如果在获取缓存的过程中timestamp恢复正常了，就走正常流程
        // If the timestamp returns to normal during the process of obtaining the cache, go to the normal process
      }
      // 时间等于上一次的时间戳，取当前的序列加1
      // The time is equal to lastTimestamp, take the current sequence + 1
      if (timestamp == this.lastTimestamp) {
        this.sequence = (this.sequence + 1) & SEQUENCE_MASK;
        // Exceed the max sequence, we wait the next second to generate id
        if (this.sequence == 0) {
          sequence = getSequenceStartOffset();
          timestamp = tilNextMillis(this.lastTimestamp);
          index = (int) (timestamp % sequenceCacheSize);
        }
      } else {
        // 时间大于上一次的时间戳没有发生回拨，序列从0开始
        // No callback occurs when the time is greater than the last timestamp, and the sequence starts from 0
        this.sequence = getSequenceStartOffset();
      }
      // 缓存序列且更新上一次的时间戳
      // Cache the sequence and update the last timestamp
      this.sequenceCache[index] = this.sequence;
      this.lastTimestamp = timestamp;
      return allocate(timestamp - this.epoch);
    } finally {
      nextIdByCacheWhenClockMovedLock.unlock();
    }
  }


  private long allocate(long deltaSeconds) {
    return allocate(deltaSeconds, this.sequence);
  }

  /**
   * @author: Ares
   * @description: 移位并通过或运算拼到一起组成64位的ID
   * @description: Shift and OR together form a 64-bit ID
   * @time: 2022-06-20 11:51:56
   * @params: [deltaMillisSeconds, sequence] 距离epoch的时间，序列
   * @return: long id
   */
  private long allocate(long deltaMillisSeconds, long sequence) {
    return (deltaMillisSeconds << TIMESTAMP_LEFT_SHIFT) | (dataCenterId << DATA_CENTER_ID_SHIFT) | (
        workerId << WORKER_ID_SHIFT) | sequence;
  }

  /**
   * 阻塞到下一个毫秒，直到获得新的时间戳 Block until the next millisecond until a new timestamp is obtained
   *
   * @param lastTimestamp 上次生成ID的时间截
   * @return 当前时间戳
   */
  protected long tilNextMillis(long lastTimestamp) {
    long timestamp = timeGen();
    while (timestamp <= lastTimestamp) {
      timestamp = timeGen();
    }
    return timestamp;
  }

  /**
   * 返回以毫秒为单位的当前时间 Returns the current time in milliseconds
   *
   * @return 当前时间(毫秒)
   */
  protected long timeGen() {
    return System.currentTimeMillis();
  }

  private static int[] toCodePoints(CharSequence str) {
    if (str == null) {
      return null;
    } else if (str.length() == 0) {
      return new int[0];
    } else {
      String s = str.toString();
      int[] result = new int[s.codePointCount(0, s.length())];
      int index = 0;

      for (int i = 0; i < result.length; ++i) {
        result[i] = s.codePointAt(index);
        index += Character.charCount(result[i]);
      }

      return result;
    }
  }

  public void sequenceStartOffset(long sequenceStartOffset) {
    this.sequenceStartOffset = sequenceStartOffset;
  }

  public long getSequenceStartOffset() {
    if (0L == sequenceStartOffset) {
      return 0L;
    }
    return ThreadLocalRandom.current().nextLong(sequenceStartOffset);
  }


}
