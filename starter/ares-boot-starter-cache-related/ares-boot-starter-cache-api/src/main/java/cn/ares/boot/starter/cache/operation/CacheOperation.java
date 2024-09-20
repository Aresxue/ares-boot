package cn.ares.boot.starter.cache.operation;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
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

/**
 * @author: Ares
 * @time: 2024-09-06 11:42:11
 * @description: 缓存操作
 * @description: Cache operation
 * @version: JDK 1.8
 */
public interface CacheOperation<V> {

  /**
   * @author: Ares
   * @description: 设置键值对
   * @description: Set key-value pair
   * @time: 2024-09-06 11:45:05
   * @params: [key, value, duration] 键，值
   * @return: Boolean 设置结果
   */
  Boolean setObject(String key, V value);

  /**
   * @author: Ares
   * @description: 设置键值对（可指定是否压缩）
   * @description: Set key-value pair(can specify whether to compress)
   * @time: 2024-09-06 11:45:05
   * @params: [key, value, isCompress] 键，值，是否压缩
   * @return: Boolean 设置结果
   */
  Boolean setObject(String key, V value, boolean isCompress);

  /**
   * @author: Ares
   * @description: 指定超时时间设置键值对
   * @description: Set key-value pair(can specify whether to compress)
   * @time: 2024-09-06 11:45:05
   * @params: [key, value, duration] 键，值，超时时间
   * @return: Boolean 设置结果
   */
  Boolean setObject(String key, V value, Duration duration);

  /**
   * @author: Ares
   * @description: 指定超时时间设置键值对（可指定是否压缩）
   * @description: Set key-value pair with specified timeout (can specify whether to compress)
   * @time: 2024-09-06 11:45:05
   * @params: [key, value, duration, isCompress] 键，值，超时时间，是否压缩
   * @return: Boolean 设置结果
   */
  Boolean setObject(String key, V value, Duration duration, boolean isCompress);

  /**
   * @author: Ares
   * @description: 指定超时时间设置键值对（可指定是否压缩）
   * @description: Set key-value pair with specified timeout (can specify whether to compress)
   * @time: 2024-09-06 11:48:45
   * @params: [key, value, expirationTime, timeUnit, isCompress] 键，值，超时时间，超时时间单位，是否压缩
   * @return: Boolean 设置结果
   */
  Boolean setObject(String key, V value, long expirationTime, TimeUnit timeUnit,
      boolean isCompress);

  /**
   * @author: Ares
   * @description: 获取键对应的对象
   * @description: Get the object corresponding to the key
   * @time: 2021-05-17 15:14:00
   * @params: [key, clazz] 键, 对象类型
   * @return: V 对象
   */
  <T> T getObject(String key, Class<T> clazz);

  /**
   * @author: Ares
   * @description: 删除指定键
   * @description: Delete the specified key
   * @time: 2022-02-17 15:31:53
   * @params: [key] 键
   * @return: java.lang.Boolean 删除结果
   */
  Boolean delete(String key);

  /**
   * @author: Ares
   * @description: 批量删除键
   * @description: Batch delete keys
   * @time: 2022-02-17 15:32:30
   * @params: [keys] 键集合
   * @return: java.lang.Long 删除结果
   */
  Long delete(Collection<String> keyColl);

  /**
   * @author: Ares
   * @description: 导出键对应的值
   * @description: Export the value corresponding to the key
   * @time: 2021-05-17 11:24:00
   * @params: [key] 键
   */
  byte[] dump(String key);

  /**
   * @author: Ares
   * @description: 是否存在键
   * @description: Whether the key exists
   * @time: 2021-05-17 11:24:00
   * @params: [key] 键
   * @return: java.lang.Boolean 是否存在
   */
  Boolean hasKey(String key);

  /**
   * @author: Ares
   * @description: 设置过期时间
   * @description: Set expiration time
   * @time: 2021-05-17 11:25:00
   * @params: [key, timeout, unit] 键, 超时时间, 时间单位
   * @return: java.lang.Boolean 设置结果
   */
  Boolean expire(String key, long timeout, TimeUnit unit);

  /**
   * @author: Ares
   * @description: 设置过期时间到指定时间
   * @description: Set the expiration time to the specified time
   * @time: 2021-05-17 11:25:00
   * @params: [key, date] 键, 时间
   * @return: java.lang.Boolean 设置结果
   */
  Boolean expireAt(String key, Date date);

  /**
   * @author: Ares
   * @description: 查找匹配的键的集合
   * @description: Find a collection of matching keys
   * @time: 2021-05-17 11:25:00
   * @params: [pattern] 匹配规则
   * @return: java.util.Set<java.lang.String> 匹配的键集合
   */
  Set<String> keys(String pattern);

  /**
   * @author: Ares
   * @description: 将当前数据库的键移动到给定的下标数据库当中
   * @description: Move the key of the current database to the given index database
   * @time: 2021-05-17 11:25:00
   * @params: [key, dbIndex] 键，数据库下标
   * @return: java.lang.Boolean 移动结果
   */
  Boolean move(String key, int dbIndex);

  /**
   * @author: Ares
   * @description: 移除键的过期时间，键将持久保持
   * @description: Remove the expiration time of the key, the key will be kept permanently
   * @time: 2021-05-17 11:26:00
   * @params: [key] 键
   * @return: java.lang.Boolean 持久化结果
   */
  Boolean persist(String key);

  /**
   * @author: Ares
   * @description: 返回键的剩余的过期时间
   * @description: Returns the remaining expiration time of the key
   * @time: 2021-05-17 11:27:00
   * @params: [key, unit] 键，时间单位
   * @return: java.lang.Long 过期时间
   */
  Long getExpire(String key, TimeUnit unit);

