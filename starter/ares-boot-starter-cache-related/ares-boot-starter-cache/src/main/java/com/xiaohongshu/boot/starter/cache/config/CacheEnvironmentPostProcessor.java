package com.xiaohongshu.boot.starter.cache.config;

import static com.xiaohongshu.boot.starter.cache.constant.CacheConstant.APPLICATION_CACHE_ENABLE;
import static com.xiaohongshu.boot.starter.cache.constant.CacheConstant.APPLICATION_CACHE_POOL_PREFIX;
import static com.xiaohongshu.boot.starter.cache.constant.CacheConstant.APPLICATION_CACHE_PREFIX;
import static com.xiaohongshu.boot.starter.cache.constant.CacheConstant.APPLICATION_CACHE_REDISSON_ENABLE;
import static com.xiaohongshu.boot.starter.cache.constant.CacheConstant.SPRING_REDIS;
import static cn.ares.boot.util.common.constant.MiddlewareType.CACHE;
import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.base.log.util.LoggerUtil;
import cn.ares.boot.util.common.primitive.BooleanUtil;
import cn.ares.boot.util.spring.EnvironmentUtil;
import cn.ares.boot.util.spring.SpringUtil;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
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
@ConditionalOnProperty(name = APPLICATION_CACHE_ENABLE, havingValue = TRUE, matchIfMissing = true)
@Order(Ordered.LOWEST_PRECEDENCE - 10_000)
@Role(value = ROLE_INFRASTRUCTURE)
public class CacheEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final AtomicBoolean PROCESSED = new AtomicBoolean(false);

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {
    if (SpringUtil.isSpringCloudApplication(application)) {
      return;
    }
    if (PROCESSED.compareAndSet(false, true)) {
      Properties cacheConfig = EnvironmentUtil.getPropertiesByPrefix(environment,
          APPLICATION_CACHE_PREFIX);
      // 如果redisson非开启禁用RedissonAutoConfiguration
      if (BooleanUtil.isFalse(environment.getProperty(APPLICATION_CACHE_REDISSON_ENABLE, TRUE))) {
        EnvironmentUtil.addSpringExclude(environment, cacheConfig, RedissonAutoConfiguration.class);
      }

      // 替换前缀供spring使用
      Properties replaceConfig = EnvironmentUtil.replacePropertiesPrefix(cacheConfig,
          APPLICATION_CACHE_PREFIX, SPRING_REDIS);
      cacheConfig.putAll(replaceConfig);
      // 替换连接池配置默认使用lettuce
      String impl = environment.getProperty(APPLICATION_CACHE_PREFIX + "impl", "lettuce");
      Properties replacePoolConfig = EnvironmentUtil.replacePropertiesPrefix(cacheConfig,
          APPLICATION_CACHE_POOL_PREFIX, SPRING_REDIS + impl + ".pool");
      cacheConfig.putAll(replacePoolConfig);

      environment.getPropertySources()
          .addFirst(new PropertiesPropertySource(CACHE.getName(), cacheConfig));
      LoggerUtil.infoDeferred("cache config load success");
    }
  }

}
