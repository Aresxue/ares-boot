package cn.ares.boot.base.log.aop;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.base.log.annotation.LogPrint;
import cn.ares.boot.base.log.config.LogPrintConfiguration;
import cn.ares.boot.base.log.util.LogPrintIgnoreAnnotationIntrospector;
import cn.ares.boot.util.common.ExceptionUtil;
import cn.ares.boot.util.common.thread.ThreadUtil;
import cn.ares.boot.util.json.JsonUtil;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import javax.annotation.PostConstruct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author: Ares
 * @time: 2021-11-11 15:15:00
 * @description: 日志打印切面
 * @description: Log print aspect
 * @version: JDK 1.8
 */
@ConditionalOnClass(Aspect.class)
@Aspect
@Configuration
@Role(value = ROLE_INFRASTRUCTURE)
public class LogPrintAspect {

  private static final JsonMapper JSON_MAPPER = JsonUtil.getDefaultJsonMapper();

  static {
    JSON_MAPPER.setAnnotationIntrospector(new LogPrintIgnoreAnnotationIntrospector());
  }

  private final LogPrintConfiguration logPrintConfiguration;
  private ExecutorService executorService;

  @PostConstruct
  public void init() {
    if (logPrintConfiguration.isAsyncEnabled()) {
      Integer workerNum = logPrintConfiguration.getAsyncWorkerNum();
      if (null == workerNum) {
        workerNum = ThreadUtil.getLargeThreadCount();
      }
      executorService = ThreadUtil.getExecutorService("Log-Print-Pool-%d", workerNum, 1024,
          new DiscardPolicy());
    }
  }

  /**
   * @author: Ares
   * @description: 输出方法出入参到日志
   * @description: Print method request and response to log
   * @time: 2021-11-11 15:24:00
   * @params: [joinPoint, logPrint] 插入点，注解
   * @return: java.lang.Object
   */
  @Around("@within(logPrint) || @annotation(logPrint)")
  public Object logPrint(ProceedingJoinPoint joinPoint, LogPrint logPrint) throws Throwable {
    if (null == logPrint) {
      Class<?> targetClass = joinPoint.getTarget().getClass();
      logPrint = AnnotationUtils.getAnnotation(targetClass, LogPrint.class);
    }
    if (null == logPrint) {
      return joinPoint.proceed();
    }

    Object response = null;
    Throwable throwable = null;
    try {
      response = joinPoint.proceed();
      return response;
    } catch (Throwable t) {
      throwable = t;
      throw t;
    } finally {
      Object finalResponse = response;
      Throwable finalThrowable = throwable;
      LogPrint finalLogPrint = logPrint;
      ExceptionUtil.run(() -> {
        if (logPrintConfiguration.isAsyncEnabled()) {
          CompletableFuture.runAsync(() -> printLog(joinPoint, finalLogPrint, finalResponse,
              finalThrowable), executorService);
        } else {
          printLog(joinPoint, finalLogPrint, finalResponse, finalThrowable);
        }
      }, true);
    }
  }

  private void printLog(ProceedingJoinPoint point, LogPrint logPrint, Object result,
      Throwable throwable) {
    Logger logger = null;
    try {
      logger = LoggerFactory.getLogger(point.getSignature().getDeclaringType());
      if (isLogPrintEnabled(throwable)) {
        boolean printParams = logPrintConfiguration.isParamsEnabled() && logPrint.printParams();
        String methodName = point.getSignature().getName();
        if (logPrintConfiguration.isOnlyError()) {
          if (null != throwable) {
            String paramsStr = serialization(printParams, point.getArgs());
            logger.error("method: {}, params: {}, throwable: ", methodName, paramsStr, throwable);
          }
        } else {
          String paramsStr = serialization(printParams, point.getArgs());
          String resultStr = serialization(
              logPrintConfiguration.isResultEnabled() && logPrint.printResult(), result);
          printLog(logPrint.logLevel(), logger, "method: {}, params: {}, result: {}", methodName,
              paramsStr, resultStr);
        }
      }
    } catch (Exception exception) {
      if (null != logger) {
        logger.warn("print method log exception: ", exception);
      }
    }
  }

  private String serialization(boolean condition, Object obj) {
    if (condition) {
      String str = JsonUtil.toJsonString(JSON_MAPPER, obj);
      // 超过阈值不打印
      // Do not print if it exceeds the threshold
      int threshold = logPrintConfiguration.getThreshold();
      if (str.length() > threshold) {
        return "";
      }
      return str;
    }
    return "";
  }

  private boolean isLogPrintEnabled(Throwable throwable) {
    // 开关未开启直接返回false
    // Return false directly if the switch is not turned on
    if (!logPrintConfiguration.isEnabled()) {
      return false;
    }
    // 有异常则一定打印
    // Print if there is an exception
    if (null != throwable) {
      return true;
    }
    // 采样
    int sampleRate = logPrintConfiguration.getSampleRate();
    if (sampleRate <= 0) {
      return false;
    }
    if (sampleRate >= 10_000) {
      return true;
    }
    // 随机采样
    return ThreadLocalRandom.current().nextInt(0, 10_000) < sampleRate;
  }

  private void printLog(LogLevel logLevel, Logger logger, String format, Object... arguments) {
    switch (logLevel) {
      case INFO:
        logger.info(format, arguments);
        break;
      case DEBUG:
        logger.debug(format, arguments);
        break;
      case WARN:
        logger.warn(format, arguments);
        break;
      case ERROR:
        logger.error(format, arguments);
        break;
      case TRACE:
        logger.trace(format, arguments);
        break;
      default:
        logger.info(format, arguments);
        break;
    }
  }


  public LogPrintAspect(LogPrintConfiguration logPrintConfiguration) {
    this.logPrintConfiguration = logPrintConfiguration;
  }

}
