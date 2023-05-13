package cn.ares.boot.util.log.constant;

import java.util.Arrays;

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
    return Arrays.stream(LoggerType.values())
        .filter(
            loggerType -> loggerFactoryClassName.equals(loggerType.getLoggerFactory()))
        .findFirst().orElse(LOGBACK);
  }

}