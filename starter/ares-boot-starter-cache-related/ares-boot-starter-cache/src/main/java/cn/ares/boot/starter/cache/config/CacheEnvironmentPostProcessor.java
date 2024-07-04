package cn.ares.boot.starter.cache.config;

import static cn.ares.boot.starter.cache.constant.CacheConstant.APPLICATION_CACHE_REDISSON_ENABLE;
import static cn.ares.boot.util.common.constant.MiddlewareType.CACHE;
import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.base.log.util.LoggerUtil;
import cn.ares.boot.util.common.primitive.BooleanUtil;
import cn.ares.boot.util.spring.EnvironmentUtil;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * @author: Ares
 * @time: 2024-07-04 20:10:01
 * @description: 缓存环境信息加载
 * @description: Cache environment information loading
 * @version: JDK 1.8
 */
@Configuration
@ConditionalOnProperty(name = "ares.application.cache.enabled", havingValue = TRUE, matchIfMissing = true)
@Import(value = {CacheSerializerConfiguration.class, CacheManagerConfiguration.class})
@Order(Ordered.LOWEST_PRECEDENCE - 10_000)
@Role(value = ROLE_INFRASTRUCTURE)
public class CacheEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final AtomicBoolean PROCESSED = new AtomicBoolean(false);

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {
    if (PROCESSED.compareAndSet(false, true)) {
      Properties cacheConfig = new Properties();
      // 如果redisson非开启禁用RedissonAutoConfiguration
      if (BooleanUtil.isFalse(environment.getProperty(APPLICATION_CACHE_REDISSON_ENABLE, TRUE))) {
        EnvironmentUtil.addSpringExclude(environment, cacheConfig, RedissonAutoConfiguration.class);
      }
      environment.getPropertySources()
          .addFirst(new PropertiesPropertySource(CACHE.getName(), cacheConfig));
      LoggerUtil.infoDeferred("cache config load success");
    }
  }

}
