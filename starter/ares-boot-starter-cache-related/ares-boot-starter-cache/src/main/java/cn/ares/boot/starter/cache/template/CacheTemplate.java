package cn.ares.boot.starter.cache.template;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_SUPPORT;
import static org.springframework.data.redis.connection.RedisStringCommands.SetOption.UPSERT;

import cn.ares.boot.starter.cache.operation.CacheOperation;
import cn.ares.boot.starter.cache.util.CacheUtil;
import cn.ares.boot.util.common.ExceptionUtil;
import cn.ares.boot.util.compress.LosslessCompressUtil;
import cn.ares.boot.util.json.JsonUtil;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BulkMapper;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @author: Ares
 * @time: 2024-09-06 11:46:48
 * @description: 缓存模板
 * @description: Cache template
 * @version: JDK 1.8
 */
@Component
@Role(value = ROLE_SUPPORT)
@Import(CacheUtil.class)
public class CacheTemplate<V> implements CacheOperation<V> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheTemplate.class);

  @Resource
  private RedisTemplate<String, V> aresRedisTemplate;

  @Autowired(required = false)
  private RedissonClient redissonClient;

  @Override
  public Boolean setObject(String key, V value) {
    return setObject(key, value, false);
  }

  @Override
  public Boolean setObject(String key, V value, boolean isCompress) {
    return setObject(key, value, null, isCompress);
  }

  @Override
  public Boolean setObject(String key, V value, Duration duration) {
    return setObject(key, value, duration, false);
  }

  @Override
  public Boolean setObject(String key, V value, Duration duration, boolean isCompress) {
    return setObject(key, value, duration, null, null, isCompress);
  }

  @Override
  public Boolean setObject(String key, V value, long expirationTime, TimeUnit timeUnit,
      boolean isCompress) {
    return setObject(key, value, null, expirationTime, timeUnit, isCompress);
  }

  /**
   * @author: Ares
   * @description: 设置对象
   * @time: 2021-05-17 14:51:00
   * @params: [key, value, timeout, expirationTime, timeUnit, isCompress] key, value, 超时时间,
   * 超时时间和timeUnit搭配使用, 超时时间和timeUnit搭配使用, 超时时间单位，是否压缩
   * @return: boolean 响应参数
   */
  private Boolean setObject(String key, V value, Duration timeout, Long expirationTime,
      TimeUnit timeUnit, boolean isCompress) {
    try {
      if (isCompress) {
        byte[] keyBytes = key.getBytes(Charset.defaultCharset());
        byte[] valueBytes = compress(value);
        Boolean result = aresRedisTemplate.execute((RedisCallback<Boolean>) (connection) -> {
          if (null == timeout) {
            connection.set(keyBytes, valueBytes);
          } else if (null != expirationTime && null != timeUnit) {
            connection.set(keyBytes, valueBytes, Expiration.from(expirationTime, timeUnit), UPSERT);
          } else {
            connection.set(keyBytes, valueBytes, Expiration.from(timeout), UPSERT);
          }
          return true;
        });
        return null != result && result;
      } else {
        if (null != timeout) {
          aresRedisTemplate.opsForValue().set(key, value, timeout);
        } else if (null != expirationTime && null != timeUnit) {
          aresRedisTemplate.opsForValue().set(key, value, expirationTime, timeUnit);
        } else {
          aresRedisTemplate.opsForValue().set(key, value);
        }
        return true;
      }
    } catch (Exception exception) {
      LOGGER.error("set object to cache exception: ", exception);
      return false;
    }
  }

  private byte[] compress(V value) {
    return LosslessCompressUtil.compress(JsonUtil.toBytes(value));
  }

  private <T> T decompress(byte[] bytes, Class<T> clazz) {
    return JsonUtil.toJavaObject(LosslessCompressUtil.decompress(bytes), clazz);
  }

  @Override
  public <T> T getObject(String key, Class<T> clazz) {
    return aresRedisTemplate.execute((RedisCallback<T>) (connection) -> {
      byte[] value = connection.get(key.getBytes());
      return null == value ? null : decompress(value, clazz);
    });
  }

  @Override
  public Boolean delete(String key) {
    return aresRedisTemplate.delete(key);
  }

  @Override
  public Long delete(Collection<String> keyColl) {
    return aresRedisTemplate.delete(keyColl);
  }

  @Override
  public byte[] dump(String key) {
    return aresRedisTemplate.dump(key);
  }

  @Override
  public Boolean hasKey(String key) {
    return aresRedisTemplate.hasKey(key);
  }

  @Override
  public Boolean expire(String key, long timeout, TimeUnit unit) {
    return aresRedisTemplate.expire(key, timeout, unit);
  }

  @Override
  public Boolean expireAt(String key, Date date) {
    return aresRedisTemplate.expireAt(key, date);
  }

  @Override
  public Set<String> keys(String pattern) {
    return aresRedisTemplate.keys(pattern);
  }

  @Override
  public Boolean move(String key, int dbIndex) {
    return aresRedisTemplate.move(key, dbIndex);
  }

  @Override
  public Boolean persist(String key) {
    return aresRedisTemplate.persist(key);
  }

  @Override
  public Long getExpire(String key, TimeUnit unit) {
    return aresRedisTemplate.getExpire(key, unit);
  }

  @Override
  public Long getExpire(String key) {
    return aresRedisTemplate.getExpire(key);
  }

  @Override
  public String randomKey() {
    return aresRedisTemplate.randomKey();
  }

  @Override
  public void rename(String oldKey, String newKey) {
    aresRedisTemplate.rename(oldKey, newKey);
  }

  @Override
  public Boolean renameIfAbsent(String oldKey, String newKey) {
    return aresRedisTemplate.renameIfAbsent(oldKey, newKey);
  }

  @Override
  public DataType type(String key) {
    return aresRedisTemplate.type(key);
  }

  @Override
  public void set(String key, V value) {
    aresRedisTemplate.opsForValue().set(key, value);
  }

  @Override
  public V get(String key) {
    return aresRedisTemplate.opsForValue().get(key);
  }

  @Override
  public void setRange(String key, V value, long offset) {
    aresRedisTemplate.opsForValue().set(key, value, offset);
  }

  @Override
  public String getRange(String key, long start, long end) {
    return aresRedisTemplate.opsForValue().get(key, start, end);
  }

  @Override
  public V getAndSet(String key, V value) {
    return aresRedisTemplate.opsForValue().getAndSet(key, value);
  }

  @Override
  public Boolean getBit(String key, long offset) {
    return aresRedisTemplate.opsForValue().getBit(key, offset);
  }

  @Override
  public List<V> multiGet(Collection<String> keyColl) {
    return aresRedisTemplate.opsForValue().multiGet(keyColl);
  }

  @Override
  public Boolean setBit(String key, long offset, boolean value) {
    return aresRedisTemplate.opsForValue().setBit(key, offset, value);
  }

  @Override
  public void setEx(String key, V value, long timeout, TimeUnit unit) {
    aresRedisTemplate.opsForValue().set(key, value, timeout, unit);
  }

  @Override
  public void setEx(String key, V value, Duration duration) {
    aresRedisTemplate.opsForValue().set(key, value, duration);
  }

  @Override
  public Boolean setIfAbsent(String key, V value) {
    return aresRedisTemplate.opsForValue().setIfAbsent(key, value);
  }

  @Override
  public Long size(String key) {
    return aresRedisTemplate.opsForValue().size(key);
  }

  @Override
  public void multiSet(Map<String, ? extends V> map) {
    aresRedisTemplate.opsForValue().multiSet(map);
  }

  @Override
  public Boolean multiSetIfAbsent(Map<String, ? extends V> map) {
    return aresRedisTemplate.opsForValue().multiSetIfAbsent(map);
  }

  @Override
  public Long incrBy(String key, long increment) {
    return aresRedisTemplate.opsForValue().increment(key, increment);
  }

  @Override
  public Double incrByFloat(String key, double increment) {
    return aresRedisTemplate.opsForValue().increment(key, increment);
  }

  @Override
  public Long incr(String key) {
    return aresRedisTemplate.opsForValue().increment(key);
  }

  @Override
  public Long decr(String key) {
    return aresRedisTemplate.opsForValue().decrement(key);
  }

  @Override
  public Long decr(String key, long decrement) {
    return aresRedisTemplate.opsForValue().decrement(key, decrement);
  }

  @Override
  public Integer append(String key, String value) {
    return aresRedisTemplate.opsForValue().append(key, value);
  }

  @Override
  public <HV> HV hGet(String key, Object hashKey) {
    HashOperations<String, Object, HV> hashOperations = aresRedisTemplate.opsForHash();
    return hashOperations.get(key, hashKey);
  }

  @Override
  public <HV> Map<String, HV> hGetAll(String key) {
    HashOperations<String, String, HV> hashOperations = aresRedisTemplate.opsForHash();
    return hashOperations.entries(key);
  }

  @Override
  public <HV> List<HV> hMultiGet(String key, Collection<String> hashKeyColl) {
    HashOperations<String, String, HV> hashOperations = aresRedisTemplate.opsForHash();
    return hashOperations.multiGet(key, hashKeyColl);
  }

  @Override
  public <HV> void hPut(String key, String hashKey, HV value) {
    aresRedisTemplate.opsForHash().put(key, hashKey, value);
  }

  @Override
  public <HV> void hPutAll(String key, Map<String, ? extends HV> map) {
    aresRedisTemplate.opsForHash().putAll(key, map);
  }

  @Override
  public <HV> Boolean hPutIfAbsent(String key, String hashKey, HV value) {
    return aresRedisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
  }

  @Override
  public Long hDelete(String key, Object... hashKeys) {
    return aresRedisTemplate.opsForHash().delete(key, hashKeys);
  }

  @Override
  public Boolean hExists(String key, Object hashKey) {
    return aresRedisTemplate.opsForHash().hasKey(key, hashKey);
  }


  @Override
  public Long hIncrBy(String key, String hashKey, long delta) {
    return aresRedisTemplate.opsForHash().increment(key, hashKey, delta);
  }

  @Override
  public Double hIncrByFloat(String key, String hashKey, double delta) {
    return aresRedisTemplate.opsForHash().increment(key, hashKey, delta);
  }

  @Override
  public Set<String> hKeys(String key) {
    HashOperations<String, String, Object> hashOperations = aresRedisTemplate.opsForHash();
    return hashOperations.keys(key);
  }

  @Override
  public Long hSize(String key) {
    HashOperations<String, Object, Object> hashOperations = aresRedisTemplate.opsForHash();
    return hashOperations.size(key);
  }

  @Override
  public <HV> List<HV> hValues(String key) {
    HashOperations<String, String, HV> hashOperations = aresRedisTemplate.opsForHash();
    return hashOperations.values(key);
  }

  @Override
  public <HV> Cursor<Entry<String, HV>> hScan(String key, ScanOptions options) {
    HashOperations<String, String, HV> hashOperations = aresRedisTemplate.opsForHash();
    return hashOperations.scan(key, options);
  }

  @Override
  public V lIndex(String key, long index) {
    return aresRedisTemplate.opsForList().index(key, index);
  }

  @Override
  public List<V> lRange(String key, long start, long end) {
    return aresRedisTemplate.opsForList().range(key, start, end);
  }

  @Override
  public Long lLeftPush(String key, V value) {
    return aresRedisTemplate.opsForList().leftPush(key, value);
  }

  @SafeVarargs
  @Override
  public final Long lLeftPushAll(String key, V... values) {
    return aresRedisTemplate.opsForList().leftPushAll(key, values);
  }

  @Override
  public Long lLeftPushAll(String key, Collection<V> collection) {
    return aresRedisTemplate.opsForList().leftPushAll(key, collection);
  }


  @Override
  public Long lLeftPushIfPresent(String key, V value) {
    return aresRedisTemplate.opsForList().leftPushIfPresent(key, value);
  }

  @Override
  public Long lLeftPush(String key, V pivot, V value) {
    return aresRedisTemplate.opsForList().leftPush(key, pivot, value);
  }

  @Override
  public Long lRightPush(String key, V value) {
    return aresRedisTemplate.opsForList().rightPush(key, value);
  }

  @SafeVarargs
  @Override
  public final Long lRightPushAll(String key, V... values) {
    return aresRedisTemplate.opsForList().rightPushAll(key, values);
  }

  @Override
  public Long lRightPushAll(String key, Collection<V> collection) {
    return aresRedisTemplate.opsForList().rightPushAll(key, collection);
  }

  @Override
  public Long lRightPushIfPresent(String key, V value) {
    return aresRedisTemplate.opsForList().rightPushIfPresent(key, value);
  }

  @Override
  public Long lRightPush(String key, V pivot, V value) {
    return aresRedisTemplate.opsForList().rightPush(key, pivot, value);
  }

  @Override
  public void lSet(String key, long index, V value) {
    aresRedisTemplate.opsForList().set(key, index, value);
  }

  @Override
  public V lLeftPop(String key) {
    return aresRedisTemplate.opsForList().leftPop(key);
  }

  @Override
  public V lBLeftPop(String key, long timeout, TimeUnit unit) {
    return aresRedisTemplate.opsForList().leftPop(key, timeout, unit);
  }

  @Override
  public V lRightPop(String key) {
    return aresRedisTemplate.opsForList().rightPop(key);
  }

  @Override
  public V lBRightPop(String key, long timeout, TimeUnit unit) {
    return aresRedisTemplate.opsForList().rightPop(key, timeout, unit);
  }

  @Override
  public V lRightPopAndLeftPush(String sourceKey, String destinationKey) {
    return aresRedisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
  }

  @Override
  public V lBRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout,
      TimeUnit unit) {
    return aresRedisTemplate.opsForList()
        .rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
  }

  @Override
  public Long lRemove(String key, long count, Object value) {
    return aresRedisTemplate.opsForList().remove(key, count, value);
  }

  @Override
  public void lTrim(String key, long start, long end) {
    aresRedisTemplate.opsForList().trim(key, start, end);
  }

  @Override
  public Long lLen(String key) {
    return aresRedisTemplate.opsForList().size(key);
  }

  @SafeVarargs
  @Override
  public final Long sAdd(String key, V... values) {
    return aresRedisTemplate.opsForSet().add(key, values);
  }

  @Override
  public Long sRemove(String key, Object... values) {
    return aresRedisTemplate.opsForSet().remove(key, values);
  }

  @Override
  public V sPop(String key) {
    return aresRedisTemplate.opsForSet().pop(key);
  }

  @Override
  public Boolean sMove(String key, V value, String destKey) {
    return aresRedisTemplate.opsForSet().move(key, value, destKey);
  }

  @Override
  public Long sSize(String key) {
    return aresRedisTemplate.opsForSet().size(key);
  }

  @Override
  public Boolean sIsMember(String key, Object value) {
    return aresRedisTemplate.opsForSet().isMember(key, value);
  }

  @Override
  public Set<V> sIntersect(String key, String otherKey) {
    return aresRedisTemplate.opsForSet().intersect(key, otherKey);
  }

  @Override
  public Set<V> sIntersect(String key, Collection<String> otherKeyColl) {
    return aresRedisTemplate.opsForSet().intersect(key, otherKeyColl);
  }

  @Override
  public Long sIntersectAndStore(String key, String otherKey, String destKey) {
    return aresRedisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
  }

  @Override
  public Long sIntersectAndStore(String key, Collection<String> otherKeyColl, String destKey) {
    return aresRedisTemplate.opsForSet().intersectAndStore(key, otherKeyColl, destKey);
  }

  @Override
  public Set<V> sUnion(String key, String otherKey) {
    return aresRedisTemplate.opsForSet().union(key, otherKey);
  }

  @Override
  public Set<V> sUnion(String key, Collection<String> otherKeyColl) {
    return aresRedisTemplate.opsForSet().union(key, otherKeyColl);
  }

  @Override
  public Long sUnionAndStore(String key, String otherKey, String destKey) {
    return aresRedisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
  }

  @Override
  public Long sUnionAndStore(String key, Collection<String> otherKeyColl, String destKey) {
    return aresRedisTemplate.opsForSet().unionAndStore(key, otherKeyColl, destKey);
  }

  @Override
  public Set<V> sDifference(String key, String otherKey) {
    return aresRedisTemplate.opsForSet().difference(key, otherKey);
  }

  @Override
  public Set<V> sDifference(String key, Collection<String> otherKeyColl) {
    return aresRedisTemplate.opsForSet().difference(key, otherKeyColl);
  }

  @Override
  public Long sDifference(String key, String otherKey, String destKey) {
    return aresRedisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
  }

  @Override
  public Long sDifference(String key, Collection<String> otherKeyColl, String destKey) {
    return aresRedisTemplate.opsForSet().differenceAndStore(key, otherKeyColl, destKey);
  }

  @Override
  public Set<V> setMembers(String key) {
    return aresRedisTemplate.opsForSet().members(key);
  }

  @Override
  public V sRandomMember(String key) {
    return aresRedisTemplate.opsForSet().randomMember(key);
  }

  @Override
  public List<V> sRandomMembers(String key, long count) {
    return aresRedisTemplate.opsForSet().randomMembers(key, count);
  }

  @Override
  public Set<V> sDistinctRandomMembers(String key, long count) {
    return aresRedisTemplate.opsForSet().distinctRandomMembers(key, count);
  }

  @Override
  public Cursor<V> sScan(String key, ScanOptions options) {
    return aresRedisTemplate.opsForSet().scan(key, options);
  }

  @Override
  public Boolean zAdd(String key, V value, double score) {
    return aresRedisTemplate.opsForZSet().add(key, value, score);
  }

  @Override
  public Long zAdd(String key, Set<TypedTuple<V>> valueTupleSet) {
    return aresRedisTemplate.opsForZSet().add(key, valueTupleSet);
  }

  @Override
  public Long zRemove(String key, Object... values) {
    return aresRedisTemplate.opsForZSet().remove(key, values);
  }

  @Override
  public Double zIncrementScore(String key, V value, double delta) {
    return aresRedisTemplate.opsForZSet().incrementScore(key, value, delta);
  }

  @Override
  public Long zRank(String key, Object value) {
    return aresRedisTemplate.opsForZSet().rank(key, value);
  }

  @Override
  public Long zReverseRank(String key, Object value) {
    return aresRedisTemplate.opsForZSet().reverseRank(key, value);
  }

  @Override
  public Set<V> zRange(String key, long start, long end) {
    return aresRedisTemplate.opsForZSet().range(key, start, end);
  }

  @Override
  public Set<TypedTuple<V>> zRangeWithScores(String key, long start, long end) {
    return aresRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
  }

  @Override
  public Set<V> zRangeByScore(String key, double min, double max) {
    return aresRedisTemplate.opsForZSet().rangeByScore(key, min, max);
  }

  @Override
  public Set<TypedTuple<V>> zRangeByScoreWithScores(String key, double min, double max) {
    return aresRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
  }

  @Override
  public Set<TypedTuple<V>> zRangeByScoreWithScores(String key, double min, double max, long start,
      long end) {
    return aresRedisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, start, end);
  }

  @Override
  public Set<V> zReverseRange(String key, long start, long end) {
    return aresRedisTemplate.opsForZSet().reverseRange(key, start, end);
  }

  @Override
  public Set<TypedTuple<V>> zReverseRangeWithScores(String key, long start, long end) {
    return aresRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
  }

  @Override
  public Set<V> zReverseRangeByScore(String key, double min, double max) {
    return aresRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
  }

  @Override
  public Set<TypedTuple<V>> zReverseRangeByScoreWithScores(String key, double min, double max) {
    return aresRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
  }

  @Override
  public Set<V> zReverseRangeByScore(String key, double min, double max, long start, long end) {
    return aresRedisTemplate.opsForZSet().reverseRangeByScore(key, min, max, start, end);
  }

  @Override
  public Long zCount(String key, double min, double max) {
    return aresRedisTemplate.opsForZSet().count(key, min, max);
  }

  @Override
  public Long zSize(String key) {
    return aresRedisTemplate.opsForZSet().size(key);
  }

  @Override
  public Long zZCard(String key) {
    return aresRedisTemplate.opsForZSet().zCard(key);
  }

  @Override
  public Double zScore(String key, Object value) {
    return aresRedisTemplate.opsForZSet().score(key, value);
  }

  @Override
  public Long zRemoveRange(String key, long start, long end) {
    return aresRedisTemplate.opsForZSet().removeRange(key, start, end);
  }

  @Override
  public Long zRemoveRangeByScore(String key, double min, double max) {
    return aresRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
  }

  @Override
  public Long zIntersectAndStore(String key, String otherKey, String destKey) {
    return aresRedisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
  }

  @Override
  public Long zIntersectAndStore(String key, Collection<String> otherKeyColl, String destKey) {
    return aresRedisTemplate.opsForZSet().intersectAndStore(key, otherKeyColl, destKey);
  }

  @Override
  public Long zUnionAndStore(String key, String otherKey, String destKey) {
    return aresRedisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
  }

  @Override
  public Long zUnionAndStore(String key, Collection<String> otherKeyColl, String destKey) {
    return aresRedisTemplate.opsForZSet().unionAndStore(key, otherKeyColl, destKey);
  }

  @Override
  public Cursor<TypedTuple<V>> zScan(String key, ScanOptions options) {
    return aresRedisTemplate.opsForZSet().scan(key, options);
  }

  @Override
  public void setEnableTransactionSupport(boolean enableTransactionSupport) {
    aresRedisTemplate.setEnableTransactionSupport(enableTransactionSupport);

  }

  @Override
  public void multi() {
    aresRedisTemplate.multi();
  }

  @Override
  public void discard() {
    aresRedisTemplate.discard();
  }

  @Override
  public List<Object> exec() {
    return aresRedisTemplate.exec();
  }

  @Override
  public void watch(String key) {
    aresRedisTemplate.watch(key);
  }

  @Override
  public void watch(Collection<String> keyColl) {
    aresRedisTemplate.watch(keyColl);
  }

  @Override
  public void unwatch() {
    aresRedisTemplate.unwatch();
  }

  @Override
  public Long countExistingKeys(Collection<String> keyColl) {
    return aresRedisTemplate.countExistingKeys(keyColl);
  }

  @Override
  public Boolean unlink(String key) {
    return aresRedisTemplate.unlink(key);
  }

  @Override
  public Long unlink(Collection<String> keyColl) {
    return aresRedisTemplate.unlink(keyColl);
  }

  @Override
  public List<Object> exec(RedisSerializer<?> valueSerializer) {
    return aresRedisTemplate.exec(valueSerializer);
  }

  @Override
  public <T> T execute(RedisCallback<T> action) {
    return aresRedisTemplate.execute(action);
  }

  @Override
  public <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
    return aresRedisTemplate.execute(action, exposeConnection);
  }

  @Override
  public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
    return aresRedisTemplate.execute(action, exposeConnection, pipeline);
  }

  @Override
  public <T> T execute(SessionCallback<T> session) {
    return aresRedisTemplate.execute(session);
  }

  @Override
  public <T> T execute(RedisScript<T> script, List<String> keys, Object... args) {
    return aresRedisTemplate.execute(script, keys, args);
  }

  @Override
  public <T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer,
      RedisSerializer<T> resultSerializer, List<String> keyList, Object... args) {
    return aresRedisTemplate.execute(script, argsSerializer, resultSerializer, keyList, args);
  }

  @Override
  public List<Object> executePipelined(RedisCallback<?> action) {
    return aresRedisTemplate.executePipelined(action);
  }

  @Override
  public List<Object> executePipelined(RedisCallback<?> action,
      RedisSerializer<?> resultSerializer) {
    return aresRedisTemplate.executePipelined(action, resultSerializer);
  }

  @Override
  public List<Object> executePipelined(SessionCallback<?> session) {
    return aresRedisTemplate.executePipelined(session);
  }

  @Override
  public List<Object> executePipelined(SessionCallback<?> session,
      RedisSerializer<?> resultSerializer) {
    return aresRedisTemplate.executePipelined(session, resultSerializer);
  }

  @Override
  public <T> List<T> sort(SortQuery<String> query, RedisSerializer<T> resultSerializer) {
    return aresRedisTemplate.sort(query, resultSerializer);
  }

  @Override
  public List<V> sort(SortQuery<String> query) {
    return aresRedisTemplate.sort(query);
  }

  @Override
  public <T, S> List<T> sort(SortQuery<String> query, BulkMapper<T, S> bulkMapper,
      RedisSerializer<S> resultSerializer) {
    return aresRedisTemplate.sort(query, bulkMapper, resultSerializer);
  }

  @Override
  public void convertAndSend(String channel, Object message) {
    aresRedisTemplate.convertAndSend(channel, message);
  }

  @Override
  public void runWithLock(String key, Runnable runnable) {
    runWithLock(key, null, runnable);
  }

  @Override
  public void runWithLock(String key, Duration leaseTime, Runnable runnable) {
    if (null == redissonClient) {
      throw new RuntimeException("Redisson not load");
    }
    RLock lock = redissonClient.getLock(key);
    if (null == leaseTime) {
      lock.lock();
    } else {
      lock.lock(leaseTime.toMillis(), TimeUnit.MILLISECONDS);
    }
    try {
      runnable.run();
    } finally {
      if (lock.isLocked() && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  @Override
  public <T> T getWithLock(String key, Supplier<T> supplier) {
    return getWithLock(key, null, supplier);
  }

  @Override
  public <T> T getWithLock(String key, Duration leaseTime, Supplier<T> supplier) {
    if (null == redissonClient) {
      throw new RuntimeException("Redisson not load");
    }
    RLock lock = redissonClient.getLock(key);
    if (null == leaseTime) {
      lock.lock();
    } else {
      lock.lock(leaseTime.toMillis(), TimeUnit.MILLISECONDS);
    }
    try {
      return supplier.get();
    } finally {
      if (lock.isLocked() && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }


  @Override
  public void runWithTryLock(String key, Runnable runnable) {
    runWithTryLock(key, Duration.ofMillis(0), runnable);
  }

  @Override
  public void runWithTryLock(String key, Duration waitTime, Runnable runnable) {
    runWithTryLock(key, waitTime, Duration.ofMillis(-1), runnable);
  }

  @Override
  public void runWithTryLock(String key, Duration waitTime, Duration leaseTime, Runnable runnable) {
    if (null == redissonClient) {
      throw new RuntimeException("Redisson not load");
    }
    RLock lock = redissonClient.getLock(key);
    boolean lockResult = ExceptionUtil.get(
        () -> lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS));
    try {
      if (lockResult) {
        runnable.run();
      }
    } finally {
      if (lockResult && lock.isLocked() && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  @Override
  public <T> T getWithTryLock(String key, Supplier<T> supplier) {
    return getWithTryLock(key, Duration.ofMillis(0), supplier);
  }

  @Override
  public <T> T getWithTryLock(String key, Duration waitTime, Supplier<T> supplier) {
    return getWithTryLock(key, waitTime, Duration.ofMillis(-1), supplier);
  }

  @Override
  public <T> T getWithTryLock(String key, Duration waitTime, Duration leaseTime,
      Supplier<T> supplier) {
    if (null == redissonClient) {
      throw new RuntimeException("Redisson not load");
    }
    RLock lock = redissonClient.getLock(key);
    boolean lockResult = ExceptionUtil.get(
        () -> lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS));
    try {
      if (lockResult) {
        return supplier.get();
      }
      return null;
    } finally {
      if (lockResult && lock.isLocked() && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }


}