  /**
   * @author: Ares
   * @description: 返回键的剩余的过期时间
   * @description: Returns the remaining expiration time of the key
   * @time: 2021-05-17 11:27:00
   * @params: [key] 键
   * @return: java.lang.Long 过期时间
   */
  Long getExpire(String key);

  /**
   * @author: Ares
   * @description: 从当前数据库中随机返回一个
   * @description: Returns a random one from the current database
   * @time: 2021-05-17 11:27:00
   * @return: java.lang.String 随机键
   */
  String randomKey();

  /**
   * @author: Ares
   * @description: 将老键重命名为新键
   * @description: Rename the old key to the new key
   * @time: 2021-05-17 11:28:00
   * @params: [oldKey, newKey] 老的键，新的键
   */
  void rename(String oldKey, String newKey);

  /**
   * @author: Ares
   * @description: 仅当新键不存在时，将老键重命名为新键
   * @description: Rename the old key to the new key only if the new key does not exist
   * @time: 2021-05-17 11:28:00
   * @params: [oldKey, newKey] 老的键，新的键
   * @return: java.lang.Boolean 重命名结果
   */
  Boolean renameIfAbsent(String oldKey, String newKey);

  /**
   * @author: Ares
   * @description: 返回key所储存的值的类型
   * @time: 2021-05-17 11:29:00
   * @params: [key] 请求参数
   * @return: org.springframework.data.redis.connection.DataType 响应参数
   */
  DataType type(String key);

  /**
   * @author: Ares
   * @description: 设置指定键的值
   * @description: Set the value of the specified key
   * @time: 2021-05-17 11:29:00
   * @params: [key, value] 键，值
   */
  void set(String key, V value);

  /**
   * @author: Ares
   * @description: 获取指定键的值
   * @description: Get the value of the specified key
   * @time: 2021-05-17 11:29:00
   * @params: [key] 键
   * @return: V 值
   */
  V get(String key);

  /**
   * @author: Ares
   * @description: 用指定值覆写给定键所储存的字符串值，从指定偏移量开始
   * @description: Overwrite the string value stored in the given key with the specified value, starting at the specified offset
   * @time: 2021-05-17 11:40:00
   * @params: [key, value, offset] 键, 值, 偏移量
   */
  void setRange(String key, V value, long offset);

  /**
   * @author: Ares
   * @description: 返回键中字符串值的子字符串
   * @description: Returns a substring of the string value in the key
   * @time: 2021-05-17 11:30:00
   * @params: [key, start, end] 键，子字符串开始位置，子字符串结束位置
   * @return: java.lang.String 子字符串
   */
  String getRange(String key, long start, long end);

  /**
   * @author: Ares
   * @description: 将给定键的值设为指定值，并返回键的老的值
   * @description: Set the value of the given key to the specified value and return the old value of the key
   * @time: 2021-05-17 11:30:00
   * @params: [key, value] 键，值
   * @return: V 老的值
   */
  V getAndSet(String key, V value);

  /**
   * @author: Ares
   * @description: 对键所储存的字符串值，获取指定偏移量上的位(bit)
   * @description: For the string value stored in the key, get the bit at the specified offset
   * @time: 2021-05-17 11:30:00
   * @params: [key, offset] 键, 偏移量
   * @return: java.lang.Boolean Bit值
   */
  Boolean getBit(String key, long offset);

  /**
   * @author: Ares
   * @description: 批量获取结果
   * @description: Batch get results
   * @time: 2021-05-17 11:30:00
   * @params: [keyColl] 键集合
   * @return: java.util.List<V> 结果集合
   */
  List<V> multiGet(Collection<String> keyColl);

  /**
   * @author: Ares
   * @description: 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为指定布尔值
   * @description: Set ASCII code, the ASCII code of the string 'a' is 97, converted to binary is
   * '01100001', this method is to change the value of the offset bit of the binary to the specified boolean value
   * @time: 2021-05-17 11:31:00
   * @params: [key, offset, value] 键, 偏移量, 布尔值
   * @return: Boolean 设置结果
   */
  Boolean setBit(String key, long offset, boolean value);

  /**
   * @author: Ares
   * @description: 将值关联到键，并将键的过期时间设为timeout
   * @description: Associate the value with the key and set the expiration time of the key to timeout
   * @time: 2021-05-17 11:38:00
   * @params: [key, value, timeout, unit] 键, 值, 超时时间, 超时时间单位
   */
  void setEx(String key, V value, long timeout, TimeUnit unit);

  /**
   * @author: Ares
   * @description: 将值关联到键，并将键的过期时间设为指定超时时间
   * @description: Associate the value with the key and set the expiration time of the key to the specified timeout
   * @time: 2021-05-28 15:17:00
   * @params: [key, value, duration] 请求参数
   */
  void setEx(String key, V value, Duration duration);

  /**
   * @author: Ares
   * @description: 只有在键不存在时设置键的值
   * @description: Set the value of the key only when the key does not exist
   * @time: 2021-05-17 11:39:00
   * @params: [key, value] 键, 值
   * @return: Boolean 设置结果
   */
  Boolean setIfAbsent(String key, V value);

  /**
   * @author: Ares
   * @description: 获取指定键对应的字符串的长度
   * @description: Get the length of the string corresponding to the specified key
   * @time: 2021-05-17 11:40:00
   * @params: [key] 键
   * @return: java.lang.Long 字符串的长度
   */
  Long size(String key);

  /**
   * @author: Ares
   * @description: 批量添加
   * @description: Batch add
   * @time: 2021-05-17 11:40:00
   * @params: [map] 映射集合
   */
  void multiSet(Map<String, ? extends V> map);

