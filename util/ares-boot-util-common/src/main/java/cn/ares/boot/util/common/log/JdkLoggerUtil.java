package cn.ares.boot.util.common.log;

import static java.util.logging.Level.WARNING;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2024-01-18 19:19:38
 * @description: jdk日志对象工具
 * @description: Jdk logger util
 * @version: JDK 1.8
 */
public class JdkLoggerUtil {

  private static final Logger LOGGER = Logger.getLogger(JdkLoggerUtil.class.getCanonicalName());

  /**
   * @author: Ares
   * @description: 获取jdk日志对象
   * @time: 2024-01-18 19:24:22
   * @params: [clazz] 类型
   * @return: java.util.logging.Logger 日志对象
   */
  public static Logger getLogger(Class<?> clazz) {
    if (null == clazz) {
      return null;
    }
    Logger logger = Logger.getLogger(clazz.getCanonicalName());
    configFormatter(logger);
    return logger;
  }

  public static void configFormatter(Logger logger) {
    // 禁用原输出handler，否则会输出两次
    // Disable the original output handler, otherwise it will output twice
    logger.setUseParentHandlers(false);
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(new SimpleConsoleFormatter());
    logger.addHandler(consoleHandler);
  }

  public static void warn(Logger logger, Object msg, Throwable throwable) {
    logger.log(WARNING, String.valueOf(msg), throwable);
  }

  public static void info(Logger logger, Object msg) {
    logger.info(String.valueOf(msg));
  }

}
