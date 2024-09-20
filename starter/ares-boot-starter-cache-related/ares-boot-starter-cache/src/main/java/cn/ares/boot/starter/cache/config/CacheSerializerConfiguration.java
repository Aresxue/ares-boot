package cn.ares.boot.starter.cache.config;

import static cn.ares.boot.starter.cache.constant.CacheConstant.APPLICATION_CACHE_SERIALIZER_ENABLE;
import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.util.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author: Ares
 * @time: 2021-03-26 13:13
 * @description: 缓存序列化配置类
 * @description: Cache serialization configuration class
 * @version: JDK 1.8
 */
@Configuration
@ConditionalOnProperty(name = APPLICATION_CACHE_SERIALIZER_ENABLE, havingValue = TRUE, matchIfMissing = true)
@AutoConfigureBefore(value = {RedisAutoConfiguration.class, RedissonAutoConfiguration.class})
@Role(value = ROLE_INFRASTRUCTURE)
public class CacheSerializerConfiguration {

  private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer;

  private final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

  @Value("${ares.application.cache.serializer.date.format:yyyy-MM-dd HH:mm:ss.SSS}")
  private String dateFormat;

  @Value("${ares.application.cache.serializer.local-date.format:yyyy-MM-dd}")
  private String localDateFormat;

  @Value("${ares.application.cache.serializer.local-time.format:HH:mm:ss.SSS}")
  private String localTimeFormat;

  @Value("${ares.application.cache.serializer.local-date-time.format:yyyy-MM-dd HH:mm:ss.SSS}")
  private String localDateTimeFormat;

  @PostConstruct
  public void init() {
    // 使用Jackson2JsonRedisSerialize替换默认序列化
    // Use Jackson2JsonRedisSerialize to replace the default serialization
    jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    ObjectMapper objectMapper = JsonUtil.getJsonMapper(false);
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    objectMapper.setTimeZone(TimeZone.getDefault());
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
    // Specify the type of serialized input. The class must be non-final. Final classes, such as String, Integer, etc., will throw an exception.
    objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

    JsonUtil.configTime(objectMapper, dateFormat, localDateFormat, localTimeFormat,
        localDateTimeFormat);
  }

  /**
   * @author: Ares
   * @description: redisTemplate默认序列化使用的jdk Serializable, 存储二进制字节码, 这里自定义json序列化类
   * @description: redisTemplate default serialization uses jdk Serializable, storing binary
   * bytecode, here customizes json serialization class
   * @time: 2021-03-26 13:41:00
   * @params: [redisConnectionFactory] redis连接工厂
   * @return: org.springframework.data.redis.core.RedisTemplate<String, V>
   */
  @Bean
  @ConditionalOnMissingBean(name = "aresRedisTemplate")
  public <V> RedisTemplate<String, V> aresRedisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);

    CompositeRedisSerializer compositeRedisSerializer = new CompositeRedisSerializer(
        jackson2JsonRedisSerializer, stringRedisSerializer);
    // 设置value的序列化规则和key的序列化规则
    // Set the serialization rules for value and key
    redisTemplate.setKeySerializer(stringRedisSerializer);
    redisTemplate.setValueSerializer(compositeRedisSerializer);
    redisTemplate.setHashKeySerializer(stringRedisSerializer);
    redisTemplate.setHashValueSerializer(compositeRedisSerializer);

    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }

  public static class CompositeRedisSerializer implements RedisSerializer<Object> {

    private final Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer;
    private final StringRedisSerializer stringRedisSerializer;

    public CompositeRedisSerializer(Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer,
        StringRedisSerializer stringRedisSerializer) {
      this.jackson2JsonRedisSerializer = jackson2JsonRedisSerializer;
      this.stringRedisSerializer = stringRedisSerializer;
    }

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
      if (obj instanceof String) {
        return stringRedisSerializer.serialize(obj.toString());
      }
      return jackson2JsonRedisSerializer.serialize(obj);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
      if (bytes == null || bytes.length == 0) {
        return null;
      }
      if (bytes[0] == '{' || bytes[0] == '[') {
        return jackson2JsonRedisSerializer.deserialize(bytes);
      }
      return stringRedisSerializer.deserialize(bytes);
    }
  }

}