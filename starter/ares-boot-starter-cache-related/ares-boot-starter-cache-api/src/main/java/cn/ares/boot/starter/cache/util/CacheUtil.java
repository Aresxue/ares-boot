package cn.ares.boot.starter.cache.util;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_SUPPORT;

import cn.ares.boot.starter.cache.operation.CacheOperation;
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
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BulkMapper;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author: Ares
 * @time: 2024-09-19 20:47:31
 * @description: 缓存工具类
 * @description: Cache util
 * @version: JDK 1.8
 */
@Component
@Role(value = ROLE_SUPPORT)
public class CacheUtil {

  private static CacheOperation<Object> cacheOperation;

  /**
   * @author: Ares
   * @description: 设置键值对
   * @description: Set key-value pair
   * @time: 2024-09-06 11:45:05
   * @params: [key, value, duration] 键，值
   * @return: Boolean 设置结果
   */
  public static <V> Boolean setObject(String key, V value) {
    return determineCache().setObject(key, value);
  }

  /**
   * @author: Ares
   * @description: 设置键值对（可指定是否压缩）
   * @description: Set key-value pair(can specify whether to compress)
   * @time: 2024-09-06 11:45:05
   * @params: [key, value, isCompress] 键，值，是否压缩
   * @return: Boolean 设置结果
   */
  public static <V> Boolean setObject(String key, V value, boolean isCompress) {
    return determineCache().setObject(key, value, isCompress);
  }

  /**
   * @author: Ares
   * @description: 指定超时时间设置键值对
   * @description: Set key-value pair(can specify whether to compress)
   * @time: 2024-09-06 11:45:05
   * @params: [key, value, duration] 键，值，超时时间
   * @return: Boolean 设置结果
   */
  public static <V> Boolean setObject(String key, V value, Duration duration) {
    return determineCache().setObject(key, value, duration);
  }

  /**
   * @author: Ares
   * @description: 指定超时时间设置键值对（可指定是否压缩）
   * @description: Set key-value pair with specified timeout (can specify whether to compress)
   * @time: 2024-09-06 11:45:05
   * @params: [key, value, duration, isCompress] 键，值，超时时间，是否压缩
   * @return: Boolean 设置结果
   */
  public static <V> Boolean setObject(String key, V value, Duration duration, boolean isCompress) {
    return determineCache().setObject(key, value, duration, isCompress);
  }

  /**
   * @author: Ares
   * @description: 指定超时时间设置键值对（可指定是否压缩）
   * @description: Set key-value pair with specified timeout (can specify whether to compress)
   * @time: 2024-09-06 11:48:45
   * @params: [key, value, expirationTime, timeUnit, isCompress] 键，值，超时时间，超时时间单位，是否压缩
   * @return: Boolean 设置结果
   */
  public static <V> Boolean setObject(String key, V value, long expirationTime, TimeUnit timeUnit,
      boolean isCompress) {
    return determineCache().setObject(key, value, expirationTime, timeUnit, isCompress);
  }

  /**
   * @author: Ares
   * @description: 获取键对应的对象
   * @description: Get the object corresponding to the key
   * @time: 2021-05-17 15:14:00
   * @params: [key, clazz] 键, 对象类型
   * @return: V 对象
   */
  public static <T> T getObject(String key, Class<T> clazz) {
    return determineCache().getObject(key, clazz);
  }

  /**
   * @author: Ares
   * @description: 删除指定键
   * @description: Delete the specified key
   * @time: 2022-02-17 15:31:53
   * @params: [key] 键
   * @return: java.lang.Boolean 删除结果
   */
  public static Boolean delete(String key) {
    return determineCache().delete(key);
  }

  /**
   * @author: Ares
   * @description: 批量删除键
   * @description: Batch delete keys
   * @time: 2022-02-17 15:32:30
   * @params: [keys] 键集合
   * @return: java.lang.Long 删除结果
   */
  public static Long delete(Collection<String> keyColl) {
    return determineCache().delete(keyColl);
  }

  /**
   * @author: Ares
   * @description: 导出键对应的值
   * @description: Export the value corresponding to the key
   * @time: 2021-05-17 11:24:00
   * @params: [key] 键
   */
  public static byte[] dump(String key) {
    return determineCache().dump(key);
  }

  /**
   * @author: Ares
   * @description: 是否存在键
   * @description: Whether the key exists
   * @time: 2021-05-17 11:24:00
   * @params: [key] 键
   * @return: java.lang.Boolean 是否存在
   */
  public static Boolean hasKey(String key) {
    return determineCache().hasKey(key);
  }

  /**
   * @author: Ares
   * @description: 设置过期时间
   * @description: Set expiration time
   * @time: 2021-05-17 11:25:00
   * @params: [key, timeout, unit] 键, 超时时间, 时间单位
   * @return: java.lang.Boolean 设置结果
   */
  public static Boolean expire(String key, long timeout, TimeUnit unit) {
    return determineCache().expire(key, timeout, unit);
  }

  /**
   * @author: Ares
   * @description: 设置过期时间到指定时间
   * @description: Set the expiration time to the specified time
   * @time: 2021-05-17 11:25:00
   * @params: [key, date] 键, 时间
   * @return: java.lang.Boolean 设置结果
   */
  public static Boolean expireAt(String key, Date date) {
    return determineCache().expireAt(key, date);
  }

  /**
   * @author: Ares
   * @description: 查找匹配的键的集合
   * @description: Find a collection of matching keys
   * @time: 2021-05-17 11:25:00
   * @params: [pattern] 匹配规则
   * @return: java.util.Set<java.lang.String> 匹配的键集合
   */
  public static Set<String> keys(String pattern) {
    return determineCache().keys(pattern);
  }

  /**
   * @author: Ares
   * @description: 将当前数据库的键移动到给定的下标数据库当中
   * @description: Move the key of the current database to the given index database
   * @time: 2021-05-17 11:25:00
   * @params: [key, dbIndex] 键，数据库下标
   * @return: java.lang.Boolean 移动结果
   */
  public static Boolean move(String key, int dbIndex) {
    return determineCache().move(key, dbIndex);
  }

  /**
   * @author: Ares
   * @description: 移除键的过期时间，键将持久保持
   * @description: Remove the expiration time of the key, the key will be kept permanently
   * @time: 2021-05-17 11:26:00
   * @params: [key] 键
   * @return: java.lang.Boolean 持久化结果
   */
  public static Boolean persist(String key) {
    return determineCache().persist(key);
  }

