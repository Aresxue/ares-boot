package cn.ares.boot.util.log.builder;

import cn.ares.boot.util.log.constant.LoggerType;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2023-04-17 10:40:14
 * @description: Logger builder
 * @version: JDK 1.8
 */
public class LoggerBuilder {

  public static Map<String, Logger> buildLogger(String logConfigDir, String... loggerNames) {
    if (null == loggerNames || loggerNames.length == 0) {
      return Collections.emptyMap();
    }

    Map<String, Logger> loggerMap = new HashMap<>(4);
    ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    try {
      Class<? extends ILoggerFactory> classType = loggerFactory.getClass();
      LoggerType loggerType = LoggerType.getLoggerType(classType.getName());
      URL url = LoggerBuilder.class.getClassLoader()
          .getResource(logConfigDir + loggerType.getConfigFileName());
      switch (loggerType) {
        case LOG4j: {
          Class<?> domConfigurator = Class.forName("org.apache.log4j.xml.DOMConfigurator");
          MethodUtils.invokeStaticMethod(domConfigurator, "configure", url);

          for (String loggerName : loggerNames) {
            loggerMap.put(loggerName, loggerFactory.getLogger(loggerName));
          }
          break;
        }
        case LOG4j2:
          URI uri = url.toURI();
          Class<?> loggerContextClass = Class.forName(
              "org.apache.logging.log4j.core.LoggerContext");
          // 取loggerNames[0]作为loggerContext名称
          Object loggerContext = ConstructorUtils.invokeConstructor(loggerContextClass,
              loggerNames[0], null, uri);
          MethodUtils.invokeMethod(loggerContext, "setConfigLocation", uri);

          for (String loggerName : loggerNames) {
            Object defaultLogger = MethodUtils.invokeMethod(loggerContext, "getLogger", loggerName);

            if (null != defaultLogger) {
              Class<?> log4jLoggerClass = Class.forName("org.apache.logging.slf4j.Log4jLogger");
              Logger logger = (Logger) ConstructorUtils.invokeConstructor(log4jLoggerClass,
                  defaultLogger, loggerName);

              loggerMap.put(loggerName, logger);
            }
          }

          break;
        case LOGBACK: {
          Class<?> joranConfiguratorClass = Class.forName(
              "ch.qos.logback.classic.joran.JoranConfigurator");
          Object joranConfiguratorInstance = joranConfiguratorClass.newInstance();
          MethodUtils.invokeMethod(joranConfiguratorInstance, "setContext", loggerFactory);
          MethodUtils.invokeMethod(joranConfiguratorInstance, "doConfigure", url);

          for (String loggerName : loggerNames) {
            loggerMap.put(loggerName, loggerFactory.getLogger(loggerName));
          }
          break;
        }
        default:
      }
    } catch (Exception e) {
      e.printStackTrace();
      for (String loggerName : loggerNames) {
        loggerMap.put(loggerName, loggerFactory.getLogger(loggerName));
      }
    }
    return loggerMap;
  }

}
