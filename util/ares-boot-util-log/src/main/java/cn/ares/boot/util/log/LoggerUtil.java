package cn.ares.boot.util.log;

import static org.slf4j.spi.LocationAwareLogger.DEBUG_INT;
import static org.slf4j.spi.LocationAwareLogger.ERROR_INT;
import static org.slf4j.spi.LocationAwareLogger.INFO_INT;
import static org.slf4j.spi.LocationAwareLogger.WARN_INT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author: Ares
 * @description: 日志工具，使用静态方法打印日志，无需每个类中定义日志对象
 * @description: The logging tool uses static methods to print logs without the need to define log
 * objects in each class
 * @time: 2021-04-12 17:38:00
 * @version: JDK 1.8
 */
public class LoggerUtil {

  private static final String FQCN = LoggerUtil.class.getName();

  private static final int INVOKE_DEPTH = 4;

  public static void error(String msg) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    locationAwareLogger.log(null, FQCN, ERROR_INT, msg, null, null);
  }

  public static void error(String msg, Object... obj) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    Throwable throwable = null;
    for (Object param : obj) {
      if (param instanceof Throwable) {
        throwable = (Throwable) param;
      }
    }
    locationAwareLogger.log(null, FQCN, ERROR_INT, msg, obj, throwable);
  }

  public static void error(String msg, Throwable e) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    locationAwareLogger.log(null, FQCN, ERROR_INT, msg, null, e);
  }

  public static void warn(String msg) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    locationAwareLogger.log(null, FQCN, WARN_INT, msg, null, null);
  }


  public static void warn(String msg, Object... obj) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    Throwable throwable = null;
    for (Object param : obj) {
      if (param instanceof Throwable) {
        throwable = (Throwable) param;
      }
    }
    locationAwareLogger.log(null, FQCN, WARN_INT, msg, obj, throwable);
  }


  public static void info(String msg) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    locationAwareLogger.log(null, FQCN, INFO_INT, msg, null, null);
  }


  public static void info(String msg, Object... obj) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    locationAwareLogger.log(null, FQCN, INFO_INT, msg, obj, null);
  }

  public static void info(Logger logger, String msg, Object... obj) {
    if (logger.isInfoEnabled()) {
      logger.info(msg, obj);
    }
  }

  public static void warn(Logger logger, String msg, Object... obj) {
    if (logger.isWarnEnabled()) {
      logger.warn(msg, obj);
    }
  }

  public static void error(Logger logger, String msg, Object... obj) {
    if (logger.isErrorEnabled()) {
      logger.error(msg, obj);
    }
  }

  public static void debug(Logger logger, String msg, Object... obj) {
    if (logger.isDebugEnabled()) {
      logger.debug(msg, obj);
    }
  }

  public static void debug(String msg) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    locationAwareLogger.log(null, FQCN, DEBUG_INT, msg, null, null);
  }


  public static void debug(String msg, Object... obj) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    locationAwareLogger.log(null, FQCN, DEBUG_INT, msg, obj, null);
  }


  /**
   * @author: Ares
   * @description: Get the class at the specified stack depth
   * @description: 获取指定堆栈深度的类
   * @time: 2022-06-09 16:31:57
   * @params: [depth] 深度
   * @return: java.lang.Class<?> 类
   */
  public static Class<?> getClazz(int depth) {
    return new SecurityManager() {
      public Class<?> securityGetClazz() {
        return getClassContext()[depth];
      }
    }.securityGetClazz();
  }

  /**
   * @author: Ares
   * @description: Get locationAwareLogger
   * @time: 2021-05-31 16:48:00
   * @params: []
   * @return: org.slf4j.spi.LocationAwareLogger
   */
  private static LocationAwareLogger getLocationAwareLogger() {
    // 获取调用error、info、warn、debug静态类的类
    // Get the class that calls the error, info, debug static classes
    Logger logger = LoggerFactory.getLogger(getClazz(INVOKE_DEPTH));
    return (LocationAwareLogger) logger;
  }

}