  /**
   * @author: Ares
   * @description: 返回键的剩余的过期时间
   * @description: Returns the remaining expiration time of the key
   * @time: 2021-05-17 11:27:00
   * @params: [key, unit] 键，时间单位
   * @return: java.lang.Long 过期时间
   */
  public static Long getExpire(String key, TimeUnit unit) {
    return determineCache().getExpire(key, unit);
  }

  /**
   * @author: Ares
   * @description: 返回键的剩余的过期时间
   * @description: Returns the remaining expiration time of the key
   * @time: 2021-05-17 11:27:00
   * @params: [key] 键
   * @return: java.lang.Long 过期时间
   */
  public static Long getExpire(String key) {
    return determineCache().getExpire(key);
  }

  /**
   * @author: Ares
   * @description: 从当前数据库中随机返回一个
   * @description: Returns a random one from the current database
   * @time: 2021-05-17 11:27:00
   * @return: java.lang.String 随机键
   */
  public static String randomKey() {
    return determineCache().randomKey();
  }

  /**
   * @author: Ares
   * @description: 将老键重命名为新键
   * @description: Rename the old key to the new key
   * @time: 2021-05-17 11:28:00
   * @params: [oldKey, newKey] 老的键，新的键
   */
  public static void rename(String oldKey, String newKey) {
    determineCache().rename(oldKey, newKey);
  }

  /**
   * @author: Ares
   * @description: 仅当新键不存在时，将老键重命名为新键
   * @description: Rename the old key to the new key only if the new key does not exist
   * @time: 2021-05-17 11:28:00
   * @params: [oldKey, newKey] 老的键，新的键
   * @return: java.lang.Boolean 重命名结果
   */
  public static Boolean renameIfAbsent(String oldKey, String newKey) {
    return determineCache().renameIfAbsent(oldKey, newKey);
  }

  /**
   * @author: Ares
   * @description: 返回key所储存的值的类型
   * @time: 2021-05-17 11:29:00
   * @params: [key] 请求参数
   * @return: org.springframework.data.redis.connection.DataType 响应参数
   */
  public static DataType type(String key) {
    return determineCache().type(key);
  }

