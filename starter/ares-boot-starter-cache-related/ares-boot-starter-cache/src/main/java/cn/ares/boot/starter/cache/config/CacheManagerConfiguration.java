package cn.ares.boot.starter.cache.config;

import static cn.ares.boot.starter.cache.constant.CacheConstant.APPLICATION_CACHE_MANAGER_ENABLE;
import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.base.config.BootEnvironment;
import cn.ares.boot.util.common.MapUtil;
import java.util.Map;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;


/**
 * @author: Ares
 * @time: 2021-12-12 14:30:00
 * @description: 缓存全局管理配置
 * @description: Cache global management configuration
 * @version: JDK 1.8
 */
@ConditionalOnProperty(name = APPLICATION_CACHE_MANAGER_ENABLE, havingValue = TRUE, matchIfMissing = true)
@ConditionalOnClass(RedissonAutoConfiguration.class)
@AutoConfigureAfter(RedissonAutoConfiguration.class)
@Role(value = ROLE_INFRASTRUCTURE)
public class CacheManagerConfiguration {

  /**
   * ttl默认设为3天
   * ttl is set to 3 days by default
   */
  @Value("${ares.application.cache.manager.ttl:259200000}")
  private Long ttl;

  /**
   * 最大空闲时间，默认为30分钟
   * Maximum idle time, default is 30 minutes
   */
  @Value("${ares.application.cache.manager.max-idle-time:1800000}")
  private Long maxIdleTime;
  /**
   * 设置map的最大大小，超出的元素使用LRU算法进行剔除
   * Set max size of map. Superfluous elements are evicted using LRU algorithm.
   */
  @Value("${ares.application.cache.manager.max-size:}")
  private Integer maxSize;

  @Bean
  @ConditionalOnBean(RedissonClient.class)
  public CacheManager cacheManager(RedissonClient redissonClient) {
    Map<String, CacheConfig> config = MapUtil.newHashMap(4);
    CacheConfig cacheConfig = new CacheConfig(ttl, maxIdleTime);
    if (null != maxSize) {
      cacheConfig.setMaxSize(maxSize);
    }
    config.put("cache-manager:" + BootEnvironment.getAppName(), cacheConfig);
    return new RedissonSpringCacheManager(redissonClient, config);
  }

}
