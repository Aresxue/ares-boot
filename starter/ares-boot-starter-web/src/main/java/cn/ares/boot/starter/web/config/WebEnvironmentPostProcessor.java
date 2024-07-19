package cn.ares.boot.starter.web.config;

import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static org.springframework.boot.web.server.Shutdown.GRACEFUL;

import cn.ares.boot.base.config.BootEnvironment;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.spring.SpringUtil;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * @author: Ares
 * @time: 2021-07-23 11:12:00
 * @description: Web配置加载
 * @description: Web config load
 * @version: JDK 1.8
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 10_000)
@Role(value = ROLE_INFRASTRUCTURE)
public class WebEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final String WEB = "web";
  private static final String CONTEXT_PATH_KEY = "server.servlet.context-path";
  private static final String SERVER_SHUTDOWN_TYPE_KEY = "server.shutdown";
  private static final String TIMEOUT_PER_SHUTDOWN_PHASE_KEY = "spring.lifecycle.timeout-per-shutdown-phase";
  public static final String DEFAULT_TIMEOUT_PER_SHUTDOWN_PHASE = "30s";

  private static final AtomicBoolean PROCESSED = new AtomicBoolean(false);

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {
    if (SpringUtil.isSpringCloudApplication(application)) {
      return;
    }
    if (PROCESSED.compareAndSet(false, true)) {
      Properties properties = new Properties();
      String contextPath = environment.getProperty(CONTEXT_PATH_KEY);
      // 如果没有配置content-path默认为应用名
      // If the content-path is not set, the default is the application name
      if (StringUtil.isEmpty(contextPath)) {
        properties.put(CONTEXT_PATH_KEY, "/" + BootEnvironment.getAppName());
      }

      // 编码集指定为jvm默认编码集
      String defaultCharsetName = Charset.defaultCharset().name();
      properties.put("server.tomcat.uri-encoding", defaultCharsetName);
      properties.put("server.undertow.url-charset", defaultCharsetName);
      properties.put("server.servlet.encoding.charset", defaultCharsetName);
      properties.put("server.servlet.encoding.force", TRUE);
      properties.put("server.servlet.encoding.enabled", TRUE);

      // 增加优雅下线配置
      // Add graceful shutdown configuration
      String shutdownType = environment.getProperty(SERVER_SHUTDOWN_TYPE_KEY);
      if (StringUtil.isEmpty(shutdownType)) {
        properties.put(SERVER_SHUTDOWN_TYPE_KEY, GRACEFUL.name());
        String shutdownPhase = environment.getProperty(TIMEOUT_PER_SHUTDOWN_PHASE_KEY);
        if (StringUtil.isEmpty(shutdownPhase)) {
          properties.put(TIMEOUT_PER_SHUTDOWN_PHASE_KEY, DEFAULT_TIMEOUT_PER_SHUTDOWN_PHASE);
        }
      }


      // 接口文档配置

      environment.getPropertySources().addLast(new PropertiesPropertySource(WEB, properties));
    }
  }

}