  /**
   * @author: Ares
   * @description: 同时设置一个或多个键值对，当且仅当所有给定键都不存在
   * @description: Set one or more key-value pairs at the same time, only when all the given keys do not exist
   * @time: 2021-05-17 11:40:00
   * @params: [map] 映射集合
   * @return: java.lang.Boolean 设置结果
   */
  Boolean multiSetIfAbsent(Map<String, ? extends V> map);

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地加指定的整数(负数则为自减)
   * @description: Atomically add the specified integer to the value of the specified key (negative for self-decrement)
   * @time: 2021-05-17 11:41:00
   * @params: [key, increment] 键，增量
   * @return: java.lang.Long 增加后的值
   */
  Long incrBy(String key, long increment);

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地加指定的浮点数(负数则为自减)
   * @description: Atomically add the specified floating point number to the value of the specified key (negative for self-decrement)
   * @time: 2021-05-17 11:41:00
   * @params: [key, increment] 键，增量
   * @return: java.lang.Double 增加后的值
   */
  Double incrByFloat(String key, double increment);

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地加一
   * @description: Atomically increment the value of the specified key
   * @time: 2022-01-04 11:02:52
   * @params: [key] 键
   * @return: java.lang.Long 增加后的值
   */
  Long incr(String key);

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地减一
   * @description: Atomically decrement the value of the specified key
   * @time: 2022-01-04 11:03:39
   * @params: [key] 键
   * @return: java.lang.Long 减少后的值
   */
  Long decr(String key);

  /**
   * @author: Ares
   * @description: 对指定键的值原子性地减指定的整数(负数则为自增)
   * @description: Atomically subtract the specified integer from the value of the specified key (negative for self-increment)
   * @time: 2022-01-04 11:03:39
   * @params: [key] 键
   * @return: java.lang.Long 减少后的值
   */
  Long decr(String key, long decrement);

  /**
   * @author: Ares
   * @description: 追加值到指定键的值的末尾
   * @description: Append a value to the end of the value of the specified key
   * @time: 2021-05-17 11:41:00
   * @params: [key, value] 键，值
   * @return: java.lang.Integer 追加后的值的长度
   */
  Integer append(String key, String value);

  /**
   * @author: Ares
   * @description: 获取指定键的哈希表中指定字段的值
   * @description: Get the value of the specified field in the hash table of the specified key
   * @time: 2021-05-17 11:41:00
   * @params: [key, hashKey] 键，哈希键
   * @return: HV 值
   */
  <HV> HV hGet(String key, Object hashKey);

  /**
   * @author: Ares
   * @description: 获取指定键的哈希表中所有的键值对
   * @description: Get all key-value pairs in the hash table of the specified key
   * @time: 2024-09-06 14:19:58
   * @params: [key] 键
   * @return: java.util.Map<String, HV> 结果映射集合
   */
  <HV> Map<String, HV> hGetAll(String key);

  /**
   * @author: Ares
   * @description: 获取指定键的哈希表中指定哈希键集合对应的值
   * @time: 2024-09-06 14:24:28
   * @params: [key, hashKeyColl] 键，哈希键集合
   * @return: java.util.List<HV> 结果集合
   */
  <HV> List<HV> hMultiGet(String key, Collection<String> hashKeyColl);

  /**
   * @author: Ares
   * @description: 对指定哈希表放入指定键和值
   * @description: Put the specified key and value into the specified hash table
   * @time: 2021-05-17 11:41:00
   * @params: [key, hashKey, value] 键，哈希键，值
   */
  <HV> void hPut(String key, String hashKey, HV value);

  /**
   * @author: Ares
   * @description: 对指定哈希表放入整个映射集合
   * @description: Put the entire mapping set into the specified hash table
   * @time: 2021-05-17 11:42:00
   * @params: [key, map] 键，映射集合
   */
  <HV> void hPutAll(String key, Map<String, ? extends HV> map);

  /**
   * @author: Ares
   * @description: 仅当哈希键不存在时才设置值
   * @description: Set the value only when the hash key does not exist
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKey, value] 键，哈希键，值
   * @return: java.lang.Boolean 设置结果
   */
  <HV> Boolean hPutIfAbsent(String key, String hashKey, HV value);

  /**
   * @author: Ares
   * @description: 删除一个或多个哈希表字段
   * @description: Delete one or more hash table fields
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKeys] 键，哈希键数组
   * @return: java.lang.Long 删除的字段数量
   */
  Long hDelete(String key, Object... hashKeys);

  /**
   * @author: Ares
   * @description: 查看键对应的哈希表中指定的字段是否存在
   * @description: Check whether the specified field in the hash table corresponding to the key
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKey] 键，哈希键
   * @return: Boolean 是否存在
   */
  Boolean hExists(String key, Object hashKey);

  /**
   * @author: Ares
   * @description: 为键指定的哈希表中的指定字段的整数值加上增量
   * @description: Add the increment to the integer value of the specified field in the hash table
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKey, delta] 键，哈希键，增量
   * @return: java.lang.Long 增加后的值
   */
  Long hIncrBy(String key, String hashKey, long delta);

  /**
   * @author: Ares
   * @description: 为键指定的哈希表中的指定字段的整数值加上浮点数增量
   * @description: Add the floating point increment to the integer value of the specified field in
   * @time: 2021-05-17 11:43:00
   * @params: [key, hashKey, delta] 键，哈希键，浮点数增量
   * @return: java.lang.Double 增加后的值
   */
  Double hIncrByFloat(String key, String hashKey, double delta);

  /**
   * @author: Ares
   * @description: 获取指定键对应的哈希表中的所有哈希键
   * @description: Get all hash keys in the hash table corresponding to the specified key
   * @time: 2024-09-06 14:33:03
   * @params: [key] 键
   * @return: java.util.Set<String> 哈希键集合
   */
  Set<String> hKeys(String key);

