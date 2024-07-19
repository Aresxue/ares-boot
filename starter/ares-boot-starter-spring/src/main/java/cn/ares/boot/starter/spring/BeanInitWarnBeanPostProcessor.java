package cn.ares.boot.starter.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;

/**
 * @author: Ares
 * @time: 2022-06-02 16:12:02
 * @description: warn when bean init over limit time
 * @description: bean初始化耗时超过一定时间告警
 * @version: JDK 1.8
 */
@ConditionalOnProperty(name = "ares.spring.bean-init-warn.enabled", havingValue = "true")
public class BeanInitWarnBeanPostProcessor implements BeanPostProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeanInitWarnBeanPostProcessor.class);

  private Long startTime;

  @Value("${ares.spring.bean-init-limit-time:200}")
  private int timeLimit;

  @Override
  public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
      throws BeansException {
    long useTime = System.currentTimeMillis() - startTime;
    if (useTime > timeLimit) {
      LOGGER.warn("bean: {} over init limit time, use time: {}", beanName, useTime);
    }
    return bean;
  }

  @Override
  public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
      throws BeansException {
    startTime = System.currentTimeMillis();
    return bean;
  }

}
