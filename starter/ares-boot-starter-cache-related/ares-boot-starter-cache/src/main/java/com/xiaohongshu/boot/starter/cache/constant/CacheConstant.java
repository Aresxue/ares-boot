package com.xiaohongshu.boot.starter.cache.constant;

/**
 * @author: Ares
 * @time: 2024-07-04 20:01:47
 * @description: 缓存常量
 * @description: Cache constant
 * @version: JDK 1.8
 */
public interface CacheConstant {

  /**
   * 缓存配置前缀
   */
  String APPLICATION_CACHE_PREFIX = "ares.application.cache.";
  /**
   * 缓存模块总开关
   */
  String APPLICATION_CACHE_ENABLE = APPLICATION_CACHE_PREFIX + "enabled";
  /**
   * Redisson功能开关
   */
  String APPLICATION_CACHE_REDISSON_ENABLE = APPLICATION_CACHE_PREFIX + "redisson.enabled";
  /**
   * 缓存管理器功能开关
   */
  String APPLICATION_CACHE_MANAGER_ENABLE = APPLICATION_CACHE_PREFIX + "manager.enabled";
  /**
   * 缓存序列化功能开关
   */
  String APPLICATION_CACHE_SERIALIZER_ENABLE = APPLICATION_CACHE_PREFIX + "serializer.enabled";
  /**
   * 缓存连接池前缀
   */
  String APPLICATION_CACHE_POOL_PREFIX = APPLICATION_CACHE_PREFIX + "pool";

  String SPRING_REDIS = "spring.redis.";

}
