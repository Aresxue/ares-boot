package cn.ares.boot.starter.cache.config;

import static cn.ares.boot.starter.cache.constant.CacheConstant.APPLICATION_CACHE_REDISSON_ENABLE;
import static cn.ares.boot.util.common.constant.StringConstant.TRUE;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Cluster;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Jedis;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.context.annotation.Bean;

/**
 * @author: Ares
 * @time: 2024-07-04 22:04:18
 * @description: 框架Redisson配置
 * @description: Framework Redisson configuration
 * @version: JDK 1.8
 */
@ConditionalOnProperty(name = APPLICATION_CACHE_REDISSON_ENABLE, havingValue = TRUE, matchIfMissing = true)
@ConditionalOnClass(RedissonAutoConfiguration.class)
@AutoConfigureBefore(RedissonAutoConfiguration.class)
public class BootRedissonConfiguration {

  private static final String REDIS_PROTOCOL_PREFIX = "redis://";
  private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

  @Autowired(required = false)
  private List<RedissonAutoConfigurationCustomizer> redissonAutoConfigurationCustomizers;

  @Autowired
  private RedisProperties redisProperties;

  @Value("${ares.application.cache.redisson.ping-connection-interval:30000}")
  private Integer pingConnectionInterval;

  /**
   * 数据读取模式（默认读从，可能会存在存完立马读不到的情况） Data read mode (default read from, there may be a situation where
   * you can't read it immediately after saving)
   */
  @Value("${ares.application.cache.redisson.read-mode:SLAVE}")
  private String readMode;

  @Bean(destroyMethod = "shutdown")
  @ConditionalOnMissingBean(RedissonClient.class)
  public RedissonClient redisson() throws IOException {
    Config config = new Config();

    Duration duration = redisProperties.getTimeout();
    int timeout = null == duration ? 10_000 : (int) duration.toMillis();
    String username = redisProperties.getUsername();
    String password = redisProperties.getPassword();

    int minIdle = 1;
    int maxActive = 8;
    Lettuce lettuce = redisProperties.getLettuce();
    Jedis jedis = redisProperties.getJedis();
    Pool pool = null;
    if (null != lettuce) {
      pool = lettuce.getPool();
    } else if (null != jedis) {
      pool = jedis.getPool();
    }
    if (null != pool) {
      minIdle = pool.getMinIdle();
      maxActive = pool.getMaxActive();
    }

    Sentinel sentinel = redisProperties.getSentinel();
    Cluster cluster = redisProperties.getCluster();
    if (null != sentinel) {
      config.useSentinelServers()
          .setMasterName(sentinel.getMaster())
          .addSentinelAddress(convert(sentinel.getNodes()))
          .setDatabase(redisProperties.getDatabase())
          .setConnectTimeout(timeout)
          .setUsername(username)
          .setPassword(password)
          .setMasterConnectionMinimumIdleSize(minIdle)
          .setMasterConnectionPoolSize(maxActive)
          .setPingConnectionInterval(pingConnectionInterval)
          .setReadMode(ReadMode.valueOf(readMode));
    } else if (null != cluster) {
      config.useClusterServers()
          .addNodeAddress(convert(cluster.getNodes()))
          .setConnectTimeout(timeout)
          .setUsername(username)
          .setPassword(password)
          .setMasterConnectionMinimumIdleSize(minIdle)
          .setMasterConnectionPoolSize(maxActive)
          .setPingConnectionInterval(pingConnectionInterval)
          .setReadMode(ReadMode.valueOf(readMode));
    } else {
      String prefix = REDIS_PROTOCOL_PREFIX;
      if (redisProperties.isSsl()) {
        prefix = REDISS_PROTOCOL_PREFIX;
      }
      config.useSingleServer()
          .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
          .setConnectTimeout(timeout)
          .setDatabase(redisProperties.getDatabase())
          .setUsername(username)
          .setPassword(redisProperties.getPassword())
          .setConnectionMinimumIdleSize(minIdle)
          .setConnectionPoolSize(maxActive)
          .setPingConnectionInterval(pingConnectionInterval);
    }
    if (redissonAutoConfigurationCustomizers != null) {
      for (RedissonAutoConfigurationCustomizer customizer : redissonAutoConfigurationCustomizers) {
        customizer.customize(config);
      }
    }
    return Redisson.create(config);
  }

  private String[] convert(List<String> nodesObject) {
    List<String> nodes = new ArrayList<>(nodesObject.size());
    for (String node : nodesObject) {
      if (!node.startsWith(REDIS_PROTOCOL_PREFIX) && !node.startsWith(REDISS_PROTOCOL_PREFIX)) {
        nodes.add(REDIS_PROTOCOL_PREFIX + node);
      } else {
        nodes.add(node);
      }
    }
    return nodes.toArray(new String[0]);
  }


}
