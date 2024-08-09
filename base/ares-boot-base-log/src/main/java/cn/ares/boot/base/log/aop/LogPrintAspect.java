package cn.ares.boot.base.log.aop;

import static cn.ares.boot.util.common.constant.SymbolConstant.HYPHEN;
import static cn.ares.boot.util.common.constant.SymbolConstant.POUND;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.base.log.annotation.LogPrint;
import cn.ares.boot.base.log.util.LogIdHolder;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.json.JsonUtil;
import java.util.StringJoiner;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

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
// TODO 支持动态配置刷新列表
public class LogPrintAspect {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogPrintAspect.class);

  /**
   * @author: Ares
   * @description: 输出方法出入参到日志
   * @description: Print method request and response to log
   * @time: 2021-11-11 15:24:00
   * @params: [thisJoinPoint, logPrint] 插入点，注解
   * @return: java.lang.Object
   */
  @Around("@annotation(logPrint)")
  public Object logPrint(ProceedingJoinPoint thisJoinPoint, LogPrint logPrint) throws Throwable {
    // 输出方法请求参数
    String fullName = printRequest(thisJoinPoint, logPrint);
    // 执行原方法
    Object result = thisJoinPoint.proceed();
    // 输出方法返回值
    printResponse(logPrint, fullName, result);

    return result;
  }

  private String printRequest(ProceedingJoinPoint thisJoinPoint, LogPrint logPrint) {
    try {
      String methodName = thisJoinPoint.getSignature().getName();
      String clazzName = thisJoinPoint.getTarget().getClass().getCanonicalName();
      String fullName = clazzName + POUND + methodName;

      if (logPrint.printRequest()) {
        StringJoiner stringJoiner = new StringJoiner(HYPHEN);
        Object[] args = thisJoinPoint.getArgs();
        for (Object arg : args) {
          stringJoiner.add((null == arg ? "" : JsonUtil.toJsonString(arg)));
        }
        String requestOtherMessage = logPrint.requestOtherMessage();
        String logId = LogIdHolder.getLogId();
        if (StringUtil.isNotEmpty(logId)) {
          LOGGER.info("log id: {}, method: {}，{} request: {}", logId, fullName, requestOtherMessage,
              stringJoiner);
        } else {
          LOGGER.info("method: {}，{} request: {}", fullName, requestOtherMessage, stringJoiner);
        }
      }
      return fullName;
    } catch (Exception exception) {
      LOGGER.warn("print method request exception: ", exception);
    }
    return null;
  }

  private void printResponse(LogPrint logPrint, String fullName, Object result) {
    try {
      if (logPrint.printResponse()) {
        String responseOtherMessage = logPrint.responseOtherMessage();
        String logId = LogIdHolder.getLogId();
        if (StringUtil.isNotEmpty(logId)) {
          LOGGER.info("log id: {}, method: {}, {} response: {}", logId, fullName,
              responseOtherMessage, null == result ? "" : JsonUtil.toJsonString(result));
        } else {
          LOGGER.info("method: {}, {} response: {}", fullName, responseOtherMessage,
              null == result ? "" : JsonUtil.toJsonString(result));
        }
      }
    } catch (Exception exception) {
      LOGGER.warn("print method response exception: ", exception);
    }
  }

}
