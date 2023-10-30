package cn.ares.boot.util.log;

import static org.slf4j.spi.LocationAwareLogger.DEBUG_INT;
import static org.slf4j.spi.LocationAwareLogger.ERROR_INT;
import static org.slf4j.spi.LocationAwareLogger.INFO_INT;
import static org.slf4j.spi.LocationAwareLogger.WARN_INT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;
import sun.reflect.Reflection;

/**
 * @author: Ares
 * @description: 日志工具，使用静态方法打印日志，无需每个类中定义日志对象
 * @description: The logging tool uses static methods to print logs without the need to define log
 * objects in each class
 * @time: 2021-04-12 17:38:00
 * @version: JDK 1.8
 */
public class BaseLoggerUtil {

  private static final String FQCN = BaseLoggerUtil.class.getName();

  /**
   * 这里选取堆栈深度为3，为2的时候会带来些许的性能提升，但是代码可读性没有使用getLocationAwareLogger方法更好
   */
  protected static final int INVOKE_DEPTH = 3;

  public static void error(String msg) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isErrorEnabled()) {
      locationAwareLogger.log(null, FQCN, ERROR_INT, msg, null, null);
    }
  }

  public static void error(String msg, Supplier<?>... suppliers) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isErrorEnabled()) {
      log(msg, locationAwareLogger, ERROR_INT, suppliers);
    }
  }

  public static void error(String msg, Object... objects) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isErrorEnabled()) {
      log(msg, locationAwareLogger, ERROR_INT, objects);
    }
  }

  public static void error(String msg, Throwable e) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isErrorEnabled()) {
      locationAwareLogger.log(null, FQCN, ERROR_INT, msg, null, e);
    }
  }


  public static void error(Logger logger, String msg, Supplier<?>... suppliers) {
    if (logger.isErrorEnabled()) {
      logger.error(msg, getLogArguments(suppliers));
    }
  }

  public static void warn(String msg) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isWarnEnabled()) {
      locationAwareLogger.log(null, FQCN, WARN_INT, msg, null, null);
    }
  }


  public static void warn(String msg, Supplier<?>... suppliers) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isWarnEnabled()) {
      log(msg, locationAwareLogger, WARN_INT, suppliers);
    }
  }

  public static void warn(String msg, Object... objects) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isWarnEnabled()) {
      log(msg, locationAwareLogger, WARN_INT, objects);
    }
  }

  public static void warn(Logger logger, String msg, Supplier<?>... suppliers) {
    if (logger.isWarnEnabled()) {
      logger.warn(msg, getLogArguments(suppliers));
    }
  }


  public static void info(String msg) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isInfoEnabled()) {
      locationAwareLogger.log(null, FQCN, INFO_INT, msg, null, null);
    }
  }


  public static void info(String msg, Supplier<?>... suppliers) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isInfoEnabled()) {
      log(msg, locationAwareLogger, INFO_INT, suppliers);
    }
  }

  public static void info(String msg, Object... objects) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isInfoEnabled()) {
      log(msg, locationAwareLogger, INFO_INT, objects);
    }
  }

  public static void info(Logger logger, String msg, Supplier<?>... suppliers) {
    if (logger.isInfoEnabled()) {
      logger.info(msg, getLogArguments(suppliers));
    }
  }

  public static void debug(String msg) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isDebugEnabled()) {
      locationAwareLogger.log(null, FQCN, DEBUG_INT, msg, null, null);
    }
  }

  public static void debug(String msg, Supplier<?>... suppliers) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isDebugEnabled()) {
      log(msg, locationAwareLogger, DEBUG_INT, suppliers);
    }
  }

  public static void debug(String msg, Object... objects) {
    LocationAwareLogger locationAwareLogger = getLocationAwareLogger();
    if (locationAwareLogger.isDebugEnabled()) {
      log(msg, locationAwareLogger, DEBUG_INT, objects);
    }
  }

  public static void debug(Logger logger, String msg, Supplier<?>... suppliers) {
    if (logger.isDebugEnabled()) {
      logger.debug(msg, getLogArguments(suppliers));
    }
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
    // getCallerClass的性能在7个方式中是最好的，但目前实现仅支持jdk8，后续更高jdk版本需使用其他方式
    Logger logger = LoggerFactory.getLogger(Reflection.getCallerClass(INVOKE_DEPTH));
    return (LocationAwareLogger) logger;
  }

  private static void log(String msg, LocationAwareLogger locationAwareLogger, int logLevel,
      Object[] objects) {
    List<Object> paramList = new ArrayList<>();
    Throwable throwable = null;
    for (Object param : objects) {
      if (param instanceof Supplier) {
        param = ((Supplier<?>) param).get();
      }
      if (param instanceof Throwable) {
        throwable = (Throwable) param;
      } else {
        paramList.add(param);
      }
    }
    locationAwareLogger.log(null, FQCN, logLevel, msg, paramList.toArray(new Object[0]), throwable);
  }


  private static Object[] getLogArguments(Supplier<?>... suppliers) {
    return Arrays.stream(suppliers).map(Supplier::get).toArray(Object[]::new);
  }

}