  /**
   * @author: Ares
   * @description: 获取指定键对应的哈希表中的元素的数量
   * @description: Get the number of elements in the hash table corresponding to the specified key
   * @time: 2021-05-17 11:44:00
   * @params: [key] 键
   * @return: java.lang.Long 哈希表中的元素的数量
   */
  Long hSize(String key);

  /**
   * @author: Ares
   * @description: 获取指定键对应的哈希表中的所有键的列表
   * @description: Get a list of all keys in the hash table corresponding to the specified key
   * @time: 2021-05-17 11:44:00
   * @params: [key] 键
   * @return: java.util.List<HV> 哈希表中的所有键的列表
   */
  <HV> List<HV> hValues(String key);

  /**
   * @author: Ares
   * @description: 获取扫描游标
   * @description: Get the scan cursor
   * @time: 2024-09-06 14:42:58
   * @params: [key, options] 键，扫描选项
   */
  <HV> Cursor<Entry<String, HV>> hScan(String key, ScanOptions options);

  /**
   * @author: Ares
   * @description: 获取指定键对应的列表中指定下标的元素
   * @description: Get the element at the specified index in the list corresponding to the specified key
   * @time: 2021-05-17 11:44:00
   * @params: [key, index] 键，下标
   * @return: V 结果
   */
  V lIndex(String key, long index);

  /**
   * @author: Ares
   * @description: 获取指定键的列表中指定范围内的元素
   * @description: Get the elements in the specified range of the list corresponding to the
   * @time: 2021-05-17 11:44:00
   * @params: [key, start, end] 键，起始坐标，结束坐标
   * @return: java.util.List<V> 结果列表
   */
  List<V> lRange(String key, long start, long end);

  /**
   * @author: Ares
   * @description: 将值存储在指定键的列表的头部
   * @description: Store the value at the head of the list of the specified key
   * @time: 2021-05-17 11:44:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 影响行数
   */
  Long lLeftPush(String key, V value);

  /**
   * @author: Ares
   * @description: 将多个值存储在指定键的列表的头部
   * @description: Store multiple values at the head of the list corresponding to the specified key
   * @time: 2021-05-17 11:44:00
   * @params: [key, value] 键，值数组
   * @return: java.lang.Long 影响行数
   */
  @SuppressWarnings("unchecked")
  Long lLeftPushAll(String key, V... values);

  /**
   * @author: Ares
   * @description: 将整个集合存储在指定键的列表的头部
   * @description: Store the entire collection at the head of the list corresponding to the specified
   * @time: 2021-05-17 11:45:00
   * @params: [key, value] 键，集合
   * @return: java.lang.Long 影响行数
   */
  Long lLeftPushAll(String key, Collection<V> collection);

  /**
   * @author: Ares
   * @description: 仅当在指定键的列表存在时将多个值存储到列表的头部
   * @description: Store multiple values at the head of the list only when the list corresponding to the specified key exists
   * @time: 2021-05-17 11:45:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 影响行数
   */
  Long lLeftPushIfPresent(String key, V value);

  /**
   * @author: Ares
   * @description: 在指定键的列表中如果目标值存在则在目标值前面添加指定值
   * @description: If the target value exists in the list corresponding to the specified key, add the specified value in front of the target value
   * @time: 2021-05-17 11:45:00
   * @params: [key, pivot, 影响行数] 键，目标值，值
   * @return: java.lang.Long 响应参数
   */
  Long lLeftPush(String key, V pivot, V value);

  /**
   * @author: Ares
   * @description: 在指定键的列表的尾部添加值
   * @description: Add a value to the end of the list corresponding to the specified key
   * @time: 2021-05-17 11:44:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 影响行数
   */
  Long lRightPush(String key, V value);

  /**
   * @author: Ares
   * @description: 在指定键的列表的尾部添加多个值
   * @description: Add multiple values to the end of the list corresponding to the specified key
   * @time: 2021-05-17 11:44:00
   * @params: [key, values] 键，值数组
   * @return: java.lang.Long 影响行数
   */
  @SuppressWarnings("unchecked")
  Long lRightPushAll(String key, V... values);

  /**
   * @author: Ares
   * @description: 在指定键的列表的尾部添加集合
   * @description: Add a collection to the end of the list corresponding to the specified key
   * @time: 2021-05-17 11:45:00
   * @params: [key, collection] 键，集合
   * @return: java.lang.Long 影响行数
   */
  Long lRightPushAll(String key, Collection<V> collection);

  /**
   * @author: Ares
   * @description: 仅当在指定键的列表存在时将多个值存储到列表的尾部
   * @description: Store multiple values at the end of the list only when the list corresponding to the specified key exists
   * @time: 2021-05-17 11:46:00
   * @params: [key, value] 请求参数
   * @return: java.lang.Long 影响行数
   */
  Long lRightPushIfPresent(String key, V value);

  /**
   * @author: Ares
   * @description: 在指定键的列表中如果目标值存在则在目标值后面添加指定值
   * @description: If the target value exists in the list corresponding to the specified key, add the specified value after the target value
   * @time: 2021-05-17 11:46:00
   * @params: [key, pivot, value] 键，目标值，值
   * @return: java.lang.Long 影响行数
   */
  Long lRightPush(String key, V pivot, V value);

  /**
   * @author: Ares
   * @description: 对指定键的列表通过索引设置元素的值
   * @description: Set the value of an element in the list corresponding to the specified key by index
   * @time: 2021-05-17 11:46:00
   * @params: [key, index, value] 键，下标，值
   */
  void lSet(String key, long index, V value);

