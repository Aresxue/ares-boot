package cn.ares.boot.util.common.structure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: Ares
 * @time: 2022-02-09 16:26:01
 * @description: Lru cache
 * @version: JDK 1.8
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {

  private static final long serialVersionUID = -8336395808351083625L;

  private static final float DEFAULT_LOAD_FACTOR = 0.75f;
  private static final int DEFAULT_MAX_CAPACITY = 1000;
  private static final ReentrantReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();
  private static final Lock READ_LOCK = READ_WRITE_LOCK.readLock();
  private static final Lock WRITE_LOCK = READ_WRITE_LOCK.writeLock();

  private final int maxCapacity;

  public LruCache() {
    this(DEFAULT_MAX_CAPACITY);
  }

  public LruCache(int maxCapacity) {
    super(16, DEFAULT_LOAD_FACTOR, true);
    this.maxCapacity = maxCapacity;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > maxCapacity;
  }

  @Override
  public boolean containsKey(Object key) {
    READ_LOCK.lock();
    try {
      return super.containsKey(key);
    } finally {
      READ_LOCK.unlock();
    }
  }

  @Override
  public V get(Object key) {
    READ_LOCK.lock();
    try {
      return super.get(key);
    } finally {
      READ_LOCK.unlock();
    }
  }

  @Override
  public V put(K key, V value) {
    WRITE_LOCK.lock();
    try {
      return super.put(key, value);
    } finally {
      WRITE_LOCK.unlock();
    }
  }

  @Override
  public V remove(Object key) {
    WRITE_LOCK.lock();
    try {
      return super.remove(key);
    } finally {
      WRITE_LOCK.unlock();
    }
  }

  @Override
  public int size() {
    READ_LOCK.lock();
    try {
      return super.size();
    } finally {
      READ_LOCK.unlock();
    }
  }

  @Override
  public void clear() {
    WRITE_LOCK.lock();
    try {
      super.clear();
    } finally {
      WRITE_LOCK.unlock();
    }
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

}
