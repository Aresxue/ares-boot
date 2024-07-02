package cn.ares.boot.base.config;

import static cn.ares.boot.base.config.constant.BaseConfigConstant.APP_NAME_KEY;
import static cn.ares.boot.base.config.constant.BaseConfigConstant.BOOT_APP_NAME_KEY;
import static cn.ares.boot.base.config.constant.BaseConfigConstant.FILE_ENCODING_KEY;
import static cn.ares.boot.base.config.constant.BaseConfigConstant.PROJECT_NAME_KEY;
import static cn.ares.boot.util.common.constant.StringConstant.UNKNOWN;
import static cn.ares.boot.version.AresBootVersion.VERSION_KEY;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.util.common.PropertyUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.spring.SpringUtil;
import cn.ares.boot.version.AresBootVersion;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.NonNull;

/**
 * @author: Ares
 * @time: 2024-07-02 17:50:52
 * @description: 框架环境
 * @description: Boot environment
 * @version: JDK 1.8
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 1_000)
@Role(value = ROLE_INFRASTRUCTURE)
public class BootEnvironment implements EnvironmentPostProcessor,
    ApplicationListener<ApplicationEvent> {

  /**
   * 在SpringBoot加载的过程中 EnvironmentPostProcessor 的执行比较早; 这个时候日志系统根本就还没有初始化; 所以在此之前的日志操作都不会有效果; 使用
   * DeferredLog 缓存日志；并在合适的时机回放日志
   */
  private static final DeferredLog LOGGER = new DeferredLog();

  /**
   * 应用已就绪
   */
  private static final AtomicBoolean APPLICATION_READY = new AtomicBoolean();

  private static String appName = UNKNOWN;

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {
    // 设置应用名称
    setAppName(environment);
    // 设置系统属性
    setSystemProperty();
  }

  private void setSystemProperty() {
    // 设置项目名称
    if (StringUtil.isBlank(System.getProperty(PROJECT_NAME_KEY))) {
      System.setProperty(PROJECT_NAME_KEY, appName);
    }
    if (StringUtil.isBlank(Charset.defaultCharset().displayName())) {
      // 不存在时设置编码集为UTF-8
      System.setProperty(FILE_ENCODING_KEY, UTF_8.name());
    }
    // 设置ares-boot版本
    System.setProperty(VERSION_KEY, AresBootVersion.getVersion());
  }

  private void setAppName(ConfigurableEnvironment environment) {
    // 先取app.name优先级最高
    String tempAppName = PropertyUtil.getSystemProperty(APP_NAME_KEY);
    if (StringUtil.isBlank(tempAppName)) {
      // 为空则取ares.application.name
      tempAppName = PropertyUtil.getSystemProperty(BOOT_APP_NAME_KEY);
    }
    if (StringUtil.isBlank(tempAppName)) {
      // 为空则尝试从spring环境中取
      tempAppName = environment.getProperty(BOOT_APP_NAME_KEY);
    }
    if (StringUtil.isNotBlank(tempAppName)) {
      appName = tempAppName;
    }
  }

  public static String getAppName() {
    return appName;
  }

  @Override
  public void onApplicationEvent(@NonNull ApplicationEvent applicationEvent) {
    if (applicationEvent instanceof ApplicationEnvironmentPreparedEvent) {
      LOGGER.replayTo(BootEnvironment.class);
    } else if (applicationEvent instanceof ApplicationReadyEvent) {
      ApplicationReadyEvent readyEvent = (ApplicationReadyEvent) applicationEvent;
      if (SpringUtil.isMainApplicationContext(readyEvent.getApplicationContext())) {
        APPLICATION_READY.compareAndSet(false, true);
      }
    }
  }

  public static boolean isApplicationReady() {
    return APPLICATION_READY.get();
  }

}
