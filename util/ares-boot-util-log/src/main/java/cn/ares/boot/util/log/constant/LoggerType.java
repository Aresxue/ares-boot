package cn.ares.boot.util.log.constant;

import cn.ares.boot.util.common.MapUtil;
import java.util.Map;

/**
 * @author: Ares
 * @time: 2023-04-14 17:01:21
 * @description: 日志类型
 * @description: Logger type
 * @version: JDK 1.8
 */
public enum LoggerType {
  /**
   * logger type
   */
  LOG4j("log4j.xml", "org.slf4j.impl.Log4jLoggerFactory"),
  LOG4j2("log4j2.xml", "org.apache.logging.slf4j.Log4jLoggerFactory"),
  LOGBACK("logback.xml", "ch.qos.logback.classic.LoggerContext");

  private static final Map<String, LoggerType> CACHED = MapUtil.newMap(values().length);

  static {
    for (LoggerType loggerType : values()) {
      CACHED.put(loggerType.getConfigFileName(), loggerType);
    }
  }

  private final String configFileName;
  private final String loggerFactoryClassName;

  LoggerType(String configLocation, String loggerFactory) {
    this.configFileName = configLocation;
    this.loggerFactoryClassName = loggerFactory;
  }

  public String getConfigFileName() {
    return configFileName;
  }

  public String getLoggerFactory() {
    return loggerFactoryClassName;
  }

  public static LoggerType getLoggerType(String loggerFactoryClassName) {
    return CACHED.getOrDefault(loggerFactoryClassName, LOGBACK);
  }

}