  /**
   * @author: Ares
   * @description: 设置指定键的值
   * @description: Set the value of the specified key
   * @time: 2021-05-17 11:29:00
   * @params: [key, value] 键，值
   */
  public static <V> void set(String key, V value) {
    determineCache().set(key, value);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的值
   * @description: Get the value of the specified key
   * @time: 2021-05-17 11:29:00
   * @params: [key] 键
   * @return: V 值
   */
  public static <V> V get(String key) {
    CacheOperation<V> operation = determineCache();
    return operation.get(key);
  }

  /**
   * @author: Ares
   * @description: 用指定值覆写给定键所储存的字符串值，从指定偏移量开始
   * @description: Overwrite the string value stored in the given key with the specified value,
   * starting at the specified offset
   * @time: 2021-05-17 11:40:00
   * @params: [key, value, offset] 键, 值, 偏移量
   */
  public static <V> void setRange(String key, V value, long offset) {
    determineCache().setRange(key, value, offset);
  }

  /**
   * @author: Ares
   * @description: 返回键中字符串值的子字符串
   * @description: Returns a substring of the string value in the key
   * @time: 2021-05-17 11:30:00
   * @params: [key, start, end] 键，子字符串开始位置，子字符串结束位置
   * @return: java.lang.String 子字符串
   */
  public static String getRange(String key, long start, long end) {
    return determineCache().getRange(key, start, end);
  }

  /**
   * @author: Ares
   * @description: 将给定键的值设为指定值，并返回键的老的值
   * @description: Set the value of the given key to the specified value and return the old value of
   * the key
   * @time: 2021-05-17 11:30:00
   * @params: [key, value] 键，值
   * @return: V 老的值
   */
  public static <V> V getAndSet(String key, V value) {
    CacheOperation<V> operation = determineCache();
    return operation.getAndSet(key, value);
  }

  /**
   * @author: Ares
   * @description: 对键所储存的字符串值，获取指定偏移量上的位(bit)
   * @description: For the string value stored in the key, get the bit at the specified offset
   * @time: 2021-05-17 11:30:00
   * @params: [key, offset] 键, 偏移量
   * @return: java.lang.Boolean Bit值
   */
  public static Boolean getBit(String key, long offset) {
    return determineCache().getBit(key, offset);
  }

  /**
   * @author: Ares
   * @description: 批量获取结果
   * @description: Batch get results
   * @time: 2021-05-17 11:30:00
   * @params: [keyColl] 键集合
   * @return: java.util.List<V> 结果集合
   */
  public static <V> List<V> multiGet(Collection<String> keyColl) {
    CacheOperation<V> operation = determineCache();
    return operation.multiGet(keyColl);
  }

  /**
   * @author: Ares
   * @description: 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为指定布尔值
   * @description: Set ASCII code, the ASCII code of the string 'a' is 97, converted to binary is
   * '01100001', this method is to change the value of the offset bit of the binary to the specified
   * boolean value
   * @time: 2021-05-17 11:31:00
   * @params: [key, offset, value] 键, 偏移量, 布尔值
   * @return: Boolean 设置结果
   */
  public static Boolean setBit(String key, long offset, boolean value) {
    return determineCache().setBit(key, offset, value);
  }

  /**
   * @author: Ares
   * @description: 将值关联到键，并将键的过期时间设为timeout
   * @description: Associate the value with the key and set the expiration time of the key to
   * timeout
   * @time: 2021-05-17 11:38:00
   * @params: [key, value, timeout, unit] 键, 值, 超时时间, 超时时间单位
   */
  public static <V> void setEx(String key, V value, long timeout, TimeUnit unit) {
    determineCache().setEx(key, value, timeout, unit);
  }

  /**
   * @author: Ares
   * @description: 将值关联到键，并将键的过期时间设为指定超时时间
   * @description: Associate the value with the key and set the expiration time of the key to the
   * specified timeout
   * @time: 2021-05-28 15:17:00
   * @params: [key, value, duration] 请求参数
   */
  public static <V> void setEx(String key, V value, Duration duration) {
    determineCache().setEx(key, value, duration);
  }

  /**
   * @author: Ares
   * @description: 只有在键不存在时设置键的值
   * @description: Set the value of the key only when the key does not exist
   * @time: 2021-05-17 11:39:00
   * @params: [key, value] 键, 值
   * @return: Boolean 设置结果
   */
  public static <V> Boolean setIfAbsent(String key, V value) {
    return determineCache().setIfAbsent(key, value);
  }

  /**
   * @author: Ares
   * @description: 获取指定键对应的字符串的长度
   * @description: Get the length of the string corresponding to the specified key
   * @time: 2021-05-17 11:40:00
   * @params: [key] 键
   * @return: java.lang.Long 字符串的长度
   */
  public static Long size(String key) {
    return determineCache().size(key);
  }

  /**
   * @author: Ares
   * @description: 批量添加
   * @description: Batch add
   * @time: 2021-05-17 11:40:00
   * @params: [map] 映射集合
   */
  public static <V> void multiSet(Map<String, ? extends V> map) {
    determineCache().multiSet(map);
  }

  /**
   * @author: Ares
   * @description: 同时设置一个或多个键值对，当且仅当所有给定键都不存在
   * @description: Set one or more key-value pairs at the same time, only when all the given keys do
   * not exist
   * @time: 2021-05-17 11:40:00
   * @params: [map] 映射集合
   * @return: java.lang.Boolean 设置结果
   */
  public static <V> Boolean multiSetIfAbsent(Map<String, ? extends V> map) {
    return determineCache().multiSetIfAbsent(map);
  }

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地加指定的整数(负数则为自减)
   * @description: Atomically add the specified integer to the value of the specified key (negative
   * for self-decrement)
   * @time: 2021-05-17 11:41:00
   * @params: [key, increment] 键，增量
   * @return: java.lang.Long 增加后的值
   */
  public static Long incrBy(String key, long increment) {
    return determineCache().incrBy(key, increment);
  }

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地加指定的浮点数(负数则为自减)
   * @description: Atomically add the specified floating point number to the value of the specified
   * key (negative for self-decrement)
   * @time: 2021-05-17 11:41:00
   * @params: [key, increment] 键，增量
   * @return: java.lang.Double 增加后的值
   */
  public static Double incrByFloat(String key, double increment) {
    return determineCache().incrByFloat(key, increment);
  }

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地加一
   * @time: 2022-01-04 11:02:52
   * @params: [key] 键
   * @return: java.lang.Long 增加后的值
   */
  public static Long incr(String key) {
    return determineCache().incr(key);
  }

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地减一
   * @time: 2022-01-04 11:03:39
   * @params: [key] 键
   * @return: java.lang.Long 减少后的值
   */
  public static Long decr(String key) {
    return determineCache().decr(key);
  }

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地减指定的整数(负数则为自增)
   * @time: 2022-01-04 11:03:39
   * @params: [key] 键
   * @return: java.lang.Long 减少后的值
   */
  public static Long decr(String key, long decrement) {
    return determineCache().decr(key, decrement);
  }

  /**
   * @author: Ares
   * @description: 追加值到指定键的值的末尾
   * @time: 2021-05-17 11:41:00
   * @params: [key, value] 键，值
   * @return: java.lang.Integer 追加后的值的长度
   */
  public static Integer append(String key, String value) {
    return determineCache().append(key, value);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的哈希表中指定字段的值
   * @time: 2021-05-17 11:41:00
   * @params: [key, hashKey] 键，哈希键
   * @return: HV 值
   */
  public static <HV> HV hGet(String key, Object hashKey) {
    return determineCache().hGet(key, hashKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的哈希表中所有的键值对
   * @time: 2024-09-06 14:19:58
   * @params: [key] 键
   * @return: java.util.Map<String, HV> 结果映射集合
   */
  public static <HV> Map<String, HV> hGetAll(String key) {
    return determineCache().hGetAll(key);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的哈希表中指定哈希键集合对应的值
   * @time: 2024-09-06 14:24:28
   * @params: [key, hashKeyColl] 键，哈希键集合
   * @return: java.util.List<HV> 结果集合
   */
  public static <HV> List<HV> hMultiGet(String key, Collection<String> hashKeyColl) {
    return determineCache().hMultiGet(key, hashKeyColl);
  }

  /**
   * @author: Ares
   * @description: 对指定哈希表放入指定键和值
   * @time: 2021-05-17 11:41:00
   * @params: [key, hashKey, value] 键，哈希键，值
   */
  public static <HV> void hPut(String key, String hashKey, HV value) {
    determineCache().hPut(key, hashKey, value);
  }

  /**
   * @author: Ares
   * @description: 对指定哈希表放入整个映射集合
   * @time: 2021-05-17 11:42:00
   * @params: [key, map] 键，映射集合
   */
  public static <HV> void hPutAll(String key, Map<String, ? extends HV> map) {
    determineCache().hPutAll(key, map);
  }

  /**
   * @author: Ares
   * @description: 仅当哈希键不存在时才设置值
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKey, value] 键，哈希键，值
   * @return: java.lang.Boolean 设置结果
   */
  public static <HV> Boolean hPutIfAbsent(String key, String hashKey, HV value) {
    return determineCache().hPutIfAbsent(key, hashKey, value);
  }

  /**
   * @author: Ares
   * @description: 删除一个或多个哈希表字段
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKeys] 键，哈希键数组
   * @return: java.lang.Long 删除的字段数量
   */
  public static Long hDelete(String key, Object... hashKeys) {
    return determineCache().hDelete(key, hashKeys);
  }

  /**
   * @author: Ares
   * @description: 查看键对应的哈希表中指定的字段是否存在
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKey] 键，哈希键
   * @return: Boolean 是否存在
   */
  public static Boolean hExists(String key, Object hashKey) {
    return determineCache().hExists(key, hashKey);
  }

  /**
   * @author: Ares
   * @description: 为键指定的哈希表中的指定字段的整数值加上增量
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKey, delta] 键，哈希键，增量
   * @return: java.lang.Long 增加后的值
   */
  public static Long hIncrBy(String key, String hashKey, long delta) {
    return determineCache().hIncrBy(key, hashKey, delta);
  }

  /**
   * @author: Ares
   * @description: 为键指定的哈希表中的指定字段的整数值加上浮点数增量
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKey, delta] 键，哈希键，浮点数增量
   * @return: java.lang.Double 增加后的值
   */
  public static Double hIncrByFloat(String key, String hashKey, double delta) {
    return determineCache().hIncrByFloat(key, hashKey, delta);
  }

  /**
   * @author: Ares
   * @description: 获取指定键对应的哈希表中的所有哈希键
   * @time: 2024-09-06 14:33:03
   * @params: [key] 键
   * @return: java.util.Set<String> 哈希键集合
   */
  public static Set<String> hKeys(String key) {
    return determineCache().hKeys(key);
  }

  /**
   * @author: Ares
   * @description: 获取指定键对应的哈希表中的元素的数量
   * @time: 2021-05-17 11:44:00
   * @params: [key] 键
   * @return: java.lang.Long 哈希表中的元素的数量
   */
  public static Long hSize(String key) {
    return determineCache().hSize(key);
  }

  /**
   * @author: Ares
   * @description: 获取指定键对应的哈希表中的所有键的列表
   * @time: 2021-05-17 11:44:00
   * @params: [key] 键
   * @return: java.util.List<HV> 哈希表中的所有键的列表
   */
  public static <HV> List<HV> hValues(String key) {
    return determineCache().hValues(key);
  }

  /**
   * @author: Ares
   * @description: 获取扫描游标
   * @time: 2024-09-06 14:42:58
   * @params: [key, options] 键，扫描选项
   */
  public static <HV> Cursor<Entry<String, HV>> hScan(String key, ScanOptions options) {
    return determineCache().hScan(key, options);
  }

  /**
   * @author: Ares
   * @description: 获取指定键对应的列表中指定下标的元素
   * @time: 2021-05-17 11:44:00
   * @params: [key, index] 键，下标
   * @return: V 结果
   */
  public static <V> V lIndex(String key, long index) {
    CacheOperation<V> operation = determineCache();
    return operation.lIndex(key, index);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的列表中指定范围内的元素
   * @time: 2021-05-17 11:44:00
   * @params: [key, start, end] 键，起始坐标，结束坐标
   * @return: java.util.List<V> 结果列表
   */
  public static <V> List<V> lRange(String key, long start, long end) {
    CacheOperation<V> operation = determineCache();
    return operation.lRange(key, start, end);
  }

  /**
   * @author: Ares
   * @description: 将值存储在指定键的列表的头部
   * @time: 2021-05-17 11:44:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 影响行数
   */
  public static <V> Long lLeftPush(String key, V value) {
    return determineCache().lLeftPush(key, value);
  }

  /**
   * @author: Ares
   * @description: 将多个值存储在指定键的列表的头部
   * @time: 2021-05-17 11:44:00
   * @params: [key, value] 键，值数组
   * @return: java.lang.Long 影响行数
   */
  @SuppressWarnings("unchecked")
  public static <V> Long lLeftPushAll(String key, V... values) {
    return determineCache().lLeftPushAll(key, values);
  }

  /**
   * @author: Ares
   * @description: 将整个集合存储在指定键的列表的头部
   * @time: 2021-05-17 11:45:00
   * @params: [key, value] 键，集合
   * @return: java.lang.Long 影响行数
   */
  public static <V> Long lLeftPushAll(String key, Collection<V> collection) {
    return determineCache().lLeftPushAll(key, collection);
  }

  /**
   * @author: Ares
   * @description: 仅当在指定键的列表存在时将多个值存储到列表的头部
   * @time: 2021-05-17 11:45:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 影响行数
   */
  public static <V> Long lLeftPushIfPresent(String key, V value) {
    return determineCache().lLeftPushIfPresent(key, value);
  }

  /**
   * @author: Ares
   * @description: 在指定键的列表中如果目标值存在则在目标值前面添加指定值
   * @time: 2021-05-17 11:45:00
   * @params: [key, pivot, 影响行数] 键，目标值，值
   * @return: java.lang.Long 响应参数
   */
  public static <V> Long lLeftPush(String key, V pivot, V value) {
    return determineCache().lLeftPush(key, pivot, value);
  }

  /**
   * @author: Ares
   * @description: 在指定键的列表的尾部添加值
   * @time: 2021-05-17 11:44:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 影响行数
   */
  public static <V> Long lRightPush(String key, V value) {
    return determineCache().lRightPush(key, value);
  }

  /**
   * @author: Ares
   * @description: 在指定键的列表的尾部添加多个值
   * @time: 2021-05-17 11:44:00
   * @params: [key, values] 键，值数组
   * @return: java.lang.Long 影响行数
   */
  @SuppressWarnings("unchecked")
  public static <V> Long lRightPushAll(String key, V... values) {
    return determineCache().lRightPushAll(key, values);
  }

  /**
   * @author: Ares
   * @description: 在指定键的列表的尾部添加集合
   * @time: 2021-05-17 11:45:00
   * @params: [key, collection] 键，集合
   * @return: java.lang.Long 影响行数
   */
  public static <V> Long lRightPushAll(String key, Collection<V> collection) {
    return determineCache().lRightPushAll(key, collection);
  }

  /**
   * @author: Ares
   * @description: 仅当在指定键的列表存在时将多个值存储到列表的尾部
   * @time: 2021-05-17 11:46:00
   * @params: [key, value] 请求参数
   * @return: java.lang.Long 影响行数
   */
  public static <V> Long lRightPushIfPresent(String key, V value) {
    return determineCache().lRightPushIfPresent(key, value);
  }

  /**
   * @author: Ares
   * @description: 在指定键的列表中如果目标值存在则在目标值后面添加指定值
   * @time: 2021-05-17 11:46:00
   * @params: [key, pivot, value] 键，目标值，值
   * @return: java.lang.Long 影响行数
   */
  public static <V> Long lRightPush(String key, V pivot, V value) {
    return determineCache().lRightPush(key, pivot, value);
  }

  /**
   * @author: Ares
   * @description: 对指定键的列表通过索引设置元素的值
   * @time: 2021-05-17 11:46:00
   * @params: [key, index, value] 键，下标，值
   */
  public static <V> void lSet(String key, long index, V value) {
    determineCache().lSet(key, index, value);
  }

  /**
   * @author: Ares
   * @description: 移出并获取指定键的列表的第一个元素
   * @time: 2021-05-17 11:46:00
   * @params: [key] 键
   * @return: V 值
   */
  public static <V> V lLeftPop(String key) {
    CacheOperation<V> operation = determineCache();
    return operation.lLeftPop(key);
  }

  /**
   * @author: Ares
   * @description: 移出并获取指定键的列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
   * @time: 2021-05-17 11:46:00
   * @params: [key, timeout, unit] 键，超时时间，单位
   * @return: V 值
   */
  public static <V> V lBLeftPop(String key, long timeout, TimeUnit unit) {
    CacheOperation<V> operation = determineCache();
    return operation.lBLeftPop(key, timeout, unit);
  }

  /**
   * @author: Ares
   * @description: 移出并获取指定键的列表的最后一个元素
   * @time: 2021-05-17 11:46:00
   * @params: [key] 键
   * @return: V 值
   */
  public static <V> V lRightPop(String key) {
    CacheOperation<V> operation = determineCache();
    return operation.lRightPop(key);
  }

  /**
   * @author: Ares
   * @description: 移出并获取指定键的列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
   * @time: 2021-05-17 11:47:00
   * @params: [key, timeout, unit] 键，超时时间，超时时间单位
   * @return: V 值
   */
  public static <V> V lBRightPop(String key, long timeout, TimeUnit unit) {
    CacheOperation<V> operation = determineCache();
    return operation.lBRightPop(key, timeout, unit);
  }

  /**
   * @author: Ares
   * @description: 移除指定键的列表的最后一个元素，并将该元素添加到另一个键的列表的头部并返回
   * @time: 2021-05-17 11:48:00
   * @params: [sourceKey, destinationKey] 源键，目标键
   * @return: V 最后一个元素
   */
  public static <V> V lRightPopAndLeftPush(String sourceKey, String destinationKey) {
    CacheOperation<V> operation = determineCache();
    return operation.lRightPopAndLeftPush(sourceKey, destinationKey);
  }

  /**
   * @author: Ares
   * @description: 移除指定键的列表的最后一个元素，并将该元素添加到另一个键的列表的头部并返回； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
   * @time: 2021-05-17 11:49:00
   * @params: [sourceKey, destinationKey, timeout, unit] 源键，目标键，超时时间，超时时间单位
   * @return: V 最后一个元素
   */
  public static <V> V lBRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout,
      TimeUnit unit) {
    CacheOperation<V> operation = determineCache();
    return operation.lBRightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
  }

  /**
   * @author: Ares
   * @description: 删除指定键的列表中指定个指定值的元素
   * @time: 2021-05-17 11:53:00
   * @params: [key, count, value] 键，个数，值
   * @return: java.lang.Long 影响行数
   */
  public static Long lRemove(String key, long count, Object value) {
    return determineCache().lRemove(key, count, value);
  }

  /**
   * @author: Ares
   * @description: 将指定键的列表裁剪为指定范围内的元素
   * @time: 2021-05-17 11:53:00
   * @params: [key, start, end] 键，开始，结束
   */
  public static void lTrim(String key, long start, long end) {
    determineCache().lTrim(key, start, end);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的列表长度
   * @time: 2021-05-17 11:53:00
   * @params: [key] 键
   * @return: java.lang.Long 列表长度
   */
  public static Long lLen(String key) {
    return determineCache().lLen(key);
  }

  /**
   * @author: Ares
   * @description: 向指定键的不可重复集合中添加元素
   * @time: 2021-05-17 11:53:00
   * @params: [key, values] 键，值数组
   * @return: java.lang.Long 影响行数
   */
  @SuppressWarnings("unchecked")
  public static <V> Long sAdd(String key, V... values) {
    return determineCache().sAdd(key, values);
  }

  /**
   * @author: Ares
   * @description: 从指定键的不可重复集合移除数组中的元素
   * @time: 2021-05-17 11:53:00
   * @params: [key, values] 键，值数组
   * @return: java.lang.Long 影响行数
   */
  public static Long sRemove(String key, Object... values) {
    return determineCache().sRemove(key, values);
  }

  /**
   * @author: Ares
   * @description: 移除并返回指定键的不可重复集合的随机一个元素
   * @time: 2021-05-17 11:54:00
   * @params: [key] 键
   * @return: V 值
   */
  public static <V> V sPop(String key) {
    CacheOperation<V> operation = determineCache();
    return operation.sPop(key);
  }

  /**
   * @author: Ares
   * @description: 将指定键的不可重复集合中指定值的元素转移到指定键的另一个集合
   * @time: 2021-05-17 11:54:00
   * @params: [key, value, destKey] 键，元素值，目标键
   * @return: java.lang.Boolean 转移结果
   */
  public static <V> Boolean sMove(String key, V value, String destKey) {
    return determineCache().sMove(key, value, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的大小
   * @time: 2021-05-17 11:54:00
   * @params: [key] 键
   * @return: java.lang.Long 大小
   */
  public static Long sSize(String key) {
    return determineCache().sSize(key);
  }

  /**
   * @author: Ares
   * @description: 判断集合是否包含value
   * @time: 2021-05-17 13:27:00
   * @params: [key, value] 请求参数
   * @return: java.lang.Boolean 响应参数
   */
  public static Boolean sIsMember(String key, Object value) {
    return determineCache().sIsMember(key, value);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的交集
   * @time: 2021-05-17 13:27:00
   * @params: [key, otherKey] 键，其他键
   * @return: java.util.Set<V> 集合交集
   */
  public static <V> Set<V> sIntersect(String key, String otherKey) {
    CacheOperation<V> operation = determineCache();
    return operation.sIntersect(key, otherKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的交集
   * @time: 2021-05-17 13:27:00
   * @params: [key, otherKeyColl] 键，其他键集合
   * @return: java.util.Set<V> 集合交集
   */
  public static <V> Set<V> sIntersect(String key, Collection<String> otherKeyColl) {
    CacheOperation<V> operation = determineCache();
    return operation.sIntersect(key, otherKeyColl);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的交集存储到目标键的集合中
   * @time: 2021-05-17 13:27:00
   * @params: [key, otherKey, destKey] 键，其他键，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long sIntersectAndStore(String key, String otherKey, String destKey) {
    return determineCache().sIntersectAndStore(key, otherKey, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的交集存储到目标键的集合中
   * @time: 2021-05-17 13:28:00
   * @params: [key, otherKeyColl, destKey] 键，其他键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long sIntersectAndStore(String key, Collection<String> otherKeyColl,
      String destKey) {
    return determineCache().sIntersectAndStore(key, otherKeyColl, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的并集
   * @time: 2021-05-17 13:28:00
   * @params: [key, otherKey] 键，其它键
   * @return: java.util.Set<V> 集合并集
   */
  public static <V> Set<V> sUnion(String key, String otherKey) {
    CacheOperation<V> operation = determineCache();
    return operation.sUnion(key, otherKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的并集
   * @time: 2021-05-17 13:28:00
   * @params: [key, otherKeyColl] 键，其它键集合
   * @return: java.util.Set<V> 集合并集
   */
  public static <V> Set<V> sUnion(String key, Collection<String> otherKeyColl) {
    CacheOperation<V> operation = determineCache();
    return operation.sUnion(key, otherKeyColl);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的并集存储到目标键的集合中
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKey, destKey] 键，其它键，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long sUnionAndStore(String key, String otherKey, String destKey) {
    return determineCache().sUnionAndStore(key, otherKey, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的交集存储到目标键的集合中
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKeyColl, destKey] 键，其它键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long sUnionAndStore(String key, Collection<String> otherKeyColl, String destKey) {
    return determineCache().sUnionAndStore(key, otherKeyColl, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的差集
   * @time: 2021-05-17 13:29
   * @params: [key, otherKey] 键，其它键
   * @return: java.util.Set<V> 集合差集
   */
  public static <V> Set<V> sDifference(String key, String otherKey) {
    CacheOperation<V> operation = determineCache();
    return operation.sDifference(key, otherKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的差集
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKeyColl] 键，其它键集合
   * @return: java.util.Set<V> 集合差集
   */
  public static <V> Set<V> sDifference(String key, Collection<String> otherKeyColl) {
    CacheOperation<V> operation = determineCache();
    return operation.sDifference(key, otherKeyColl);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的差集存储到目标键的集合中
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKey, destKey] 键，其它键，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long sDifference(String key, String otherKey, String destKey) {
    return determineCache().sDifference(key, otherKey, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的差集存储到目标键的集合中
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKeyColl, destKey] 键，其它键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long sDifference(String key, Collection<String> otherKeyColl, String destKey) {
    return determineCache().sDifference(key, otherKeyColl, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的所有元素
   * @time: 2021-05-17 11:24:00
   * @params: [key] 键
   * @return: java.util.Set<V> 集合
   */
  public static <V> Set<V> setMembers(String key) {
    CacheOperation<V> operation = determineCache();
    return operation.setMembers(key);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的随机一个元素
   * @time: 2021-05-17 13:29:00
   * @params: [key] 键
   * @return: V 元素
   */
  public static <V> V sRandomMember(String key) {
    CacheOperation<V> operation = determineCache();
    return operation.sRandomMember(key);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的随机指定个元素
   * @time: 2021-05-17 13:29:00
   * @params: [key, count] 键，个数
   * @return: java.util.List<V> 元素列表
   */
  public static <V> List<V> sRandomMembers(String key, long count) {
    CacheOperation<V> operation = determineCache();
    return operation.sRandomMembers(key, count);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的随机指定个不重复元素
   * @time: 2021-05-17 13:29:00
   * @params: [key, count] 键，个数
   * @return: java.util.Set<V> 元素集合
   */
  public static <V> Set<V> sDistinctRandomMembers(String key, long count) {
    CacheOperation<V> operation = determineCache();
    return operation.sDistinctRandomMembers(key, count);
  }

  /**
   * @author: Ares
   * @description: 获取扫描游标
   * @time: 2024-09-06 15:29:21
   * @params: [key, options] 键，扫描选项
   * @return: org.springframework.data.redis.core.Cursor<V> 可迭代的游标
   */
  public static <V> Cursor<V> sScan(String key, ScanOptions options) {
    CacheOperation<V> operation = determineCache();
    return operation.sScan(key, options);
  }

  /**
   * @author: Ares
   * @description: 向获取指定键的不可重复分数集合添加元素
   * @time: 2021-05-17 13:31:00
   * @params: [key, value, score] 键，值，分数
   * @return: java.lang.Boolean 添加结果
   */
  public static <V> Boolean zAdd(String key, V value, double score) {
    return determineCache().zAdd(key, value, score);
  }

  /**
   * @author: Ares
   * @description: 添加集合
   * @time: 2021-05-17 13:32:00
   * @params: [key, values] 请求参数
   * @return: java.lang.Long 响应参数
   */
  public static <V> Long zAdd(String key, Set<TypedTuple<V>> valueTupleSet) {
    CacheOperation<V> operation = determineCache();
    return operation.zAdd(key, valueTupleSet);
  }

  /**
   * @author: Ares
   * @description: 从获取指定键的不可重复分数集合中删除指定值的元素
   * @time: 2021-05-17 13:32:00
   * @params: [key, values] 请求参数
   * @return: java.lang.Long 响应参数
   */
  public static Long zRemove(String key, Object... values) {
    return determineCache().zRemove(key, values);
  }

  /**
   * @author: Ares
   * @description: 增加指定键的不可重复分数集合中的指定值元素的得分，并返回增加后的分数
   * @time: 2021-05-17 13:33:00
   * @params: [key, value, delta] 键，值，增加分数
   * @return: java.lang.Double 增加后分数
   */
  public static <V> Double zIncrementScore(String key, V value, double delta) {
    return determineCache().zIncrementScore(key, value, delta);
  }

  /**
   * @author: Ares
   * @description: 返回指定键的不可重复分数集合中的指定值元素的排名, 有序集合是按照元素的得分由小到大排列
   * @time: 2021-05-17 13:33:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 排名
   */
  public static Long zRank(String key, Object value) {
    return determineCache().zRank(key, value);
  }

  /**
   * @author: Ares
   * @description: 返回指定键的不可重复分数集合中的指定值元素的排名, 有序集合是按照元素的得分由大到小排列
   * @time: 2021-05-17 13:34:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 排名
   */
  public static Long zReverseRank(String key, Object value) {
    return determineCache().zReverseRank(key, value);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内的元素
   * @time: 2021-05-17 13:34:00
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.util.Set<V> 元素集合
   */
  public static <V> Set<V> zRange(String key, long start, long end) {
    CacheOperation<V> operation = determineCache();
    return operation.zRange(key, start, end);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内的元素及其得分
   * @time: 2024-09-06 15:38:27
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.util.Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple < V>> 结果元组
   */
  public static <V> Set<TypedTuple<V>> zRangeWithScores(String key, long start, long end) {
    CacheOperation<V> operation = determineCache();
    return operation.zRangeWithScores(key, start, end);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素
   * @time: 2021-05-17 13:34:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.util.Set<V> 元素集合
   */
  public static <V> Set<V> zRangeByScore(String key, double min, double max) {
    CacheOperation<V> operation = determineCache();
    return operation.zRangeByScore(key, min, max);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素及其得分
   * @time: 2021-05-17 13:34:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.util.Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<V>> 结果元组
   */
  public static <V> Set<TypedTuple<V>> zRangeByScoreWithScores(String key, double min, double max) {
    CacheOperation<V> operation = determineCache();
    return operation.zRangeByScoreWithScores(key, min, max);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内和得分范围内的元素
   * @time: 2021-05-17 13:34:00
   * @params: [key, min, max, start, end] 键，最小分数，最大分数，开始下标，结束下标
   * @return: java.util.Set<corg.springframework.data.redis.core.ZSetOperations.TypedTuple<V>> 结果元组
   */
  public static <V> Set<TypedTuple<V>> zRangeByScoreWithScores(String key, double min, double max,
      long start, long end) {
    CacheOperation<V> operation = determineCache();
    return operation.zRangeByScoreWithScores(key, min, max, start, end);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内的元素, 有序集合是按照元素的得分由大到小排列
   * @time: 2021-05-17 13:35:00
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.util.Set<V> 元素集合
   */
  public static <V> Set<V> zReverseRange(String key, long start, long end) {
    CacheOperation<V> operation = determineCache();
    return operation.zReverseRange(key, start, end);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内的元素及其得分, 有序集合是按照元素的得分由大到小排列
   * @time: 2024-09-06 15:44:32
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.util.Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<V>> 结果元组
   */
  public static <V> Set<TypedTuple<V>> zReverseRangeWithScores(String key, long start, long end) {
    CacheOperation<V> operation = determineCache();
    return operation.zReverseRangeWithScores(key, start, end);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素, 有序集合是按照元素的得分由大到小排列
   * @time: 2021-05-17 13:35:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.util.Set<V> 元素集合
   */
  public static <V> Set<V> zReverseRangeByScore(String key, double min, double max) {
    CacheOperation<V> operation = determineCache();
    return operation.zReverseRangeByScore(key, min, max);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素及其得分, 有序集合是按照元素的得分由大到小排列
   * @time: 2024-09-06 15:45:38
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.util.Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<V>> 结果元组
   */
  public static <V> Set<TypedTuple<V>> zReverseRangeByScoreWithScores(String key, double min,
      double max) {
    CacheOperation<V> operation = determineCache();
    return operation.zReverseRangeByScoreWithScores(key, min, max);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内和得分范围内的元素, 有序集合是按照元素的得分由大到小排列
   * @time: 2021-05-17 13:35:00
   * @params: [key, min, max, start, end] 键，最小分数，最大分数，开始下标，结束下标
   * @return: java.util.Set<V> 元素集合
   */
  public static <V> Set<V> zReverseRangeByScore(String key, double min, double max, long start,
      long end) {
    CacheOperation<V> operation = determineCache();
    return operation.zReverseRangeByScore(key, min, max, start, end);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素数量
   * @time: 2021-05-17 13:35:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.lang.Long 数量
   */
  public static Long zCount(String key, double min, double max) {
    return determineCache().zCount(key, min, max);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合的元素数量
   * @time: 2021-05-17 13:35:00
   * @params: [key] 键
   * @return: java.lang.Long 元素数量
   */
  public static Long zSize(String key) {
    return determineCache().zSize(key);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合的元素占用内存大小
   * @time: 2021-05-17 13:36:00
   * @params: [key] 键
   * @return: java.lang.Long 占用内存大小
   */
  public static Long zZCard(String key) {
    return determineCache().zZCard(key);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合的指定值元素的得分
   * @time: 2021-05-17 13:36:00
   * @params: [key, value] 键，值
   * @return: java.lang.Double 分数
   */
  public static Double zScore(String key, Object value) {
    return determineCache().zScore(key, value);
  }

  /**
   * @author: Ares
   * @description: 移除指定键的不可重复分数集合指定下标范围的成员
   * @time: 2021-05-17 13:36:00
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.lang.Long 影响行数
   */
  public static Long zRemoveRange(String key, long start, long end) {
    return determineCache().zRemoveRange(key, start, end);
  }

  /**
   * @author: Ares
   * @description: 移除指定键的不可重复分数集合指定得分范围的成员
   * @time: 2021-05-17 13:36:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.lang.Long 影响行数
   */
  public static Long zRemoveRangeByScore(String key, double min, double max) {
    return determineCache().zRemoveRangeByScore(key, min, max);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复得分集合的交集存储到目标键的集合中
   * @time: 2021-05-17 13:36:00
   * @params: [key, otherKey, destKey] 键，其他键，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long zIntersectAndStore(String key, String otherKey, String destKey) {
    return determineCache().zIntersectAndStore(key, otherKey, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复得分集合与集合对应的不可重复得分集合的交集存储到目标键的集合中
   * @time: 2021-05-17 13:36:00
   * @params: [key, otherKeyColl, destKey] 键，其他键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long zIntersectAndStore(String key, Collection<String> otherKeyColl,
      String destKey) {
    return determineCache().zIntersectAndStore(key, otherKeyColl, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复得分集合的并集存储到目标键的集合中
   * @time: 2021-05-17 13:36:00
   * @params: [key, otherKey, destKey] 键，其他键，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long zUnionAndStore(String key, String otherKey, String destKey) {
    return determineCache().zUnionAndStore(key, otherKey, destKey);
  }

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复得分集合与集合对应的不可重复得分集合的并集存储到目标键的集合中
   * @time: 2021-05-17 13:36:00
   * @params: [key, otherKeyColl, destKey] 键，其他键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  public static Long zUnionAndStore(String key, Collection<String> otherKeyColl, String destKey) {
    return determineCache().zUnionAndStore(key, otherKeyColl, destKey);
  }

  /**
   * @author: Ares
   * @description: 模糊扫描
   * @time: 2021-05-17 13:36:00
   * @params: [key, options] 请求参数
   * @return: org.springframework.data.redis.core.Cursor<TypedTuple<V>> 响应参数
   */
  public static <V> Cursor<TypedTuple<V>> zScan(String key, ScanOptions options) {
    CacheOperation<V> operation = determineCache();
    return operation.zScan(key, options);
  }

  /**
   * @author: Ares
   * @description: 设置是否启用事务支持
   * @time: 2024-09-06 16:49:57
   * @params: [enableTransactionSupport] 是否启用事务支持
   */
  public static void setEnableTransactionSupport(boolean enableTransactionSupport) {
    determineCache().setEnableTransactionSupport(enableTransactionSupport);
  }

  public static void multi() {
    determineCache().multi();
  }

  public static void discard() {
    determineCache().discard();
  }

  public static List<Object> exec() {
    return determineCache().exec();
  }

  public static void watch(String key) {
    determineCache().watch(key);
  }

  public static void watch(Collection<String> keyColl) {
    determineCache().watch(keyColl);
  }

  public static void unwatch() {
    determineCache().unwatch();
  }

  public static Long countExistingKeys(Collection<String> keyColl) {
    return determineCache().countExistingKeys(keyColl);
  }

  public static Boolean unlink(String key) {
    return determineCache().unlink(key);
  }

  public static Long unlink(Collection<String> keyColl) {
    return determineCache().unlink(keyColl);
  }

  public static List<Object> exec(RedisSerializer<?> valueSerializer) {
    return determineCache().exec(valueSerializer);
  }

  public static <T> T execute(RedisCallback<T> action) {
    return determineCache().execute(action);
  }

  public static <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
    return determineCache().execute(action, exposeConnection);
  }

  public static <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
    return determineCache().execute(action, exposeConnection, pipeline);
  }

  public static <T> T execute(SessionCallback<T> session) {
    return determineCache().execute(session);
  }

  public static <T> T execute(RedisScript<T> script, List<String> keys, Object... args) {
    return determineCache().execute(script, keys, args);
  }

  public static <T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer,
      RedisSerializer<T> resultSerializer, List<String> keyList, Object... args) {
    return determineCache().execute(script, argsSerializer, resultSerializer, keyList, args);
  }

  public static List<Object> executePipelined(RedisCallback<?> action) {
    return determineCache().executePipelined(action);
  }

  public static List<Object> executePipelined(RedisCallback<?> action,
      @Nullable RedisSerializer<?> resultSerializer) {
    return determineCache().executePipelined(action, resultSerializer);
  }

  public static List<Object> executePipelined(SessionCallback<?> session) {
    return determineCache().executePipelined(session);
  }

  public static List<Object> executePipelined(SessionCallback<?> session,
      @Nullable RedisSerializer<?> resultSerializer) {
    return determineCache().executePipelined(session, resultSerializer);
  }

  public static <T> List<T> sort(SortQuery<String> query, RedisSerializer<T> resultSerializer) {
    return determineCache().sort(query, resultSerializer);
  }

  public static <V> List<V> sort(SortQuery<String> query) {
    CacheOperation<V> operation = determineCache();
    return operation.sort(query);
  }

  public static <T, S> List<T> sort(SortQuery<String> query, BulkMapper<T, S> bulkMapper,
      RedisSerializer<S> resultSerializer) {
    return determineCache().sort(query, bulkMapper, resultSerializer);
  }

  public static void convertAndSend(String channel, Object message) {
    determineCache().convertAndSend(channel, message);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务
   * @time: 2024-09-19 19:13:48
   * @params: [key, runnable] 键，任务
   */
  public static void runWithLock(String key, Runnable runnable) {
    determineCache().runWithLock(key, runnable);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务（超时释放）
   * @time: 2024-09-19 19:14:47
   * @params: [key, leaseTime, runnable] 键，锁超时释放时间，任务
   */
  public static void runWithLock(String key, Duration leaseTime, Runnable runnable) {
    determineCache().runWithLock(key, leaseTime, runnable);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果
   * @time: 2024-09-19 19:15:39
   * @params: [key, supplier] 键，任务
   * @return: T 任务执行结果
   */
  public static <T> T getWithLock(String key, Supplier<T> supplier) {
    return determineCache().getWithLock(key, supplier);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果（超时释放）
   * @time: 2024-09-19 19:15:39
   * @params: [key, leaseTime, supplier] 键，锁超时释放时间，任务
   * @return: T 任务执行结果
   */
  public static <T> T getWithLock(String key, Duration leaseTime, Supplier<T> supplier) {
    return determineCache().getWithLock(key, leaseTime, supplier);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务
   * @time: 2024-09-19 19:16:57
   * @params: [key, runnable] 键，任务
   */
  public static void runWithTryLock(String key, Runnable runnable) {
    determineCache().runWithTryLock(key, runnable);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务（可指定锁获取等待时间）
   * @time: 2024-09-19 19:16:57
   * @params: [key, waitTime, runnable] 键，锁获取等待时间，任务
   */
  public static void runWithTryLock(String key, Duration waitTime, Runnable runnable) {
    determineCache().runWithTryLock(key, waitTime, runnable);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务（可指定锁获取等待时间和超时释放时间）
   * @time: 2024-09-19 19:16:57
   * @params: [key, waitTime, leaseTime, runnable] 键，锁获取等待时间，锁超时释放时间，任务
   */
  public static void runWithTryLock(String key, Duration waitTime, Duration leaseTime,
      Runnable runnable) {
    determineCache().runWithTryLock(key, waitTime, leaseTime, runnable);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果
   * @time: 2024-09-19 19:16:57
   * @params: [key, runnable] 键，任务
   * @return: T 任务执行结果
   */
  public static <T> T getWithTryLock(String key, Supplier<T> supplier) {
    return determineCache().getWithTryLock(key, supplier);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果（可指定锁获取等待时间）
   * @time: 2024-09-19 19:16:57
   * @params: [key, waitTime, runnable] 键，锁获取等待时间，任务
   * @return: T 任务执行结果
   */
  public static <T> T getWithTryLock(String key, Duration waitTime, Supplier<T> supplier) {
    return determineCache().getWithTryLock(key, waitTime, supplier);
  }

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果（可指定锁获取等待时间和超时释放时间）
   * @time: 2024-09-19 19:16:57
   * @params: [key, waitTime, leaseTime, runnable] 键，锁获取等待时间，锁超时释放时间，任务
   * @return: T 任务执行结果
   */
  public static <T> T getWithTryLock(String key, Duration waitTime, Duration leaseTime,
      Supplier<T> supplier) {
    return determineCache().getWithTryLock(key, waitTime, leaseTime, supplier);
  }


  private static <V> CacheOperation<V> determineCache() {
    return (CacheOperation<V>) cacheOperation;
  }

  @Resource
  public <V> void setCacheOperation(CacheOperation<Object> cacheOperation) {
    CacheUtil.cacheOperation = cacheOperation;
  }

}
