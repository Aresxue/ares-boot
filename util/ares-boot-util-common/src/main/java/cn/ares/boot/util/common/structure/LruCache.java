package cn.ares.boot.util.common.structure;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
  private final Lock lock = new ReentrantLock();
  private volatile int maxCapacity;

  public LruCache() {
    this(DEFAULT_MAX_CAPACITY);
  }

  public LruCache(int maxCapacity) {
    super(16, DEFAULT_LOAD_FACTOR, true);
    this.maxCapacity = maxCapacity;
  }

  @Override
  protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
    return size() > maxCapacity;
  }

  @Override
  public boolean containsKey(Object key) {
    lock.lock();
    try {
      return super.containsKey(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public V get(Object key) {
    lock.lock();
    try {
      return super.get(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public V put(K key, V value) {
    lock.lock();
    try {
      return super.put(key, value);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public V remove(Object key) {
    lock.lock();
    try {
      return super.remove(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int size() {
    lock.lock();
    try {
      return super.size();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void clear() {
    lock.lock();
    try {
      super.clear();
    } finally {
      lock.unlock();
    }
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

  public void setMaxCapacity(int maxCapacity) {
    this.maxCapacity = maxCapacity;
  }

}
