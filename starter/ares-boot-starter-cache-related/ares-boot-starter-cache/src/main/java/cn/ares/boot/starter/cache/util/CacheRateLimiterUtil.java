package cn.ares.boot.starter.cache.util;

import static org.redisson.api.RateIntervalUnit.MILLISECONDS;
import static org.redisson.api.RateType.OVERALL;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_SUPPORT;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateLimiterConfig;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;

/**
 * @author: Ares
 * @time: 2022-02-18 11:31:28
 * @description: 基于缓存的限流工具类
 * @description: Rate limiter util by cahe
 * @version: JDK 1.8
 */
@Component
@Role(value = ROLE_SUPPORT)
public class CacheRateLimiterUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheRateLimiterUtil.class);

  private static final String RATE_LIMITER_PREFIX = "rl:";

  private static RedissonClient redissonClient;

  /**
   * @author: Ares
   * @description: 是否禁用Redisson
   * @time: 2024-09-06 17:06:24
   * @return: boolean 是否禁用
   */
  public static boolean disableRedisson() {
    return null == redissonClient;
  }

  /**
   * @author: Ares
   * @description: 尝试获取指定周期内的许可
   * @description: try to acquire permits in specified period
   * @time: 2024-09-06 17:06:40
   * @params: [key, permit, period] 键，许可数，许可周期
   * @return: boolean 是否许可
   */
  public static boolean tryAcquire(String key, int permit, Duration period) {
    return tryAcquire(key, permit, period, null);
  }

  /**
   * @author: Ares
   * @description: 尝试获取指定周期内的许可
   * @description: try to acquire permits in specified period
   * @time: 2024-09-06 17:06:40
   * @params: [key, permit, period, timeout] 键，许可数，许可周期，超时时间
   * @return: boolean 是否许可
   */
  public static boolean tryAcquire(String key, long permit, Duration period, Duration timeout) {
    RRateLimiter rateLimiter = redissonClient.getRateLimiter(RATE_LIMITER_PREFIX + key);
    boolean isExists = rateLimiter.isExists();
    long periodMillis = period.toMillis();
    if (!isExists) {
      rateLimiter.setRate(OVERALL, permit, periodMillis, MILLISECONDS);
    } else {
      RateLimiterConfig rateLimiterConfig = rateLimiter.getConfig();
      // 判断配置是否更新，如果更新则重新加载限流器配置
      // judge config is update, if update reload limiter config
      boolean permitChange = permit != rateLimiterConfig.getRate();
      Long rateInterval = rateLimiterConfig.getRateInterval();
      if (permitChange || null == rateInterval || !rateInterval.equals(periodMillis)) {
        rateLimiter.setRate(OVERALL, permit, periodMillis, MILLISECONDS);
        if (permitChange) {
          expire(period, rateLimiter);
        }
      }
    }

    boolean acquire;
    try {
      if (null == timeout) {
        acquire = rateLimiter.tryAcquire();
      } else {
        acquire = rateLimiter.tryAcquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
      }
    } finally {
      // 在尝试获取许可之后过期，此时许可和值已生成
      // expire after tryAcquire, permits and value has generated at this moment
      if (!isExists) {
        expire(period, rateLimiter);
      }
    }

    return acquire;
  }

  /**
   * @author: Ares
   * @description: 取消限流
   * @description: cancel rate limiter
   * @time: 2024-09-06 17:16:43
   * @params: [key] 键
   * @return: boolean 取消结果
   */
  public static boolean cancel(String key) {
    try {
      boolean cancelResult = redissonClient.getRateLimiter(RATE_LIMITER_PREFIX + key).delete();
      if (!cancelResult) {
        LOGGER.warn("cancel limiter fail for key: {}", key);
      }
      return cancelResult;
    } catch (Exception exception) {
      LOGGER.warn("cancel limiter fail for key: {}, exception: ", key, exception);
      return false;
    }
  }

  private static void expire(Duration period, RRateLimiter rateLimiter) {
    // 设置键的过期时间为周期的两倍
    // set key expire time is twice period
    Duration keyExpire = period.multipliedBy(2);
    rateLimiter.expireAsync(keyExpire);
  }

}
