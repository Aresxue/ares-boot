package cn.ares.boot.starter.search.engine.config;

import static cn.ares.boot.util.common.constant.StringConstant.FALSE;
import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.base.log.util.LoggerUtil;
import cn.ares.boot.util.common.primitive.BooleanUtil;
import cn.ares.boot.util.spring.EnvironmentUtil;
import cn.ares.boot.util.spring.SpringUtil;
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
 * @time: 2021-03-27 18:17:00
 * @description: 搜索引擎环境配置
 * @description: Search engine EnvironmentPostProcessor
 * @version: JDK 1.8
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 1_000)
@Role(value = ROLE_INFRASTRUCTURE)
public class SearchEngineEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final String SEARCH_ENGINE_PREFIX = "ares.search-engine.";
  protected static final String SEARCH_ENGINE_ENABLED = SEARCH_ENGINE_PREFIX + "enabled";
  private static final String SEARCH_ENGINE = "search-engine";

  private static final AtomicBoolean PROCESSED = new AtomicBoolean(false);

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {
    if (SpringUtil.isSpringCloudApplication(application)) {
      return;
    }
    if (PROCESSED.compareAndSet(false, true)) {
      try {
        Properties properties = new Properties();

        // TODO 从配置中心获取全局配置

        // 使用本地配置覆盖
        // Use local configuration to override
        properties.putAll(
            EnvironmentUtil.getPropertiesByPrefix(environment, SEARCH_ENGINE_PREFIX, true));

        // 如果被禁用了则关闭对应的健康检查
        // If it is disabled, close the corresponding health check
        String searchEngineEnable = properties.getProperty(SEARCH_ENGINE_ENABLED, TRUE);
        if (BooleanUtil.isFalse(searchEngineEnable)) {
          properties.put("management.health.elasticsearch.enabled", FALSE);
        }
        environment.getPropertySources()
            .addLast(new PropertiesPropertySource(SEARCH_ENGINE, properties));
        LoggerUtil.infoDeferred("search engine config load success");
      } catch (Exception exception) {
        LoggerUtil.errorDeferred("search engine config load fail: ", exception);
      }
    }
  }

}
