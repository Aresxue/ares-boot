package cn.ares.boot.util.common;


/**
 * @author: Ares
 * @time: 2021-05-13 10:14:00
 * @description: SnowFlake id util
 * @version: JDK 1.8
 */
public class SnowFlakeIdUtil {

  private static final SnowFlakeIdWorker SNOWFLAKE_ID_WORKER = new SnowFlakeIdWorker();

  /**
   * @author: Ares
   * @description: 获取分布式标识 (该方法是线程安全的)
   * @description: Get the distributed identity (this method is thread-safe)
   * @time: 2023-05-08 13:10:23
   * @params: []
   * @return: long 分布式标识
   */
  public static long nextId() {
    return SNOWFLAKE_ID_WORKER.nextId();
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
   * @description: A way to obtain distributed identities that is compatible with short clock
   * backticks (the method is thread-safe)
   * @time: 2023-05-08 13:09:45
   * @params: []
   * @return: long 分布式标识
   */
  public static long nextIdByCacheWhenClockMoved() {
    return SNOWFLAKE_ID_WORKER.nextIdByCacheWhenClockMoved();
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