  /**
   * @author: Ares
   * @description: 移出并获取指定键的列表的第一个元素
   * @description: Remove and get the first element of the list corresponding to the specified key
   * @time: 2021-05-17 11:46:00
   * @params: [key] 键
   * @return: V 值
   */
  V lLeftPop(String key);

  /**
   * @author: Ares
   * @description: 移出并获取指定键的列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
   * @description: Remove and get the first element of the list corresponding to the specified key, if the list is empty, it will block the list until the wait timeout or find the element to pop
   * @time: 2021-05-17 11:46:00
   * @params: [key, timeout, unit] 键，超时时间，单位
   * @return: V 值
   */
  V lBLeftPop(String key, long timeout, TimeUnit unit);

  /**
   * @author: Ares
   * @description: 移出并获取指定键的列表的最后一个元素
   * @description: Remove and get the last element of the list corresponding to the specified key
   * @time: 2021-05-17 11:46:00
   * @params: [key] 键
   * @return: V 值
   */
  V lRightPop(String key);

  /**
   * @author: Ares
   * @description: 移出并获取指定键的列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
   * @description: Remove and get the last element of the list corresponding to the specified key, if the list is empty, it will block the list until the wait timeout or find the element to pop
   * @time: 2021-05-17 11:47:00
   * @params: [key, timeout, unit] 键，超时时间，超时时间单位
   * @return: V 值
   */
  V lBRightPop(String key, long timeout, TimeUnit unit);

  /**
   * @author: Ares
   * @description: 移除指定键的列表的最后一个元素，并将该元素添加到另一个键的列表的头部并返回
   * @description: Remove the last element of the list corresponding to the specified key and add it to the head of the list corresponding to another key and return
   * @time: 2021-05-17 11:48:00
   * @params: [sourceKey, destinationKey] 源键，目标键
   * @return: V 最后一个元素
   */
  V lRightPopAndLeftPush(String sourceKey, String destinationKey);

