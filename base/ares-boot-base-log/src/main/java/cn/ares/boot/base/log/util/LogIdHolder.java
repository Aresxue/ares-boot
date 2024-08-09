package cn.ares.boot.base.log.util;

/**
 * @author: Ares
 * @time: 2022-02-17 13:56:25
 * @description: 日志主键持有者
 * @description: Log id holder
 * @version: JDK 1.8
 */
public class LogIdHolder {

  private static final InheritableThreadLocal<String> LOG_ID_REF = new InheritableThreadLocal<>();

  /**
   * @author: Ares
   * @description: 获取日志主键
   * @time: 2024-08-09 19:10:56
   */
  public static String getLogId() {
    return LOG_ID_REF.get();
  }

  /**
   * @author: Ares
   * @description: 设置日志主键
   * @time: 2024-08-09 19:10:56
   */
  public static void setLogId(String logId) {
    LOG_ID_REF.set(logId);
  }

  /**
   * @author: Ares
   * @description: 清理
   * @time: 2024-08-09 19:10:56
   */
  public static void clear() {
    LOG_ID_REF.remove();
  }

}
