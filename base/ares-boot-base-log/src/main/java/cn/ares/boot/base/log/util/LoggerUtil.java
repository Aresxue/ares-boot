package cn.ares.boot.base.log.util;

import static org.slf4j.spi.LocationAwareLogger.DEBUG_INT;
import static org.slf4j.spi.LocationAwareLogger.ERROR_INT;
import static org.slf4j.spi.LocationAwareLogger.INFO_INT;
import static org.slf4j.spi.LocationAwareLogger.WARN_INT;

import cn.ares.boot.util.common.thread.ThreadLocalMapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;
import org.springframework.boot.logging.DeferredLog;

/**
 * @author: Ares
 * @description: Log tool, use static method to print logs, no need to define log objects in each
 * class (Deferred log method name is incorrect because the stack information of the print log
 * thread cannot be obtained after asynchronous activation)
 * @description: 日志工具，使用静态方法打印日志，无需每个类中定义日志对象（Deferred log方法名不正确因为异步启用后拿不到打印日志线程的堆栈信息）
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

  public static void errorDeferred(String msg) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.error(msg);
    }
  }

  public static void errorDeferred(String msg, Throwable e) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.error(msg, e);
    }
  }

  public static void warnDeferred(String msg) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.warn(msg);
    }
  }

  public static void infoDeferred(String msg) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.info(msg);
    }
  }

  public static void debugDeferred(String msg) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.debug(msg);
    }
  }

  public static DeferredLog getDeferredLog() {
    // Get the class that calls the error, info, debug static classes
    // 获取调用error，info，debug静态类的类
    Class<?> clazz = getClazz(INVOKE_DEPTH);
    /*
     在SpringBoot加载的过程中 EnvironmentPostProcessor 的执行比较早; 这个时候日志系统根本就还没有初始化; 所以在此之前的日志操作都不会有效果; 使用
     DeferredLog 缓存日志；并在合适的时机回放日志
     */
    DeferredLog deferredLog = ThreadLocalMapUtil.get(clazz);
    if (null == deferredLog) {
      deferredLog = new DeferredLog();
      ThreadLocalMapUtil.set(clazz, deferredLog);
    }
    return deferredLog;
  }

  /**
   * @author: Ares
   * @description: Get locationAwareLogger
   * @time: 2021-05-31 16:48:00
   * @params: []
   * @return: org.slf4j.spi.LocationAwareLogger
   */
  private static LocationAwareLogger getLocationAwareLogger() {
    // Get the class that calls the error, info, debug static classes
    // 获取调用error，info，debug静态类的类
    Logger logger = LoggerFactory.getLogger(getClazz(INVOKE_DEPTH));
    return (LocationAwareLogger) logger;
  }

}
