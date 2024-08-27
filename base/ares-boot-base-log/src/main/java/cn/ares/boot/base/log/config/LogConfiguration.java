package cn.ares.boot.base.log.config;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.base.log.aop.LogPrintAspect;
import cn.ares.boot.util.common.thread.ThreadLocalMapUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.lang.NonNull;

/**
 * @author: Ares
 * @time: 2024-08-09 18:47:33
 * @description: 日志配置类
 * @description: Log configuration
 * @version: JDK 1.8
 */
@Configuration
@Import({LogPrintConfiguration.class, LogPrintAspect.class})
@Role(value = ROLE_INFRASTRUCTURE)
public class LogConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  @Override
  public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
    // 回放DeferredLog
    Map<Object, Object> threadLocalMap = ThreadLocalMapUtil.getThreadLocal();
    if (null != threadLocalMap) {
      List<String> removeClassNameList = new ArrayList<>(threadLocalMap.size());
      threadLocalMap.forEach((className, log) -> {
        if (log instanceof DeferredLog) {
          DeferredLog deferredLog = (DeferredLog) log;
          String tempClassName = className.toString();
          deferredLog.replayTo(LogFactory.getLog(tempClassName));
          removeClassNameList.add(tempClassName);
        }
      });
      removeClassNameList.forEach(threadLocalMap::remove);
      if (threadLocalMap.isEmpty()) {
        ThreadLocalMapUtil.remove();
      }
    }
  }

}