  /**
   * @author: Ares
   * @description: 移除指定键的列表的最后一个元素，并将该元素添加到另一个键的列表的头部并返回； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
   * @description: Remove the last element of the list corresponding to the specified key and add it to the head of the list corresponding to another key and return; if the list is empty, it will block the list until the wait timeout or find the element to pop
   * @time: 2021-05-17 11:49:00
   * @params: [sourceKey, destinationKey, timeout, unit] 源键，目标键，超时时间，超时时间单位
   * @return: V 最后一个元素
   */
  V lBRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit);

  /**
   * @author: Ares
   * @description: 删除指定键的列表中指定个指定值的元素
   * @description: Delete the specified number of specified values in the list corresponding to the specified key
   * @time: 2021-05-17 11:53:00
   * @params: [key, count, value] 键，个数，值
   * @return: java.lang.Long 影响行数
   */
  Long lRemove(String key, long count, Object value);

  /**
   * @author: Ares
   * @description: 将指定键的列表裁剪为指定范围内的元素
   * @description: Trim the list corresponding to the specified key to the elements within the specified range
   * @time: 2021-05-17 11:53:00
   * @params: [key, start, end] 键，开始，结束
   */
  void lTrim(String key, long start, long end);

  /**
   * @author: Ares
   * @description: 获取指定键的列表长度
   * @description: Get the length of the list corresponding to the specified key
   * @time: 2021-05-17 11:53:00
   * @params: [key] 键
   * @return: java.lang.Long 列表长度
   */
  Long lLen(String key);

  /**
   * @author: Ares
   * @description: 向指定键的不可重复集合中添加元素
   * @description: Add elements to the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 11:53:00
   * @params: [key, values] 键，值数组
   * @return: java.lang.Long 影响行数
   */
  @SuppressWarnings("unchecked")
  Long sAdd(String key, V... values);

  /**
   * @author: Ares
   * @description: 从指定键的不可重复集合移除数组中的元素
   * @description: Remove elements from the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 11:53:00
   * @params: [key, values] 键，值数组
   * @return: java.lang.Long 影响行数
   */
  Long sRemove(String key, Object... values);

  /**
   * @author: Ares
   * @description: 移除并返回指定键的不可重复集合的随机一个元素
   * @description: Remove and return a random element of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 11:54:00
   * @params: [key] 键
   * @return: V 值
   */
  V sPop(String key);

  /**
   * @author: Ares
   * @description: 将指定键的不可重复集合中指定值的元素转移到指定键的另一个集合
   * @description: Move the element of the specified value in the non-repeating set corresponding to the specified key to another set corresponding to the specified key
   * @time: 2021-05-17 11:54:00
   * @params: [key, value, destKey] 键，元素值，目标键
   * @return: java.lang.Boolean 转移结果
   */
  Boolean sMove(String key, V value, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的大小
   * @description: Get the size of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 11:54:00
   * @params: [key] 键
   * @return: java.lang.Long 大小
   */
  Long sSize(String key);

  /**
   * @author: Ares
   * @description: 判断集合是否包含value
   * @time: 2021-05-17 13:27:00
   * @params: [key, value] 请求参数
   * @return: java.lang.Boolean 响应参数
   */
  Boolean sIsMember(String key, Object value);

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的交集
   * @description: Get the intersection of two non-repeating sets corresponding to the specified key
   * @time: 2021-05-17 13:27:00
   * @params: [key, otherKey] 键，其他键
   * @return: java.util.Set<V> 集合交集
   */
  Set<V> sIntersect(String key, String otherKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的交集
   * @description: Get the intersection of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 13:27:00
   * @params: [key, otherKeyColl] 键，其他键集合
   * @return: java.util.Set<V> 集合交集
   */
  Set<V> sIntersect(String key, Collection<String> otherKeyColl);

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的交集存储到目标键的集合中
   * @description: Get the intersection of two non-repeating sets corresponding to the specified key
   * @time: 2021-05-17 13:27:00
   * @params: [key, otherKey, destKey] 键，其他键，目标键
   * @return: java.lang.Long 影响行数
   */
  Long sIntersectAndStore(String key, String otherKey, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的交集存储到目标键的集合中
   * @description: Get the intersection of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 13:28:00
   * @params: [key, otherKeyColl, destKey] 键，其他键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  Long sIntersectAndStore(String key, Collection<String> otherKeyColl, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的并集
   * @description: Get the union of two non-repeating sets corresponding to the specified key
   * @time: 2021-05-17 13:28:00
   * @params: [key, otherKey] 键，其它键
   * @return: java.util.Set<V> 集合并集
   */
  Set<V> sUnion(String key, String otherKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的并集
   * @description: Get the union of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 13:28:00
   * @params: [key, otherKeyColl] 键，其它键集合
   * @return: java.util.Set<V> 集合并集
   */
  Set<V> sUnion(String key, Collection<String> otherKeyColl);

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的并集存储到目标键的集合中
   * @description: Get the union of two non-repeating sets corresponding to the specified key
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKey, destKey] 键，其它键，目标键
   * @return: java.lang.Long 影响行数
   */
  Long sUnionAndStore(String key, String otherKey, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的交集存储到目标键的集合中
   * @description: Get the intersection of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKeyColl, destKey] 键，其它键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  Long sUnionAndStore(String key, Collection<String> otherKeyColl, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的差集
   * @description: Get the difference of two non-repeating sets corresponding to the specified key
   * @time: 2021-05-17 13:29
   * @params: [key, otherKey] 键，其它键
   * @return: java.util.Set<V> 集合差集
   */
  Set<V> sDifference(String key, String otherKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的差集
   * @description: Get the difference of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKeyColl] 键，其它键集合
   * @return: java.util.Set<V> 集合差集
   */
  Set<V> sDifference(String key, Collection<String> otherKeyColl);

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复集合的差集存储到目标键的集合中
   * @description: Get the difference of two non-repeating sets corresponding to the specified key
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKey, destKey] 键，其它键，目标键
   * @return: java.lang.Long 影响行数
   */
  Long sDifference(String key, String otherKey, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合与集合对应的不可重复集合的差集存储到目标键的集合中
   * @description: Get the difference of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 13:29:00
   * @params: [key, otherKeyColl, destKey] 键，其它键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  Long sDifference(String key, Collection<String> otherKeyColl, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的所有元素
   * @description: Get all elements of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 11:24:00
   * @params: [key] 键
   * @return: java.util.Set<V> 集合
   */
  Set<V> setMembers(String key);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的随机一个元素
   * @description: Get a random element of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 13:29:00
   * @params: [key] 键
   * @return: V 元素
   */
  V sRandomMember(String key);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的随机指定个元素
   * @description: Get the specified number of random elements of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 13:29:00
   * @params: [key, count] 键，个数
   * @return: java.util.List<V> 元素列表
   */
  List<V> sRandomMembers(String key, long count);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复集合的随机指定个不重复元素
   * @description: Get the specified number of random non-repeating elements of the non-repeating set corresponding to the specified key
   * @time: 2021-05-17 13:29:00
   * @params: [key, count] 键，个数
   * @return: java.util.Set<V> 元素集合
   */
  Set<V> sDistinctRandomMembers(String key, long count);

  /**
   * @author: Ares
   * @description: 获取扫描游标
   * @time: 2024-09-06 15:29:21
   * @params: [key, options] 键，扫描选项
   * @return: org.springframework.data.redis.core.Cursor<V> 可迭代的游标
   */
  Cursor<V> sScan(String key, ScanOptions options);

  /**
   * @author: Ares
   * @description: 向获取指定键的不可重复分数集合添加元素
   * @description: Add elements to the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:31:00
   * @params: [key, value, score] 键，值，分数
   * @return: java.lang.Boolean 添加结果
   */
  Boolean zAdd(String key, V value, double score);

  /**
   * @author: Ares
   * @description: 添加集合
   * @time: 2021-05-17 13:32:00
   * @params: [key, values] 请求参数
   * @return: java.lang.Long 响应参数
   */
  Long zAdd(String key, Set<TypedTuple<V>> valueTupleSet);

  /**
   * @author: Ares
   * @description: 从获取指定键的不可重复分数集合中删除指定值的元素
   * @description: Remove the element of the specified value from the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:32:00
   * @params: [key, values] 请求参数
   * @return: java.lang.Long 响应参数
   */
  Long zRemove(String key, Object... values);

  /**
   * @author: Ares
   * @description: 增加指定键的不可重复分数集合中的指定值元素的得分，并返回增加后的分数
   * @description: Increase the score of the specified value element in the non-repeating score set corresponding to the specified key, and return the score after the increase
   * @time: 2021-05-17 13:33:00
   * @params: [key, value, delta] 键，值，增加分数
   * @return: java.lang.Double 增加后分数
   */
  Double zIncrementScore(String key, V value, double delta);

  /**
   * @author: Ares
   * @description: 返回指定键的不可重复分数集合中的指定值元素的排名, 有序集合是按照元素的得分由小到大排列
   * @description: Return the ranking of the specified value element in the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:33:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 排名
   */
  Long zRank(String key, Object value);

  /**
   * @author: Ares
   * @description: 返回指定键的不可重复分数集合中的指定值元素的排名, 有序集合是按照元素的得分由大到小排列
   * @description: Return the ranking of the specified value element in the non-repeating
   * @time: 2021-05-17 13:34:00
   * @params: [key, value] 键，值
   * @return: java.lang.Long 排名
   */
  Long zReverseRank(String key, Object value);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内的元素
   * @description: Get the elements in the specified range of the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:34:00
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.util.Set<V> 元素集合
   */
  Set<V> zRange(String key, long start, long end);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内的元素及其得分
   * @description: Get the elements and their scores in the specified range of the non-repeating score set corresponding to the specified key
   * @time: 2024-09-06 15:38:27
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.util.Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<V>> 结果元组
   */
  Set<TypedTuple<V>> zRangeWithScores(String key, long start, long end);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素
   * @description: Get the elements in the specified score range of the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:34:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.util.Set<V> 元素集合
   */
  Set<V> zRangeByScore(String key, double min, double max);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素及其得分
   * @description: Get the elements and their scores in the specified range of the non-repeating
   * @time: 2021-05-17 13:34:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.util.Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<V>> 结果元组
   */
  Set<TypedTuple<V>> zRangeByScoreWithScores(String key, double min, double max);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内和得分范围内的元素
   * @description: Get the elements in the specified range and score range of the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:34:00
   * @params: [key, min, max, start, end] 键，最小分数，最大分数，开始下标，结束下标
   * @return: java.util.Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<V>> 结果元组
   */
  Set<TypedTuple<V>> zRangeByScoreWithScores(String key, double min, double max, long start,
      long end);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内的元素, 有序集合是按照元素的得分由大到小排列
   * @description: Get the elements in the specified range of the non-repeating score set
   * @time: 2021-05-17 13:35:00
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.util.Set<V> 元素集合
   */
  Set<V> zReverseRange(String key, long start, long end);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内的元素及其得分, 有序集合是按照元素的得分由大到小排列
   * @description: Get the elements and their scores in the specified range of the non-repeating
   * @time: 2024-09-06 15:44:32
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.util.Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<V>> 结果元组
   */
  Set<TypedTuple<V>> zReverseRangeWithScores(String key, long start, long end);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素, 有序集合是按照元素的得分由大到小排列
   * @description: Get the elements in the specified score range of the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:35:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.util.Set<V> 元素集合
   */
  Set<V> zReverseRangeByScore(String key, double min, double max);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素及其得分, 有序集合是按照元素的得分由大到小排列
   * @description: Get the elements and their scores in the specified range of the non-repeating
   * @time: 2024-09-06 15:45:38
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.util.Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<V>> 结果元组
   */
  Set<TypedTuple<V>> zReverseRangeByScoreWithScores(String key, double min, double max);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定下标范围内和得分范围内的元素, 有序集合是按照元素的得分由大到小排列
   * @description: Get the elements in the specified range and score range of the non-repeating
   * @time: 2021-05-17 13:35:00
   * @params: [key, min, max, start, end] 键，最小分数，最大分数，开始下标，结束下标
   * @return: java.util.Set<V> 元素集合
   */
  Set<V> zReverseRangeByScore(String key, double min, double max, long start, long end);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合中指定得分范围内的元素数量
   * @description: Get the number of elements in the specified score range of the non-repeating
   * @time: 2021-05-17 13:35:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.lang.Long 数量
   */
  Long zCount(String key, double min, double max);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合的元素数量
   * @description: Get the number of elements in the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:35:00
   * @params: [key] 键
   * @return: java.lang.Long 元素数量
   */
  Long zSize(String key);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合的元素占用内存大小
   * @description: Get the memory size of the elements in the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:36:00
   * @params: [key] 键
   * @return: java.lang.Long 占用内存大小
   */
  Long zZCard(String key);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复分数集合的指定值元素的得分
   * @description: Get the score of the specified value element in the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:36:00
   * @params: [key, value] 键，值
   * @return: java.lang.Double 分数
   */
  Double zScore(String key, Object value);

  /**
   * @author: Ares
   * @description: 移除指定键的不可重复分数集合指定下标范围的成员
   * @description: Remove members of the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:36:00
   * @params: [key, start, end] 键，开始下标，结束下标
   * @return: java.lang.Long 影响行数
   */
  Long zRemoveRange(String key, long start, long end);

  /**
   * @author: Ares
   * @description: 移除指定键的不可重复分数集合指定得分范围的成员
   * @description: Remove members of the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:36:00
   * @params: [key, min, max] 键，最小分数，最大分数
   * @return: java.lang.Long 影响行数
   */
  Long zRemoveRangeByScore(String key, double min, double max);

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复得分集合的交集存储到目标键的集合中
   * @description: Get the intersection of two non-repeating score sets corresponding to the specified key
   * @time: 2021-05-17 13:36:00
   * @params: [key, otherKey, destKey] 键，其他键，目标键
   * @return: java.lang.Long 影响行数
   */
  Long zIntersectAndStore(String key, String otherKey, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复得分集合与集合对应的不可重复得分集合的交集存储到目标键的集合中
   * @description: Get the intersection of the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:36:00
   * @params: [key, otherKeyColl, destKey] 键，其他键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  Long zIntersectAndStore(String key, Collection<String> otherKeyColl, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的两个不可重复得分集合的并集存储到目标键的集合中
   * @description: Get the union of two non-repeating score sets corresponding to the specified key
   * @time: 2021-05-17 13:36:00
   * @params: [key, otherKey, destKey] 键，其他键，目标键
   * @return: java.lang.Long 影响行数
   */
  Long zUnionAndStore(String key, String otherKey, String destKey);

  /**
   * @author: Ares
   * @description: 获取指定键的不可重复得分集合与集合对应的不可重复得分集合的并集存储到目标键的集合中
   * @description: Get the union of the non-repeating score set corresponding to the specified key
   * @time: 2021-05-17 13:36:00
   * @params: [key, otherKeyColl, destKey] 键，其他键集合，目标键
   * @return: java.lang.Long 影响行数
   */
  Long zUnionAndStore(String key, Collection<String> otherKeyColl, String destKey);

  /**
   * @author: Ares
   * @description: 模糊扫描
   * @time: 2021-05-17 13:36:00
   * @params: [key, options] 请求参数
   * @return: org.springframework.data.redis.core.Cursor<TypedTuple<V>> 响应参数
   */
  Cursor<TypedTuple<V>> zScan(String key, ScanOptions options);

  /**
   * @author: Ares
   * @description: 设置是否启用事务支持
   * @description: Set whether to enable transaction support
   * @time: 2024-09-06 16:49:57
   * @params: [enableTransactionSupport] 是否启用事务支持
   */
  void setEnableTransactionSupport(boolean enableTransactionSupport);

  void multi();

  void discard();

  List<Object> exec();

  void watch(String key);

  void watch(Collection<String> keyColl);

  void unwatch();

  Long countExistingKeys(Collection<String> keyColl);

  Boolean unlink(String key);

  Long unlink(Collection<String> keyColl);

  List<Object> exec(RedisSerializer<?> valueSerializer);

  <T> T execute(RedisCallback<T> action);

  <T> T execute(RedisCallback<T> action, boolean exposeConnection);

  <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline);

  <T> T execute(SessionCallback<T> session);

  <T> T execute(RedisScript<T> script, List<String> keys, Object... args);

  <T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer,
      RedisSerializer<T> resultSerializer, List<String> keyList, Object... args);

  List<Object> executePipelined(RedisCallback<?> action);

  List<Object> executePipelined(RedisCallback<?> action,
      @Nullable RedisSerializer<?> resultSerializer);

  List<Object> executePipelined(SessionCallback<?> session);

  List<Object> executePipelined(SessionCallback<?> session,
      @Nullable RedisSerializer<?> resultSerializer);

  <T> List<T> sort(SortQuery<String> query, RedisSerializer<T> resultSerializer);

  List<V> sort(SortQuery<String> query);

  <T, S> List<T> sort(SortQuery<String> query, BulkMapper<T, S> bulkMapper,
      RedisSerializer<S> resultSerializer);

  void convertAndSend(String channel, Object message);

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务
   * @description: Execute tasks with distributed locks with specified keys
   * @time: 2024-09-19 19:13:48
   * @params: [key, runnable] 键，任务
   */
  void runWithLock(String key, Runnable runnable);
  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务（超时释放）
   * @description: Execute tasks with distributed locks with specified keys (timeout release)
   * @time: 2024-09-19 19:14:47
   * @params: [key, leaseTime, runnable] 键，锁超时释放时间，任务
   */
  void runWithLock(String key, Duration leaseTime, Runnable runnable);

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果
   * @description: Execute tasks with distributed locks with specified keys to get results
   * @time: 2024-09-19 19:15:39
   * @params: [key, supplier] 键，任务
   * @return: T 任务执行结果
   */
  <T> T getWithLock(String key, Supplier<T> supplier);
  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果（超时释放）
   * @description: Execute tasks with distributed locks with specified keys to get results (timeout)
   * @time: 2024-09-19 19:15:39
   * @params: [key, leaseTime, supplier] 键，锁超时释放时间，任务
   * @return: T 任务执行结果
   */
  <T> T getWithLock(String key, Duration leaseTime, Supplier<T> supplier);

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务
   * @description: Execute tasks with distributed locks with specified keys
   * @time: 2024-09-19 19:16:57
   * @params: [key, runnable] 键，任务
   */
  void runWithTryLock(String key, Runnable runnable);
  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务（可指定锁获取等待时间）
   * @description: Execute tasks with distributed locks with specified keys (you can specify the lock acquisition waiting time)
   * @time: 2024-09-19 19:16:57
   * @params: [key, waitTime, runnable] 键，锁获取等待时间，任务
   */
  void runWithTryLock(String key, Duration waitTime, Runnable runnable);
  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务（可指定锁获取等待时间和超时释放时间）
   * @description: Execute tasks with distributed locks with specified keys (you can specify the lock acquisition waiting time and timeout release time)
   * @time: 2024-09-19 19:16:57
   * @params: [key, waitTime, leaseTime, runnable] 键，锁获取等待时间，锁超时释放时间，任务
   */
  void runWithTryLock(String key, Duration waitTime, Duration leaseTime, Runnable runnable);

  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果
   * @description: Execute tasks with distributed locks with specified keys to get results
   * @time: 2024-09-19 19:16:57
   * @params: [key, runnable] 键，任务
   */
  <T> T getWithTryLock(String key, Supplier<T> supplier);
  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果（可指定锁获取等待时间）
   * @description: Execute tasks with distributed locks with specified keys to get results (you can specify the lock acquisition waiting time)
   * @time: 2024-09-19 19:16:57
   * @params: [key, waitTime, runnable] 键，锁获取等待时间，任务
   */
  <T> T getWithTryLock(String key, Duration waitTime, Supplier<T> supplier);
  /**
   * @author: Ares
   * @description: 带着指定键的分布式锁执行任务获取结果（可指定锁获取等待时间和超时释放时间）
   * @description: Execute tasks with distributed locks with specified keys to get results (you can specify the lock acquisition waiting time and timeout release time)
   * @time: 2024-09-19 19:16:57
   * @params: [key, waitTime, leaseTime, runnable] 键，锁获取等待时间，锁超时释放时间，任务
   */
  <T> T getWithTryLock(String key, Duration waitTime, Duration leaseTime, Supplier<T> supplier);

}
