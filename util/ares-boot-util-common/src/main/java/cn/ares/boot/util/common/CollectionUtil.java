package cn.ares.boot.util.common;

import cn.ares.boot.util.common.primitive.IntegerUtil;
import cn.ares.boot.util.common.structure.ConcurrentHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * @author: Ares
 * @time: 2021-10-25 18:59:00
 * @description: Collection util
 * @version: JDK 1.8
 */
public class CollectionUtil {

  /**
   * @author: Ares
   * @description: 集合是否为空
   * @description: Collection is empty
   * @time: 2022-06-07 17:06:43
   * @params: [collection] 集合
   * @return: boolean 是否为空
   */
  public static <T> boolean isEmpty(Collection<T> collection) {
    return (collection == null || collection.isEmpty());
  }

  /**
   * @author: Ares
   * @description: 集合是否非空
   * @description: Collection is not empty
   * @time: 2022-06-07 17:06:43
   * @params: [collection] 集合
   * @return: boolean 是否非空
   */
  public static <T> boolean isNotEmpty(Collection<T> collection) {
    return !isEmpty(collection);
  }

  /**
   * @author: Ares
   * @description: 以期望的元素个数创建HashSet
   * @description: New HashSet with expected size
   * @time: 2022-06-07 17:09:07
   * @params: [expectedSize] 期望的元素个数
   * @return: java.util.HashSet<E> 不可重复集合
   */
  public static <E> HashSet<E> newHashSet(int expectedSize) {
    return new HashSet<>(MapUtil.capacity(expectedSize));
  }

  /**
   * @author: Ares
   * @description: 以期望的元素个数创建并发的不可重复集合
   * @description: New concurrent HashSet with expected size
   * @time: 2022-06-07 17:09:07
   * @params: [expectedSize] 期望的元素个数
   * @return: java.util.HashSet<E> 并发的不可重复集合
   */
  public static <E> ConcurrentHashSet<E> newConcurrentHashSet(int expectedSize) {
    return new ConcurrentHashSet<>(MapUtil.capacity(expectedSize));
  }

  /**
   * @author: Ares
   * @description: 以默认的元素个数创建并发的不可重复集合
   * @description: New concurrent HashSet with default size
   * @time: 2022-06-07 17:09:07
   * @params: [expectedSize] 期望的元素个数
   * @return: java.util.HashSet<E> 并发的不可重复集合
   */
  public static <E> ConcurrentHashSet<E> newConcurrentHashSet() {
    return new ConcurrentHashSet<>(16);
  }

  /**
   * @author: Ares
   * @description: 以默认的元素个数创建不可重复集合
   * @description: New HashSet with default size
   * @time: 2022-06-07 17:09:35
   * @params: []
   * @return: java.util.HashSet<E>
   */
  public static <E> HashSet<E> newHashSet() {
    return newHashSet(16);
  }

  /**
   * @author: Ares
   * @description: 集合是否包含元素
   * @description: Collection contains element
   * @time: 2022-06-07 17:09:53
   * @params: [collection, element] 集合，元素
   * @return: boolean 是否包含
   */
  public static <T> boolean containsInstance(Collection<T> collection, Object element) {
    if (collection != null) {
      for (Object candidate : collection) {
        if (candidate == element) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @author: Ares
   * @description: 比较两个集合是否一致
   * @description: Compare two collection for consistency
   * @time: 2021-12-18 16:46:00
   * @params: [source, target] 来源集合，目标集合
   * @return: boolean 是否一致
   */
  public static <T> boolean equals(Collection<T> source, Collection<T> target) {
    return equals(source, target, Collection::hashCode);
  }

  /**
   * @author: Ares
   * @description: 比较两个集合使用指定的hashcode计算方式
   * @description: Compare two collection use way of compute hashCode
   * @time: 2021-12-18 16:46:00
   * @params: [source, target, callback] 来源集合，目标集合，hashCode计算方式
   * @return: boolean 是否一致
   */
  public static <T> boolean equals(Collection<T> source, Collection<T> target,
      Function<Collection<T>, Integer> function) {
    if (isEmpty(source) && isNotEmpty(target)) {
      return false;
    }
    if (isNotEmpty(source) && isEmpty(target)) {
      return false;
    }
    if (null == source && null == target) {
      return true;
    }
    if (source.size() != target.size()) {
      return false;
    }

    return function.apply(source).equals(function.apply(target));
  }

  /**
   * @author: Ares
   * @description: 来源集合包含候选集合的任意元素
   * @description: The source set contains any element of the candidate set
   * @time: 2022-06-07 17:13:35
   * @params: [source, candidates] 来源集合，候选集合
   * @return: boolean 是否包含
   */
  public static <T> boolean containsAny(Collection<T> source, Collection<T> candidates) {
    return findFirstMatch(source, candidates) != null;
  }

  /**
   * @author: Ares
   * @description: 从列表中随机选取元素
   * @description: Get random element from list
   * @time: 2022-06-07 17:17:34
   * @params: [list] 列表
   * @return: T 随机元素
   */
  public static <T> T random(List<T> list) {
    if (CollectionUtil.isEmpty(list)) {
      return null;
    }
    int index = ThreadLocalRandom.current().nextInt(list.size());
    return list.get(index);
  }

  /**
   * @author: Ares
   * @description: 将老的集合和元素数组组合成新的链表
   * @description: Combines an old collection and an array of elements into a new collection
   * @time: 2023-05-08 11:43:37
   * @params: [oldList, elements] 老的集合，元素数组
   * @return: java.util.List<E> 新的集合
   */
  public static <E> List<E> newList(List<E> oldList, E... elements) {
    if (ArrayUtil.isNotEmpty(elements)) {
      Collections.addAll(oldList, elements);
    }
    return oldList;
  }

  /**
   * @author: Ares
   * @description: 元素数组转为数组链表
   * @description: Convert element array to ArrayList
   * @time: 2022-06-07 17:18:38
   * @params: [elements] 元素数组
   * @return: java.util.ArrayList<E>
   */
  @SafeVarargs
  public static <E> ArrayList<E> newArrayList(E... elements) {
    if (null == elements) {
      throw new NullPointerException();
    }
    int arraySize = elements.length;
    ArrayList<E> list = new ArrayList<>(suitableCapacity(arraySize));
    Collections.addAll(list, elements);
    return list;
  }

  /**
   * @author: Ares
   * @description: 元素数组转为数组链表
   * @description: Convert element array to ArrayList
   * @time: 2022-06-07 17:18:38
   * @params: [elements] 元素数组
   * @return: java.util.ArrayList<E>
   */
  public static <E> ArrayList<E> newListArray(E[] elements) {
    if (null == elements) {
      throw new NullPointerException();
    }
    return newArrayList(elements);
  }

  /**
   * @author: Ares
   * @description: 根据数组大小获取合适的数组链表大小
   * @description: Get suitable capacity for ArrayList by array size
   * @time: 2022-06-07 17:19:47
   * @params: [arraySize] in 入参
   * @return: int out 出参
   */
  public static int suitableCapacity(int arraySize) {
    return IntegerUtil.saturatedCast(5 + arraySize + arraySize / 10);
  }


  /**
   * @author: Ares
   * @description: 寻找两个集合的第一个匹配元素
   * @description: Find the first matching element of two collection
   * @time: 2022-06-07 17:14:17
   * @params: [source, candidates] 来源集合，候选集合
   * @return: E 元素
   */
  public static <SOURCE, E> E findFirstMatch(Collection<SOURCE> source, Collection<E> candidates) {
    if (isEmpty(source) || isEmpty(candidates)) {
      return null;
    }
    for (Object candidate : candidates) {
      if (source.contains(candidate)) {
        return (E) candidate;
      }
    }
    return null;
  }

  /**
   * @author: Ares
   * @description: 寻找指定类型的元素
   * @description: Find value from collection by type
   * @time: 2022-06-07 17:21:26
   * @params: [collection, type] 集合，类型
   * @return: T 元素
   */
  public static <T> T findValueOfType(Collection<T> collection, Class<T> type) {
    if (isEmpty(collection)) {
      return null;
    }
    T value = null;
    for (Object element : collection) {
      if (type == null || type.isInstance(element)) {
        if (value != null) {
          // More than one value found... no clear single value.
          return null;
        }
        value = (T) element;
      }
    }
    return value;
  }

  /**
   * @author: Ares
   * @description: 只包含唯一元素
   * @description: Only contains one element
   * @time: 2022-06-07 17:22:27
   * @params: [collection] 集合
   * @return: boolean 是否
   */
  public static <T> boolean hasUniqueObject(Collection<T> collection) {
    if (isEmpty(collection)) {
      return false;
    }
    boolean hasCandidate = false;
    Object candidate = null;
    for (Object elem : collection) {
      if (!hasCandidate) {
        hasCandidate = true;
        candidate = elem;
      } else if (candidate != elem) {
        return false;
      }
    }
    return true;
  }

  /**
   * @author: Ares
   * @description: 寻找通用元素类型
   * @description: Find common element type
   * @time: 2022-06-07 17:24:56
   * @params: [collection] 集合
   * @return: java.lang.Class<?> 类型
   */
  public static <T> Class<?> findCommonElementType(Collection<T> collection) {
    if (isEmpty(collection)) {
      return null;
    }
    Class<?> candidate = null;
    for (Object val : collection) {
      if (val != null) {
        if (candidate == null) {
          candidate = val.getClass();
        } else if (candidate != val.getClass()) {
          return null;
        }
      }
    }
    return candidate;
  }

  /**
   * @author: Ares
   * @description: 获取set第一个元素
   * @description: Get first element for set
   * @time: 2022-06-07 17:25:44
   * @params: [set]
   * @return: T 元素
   */
  public static <T> T firstElement(Set<T> set) {
    if (isEmpty(set)) {
      return null;
    }
    if (set instanceof SortedSet) {
      return ((SortedSet<T>) set).first();
    }

    Iterator<T> it = set.iterator();
    T first = null;
    if (it.hasNext()) {
      first = it.next();
    }
    return first;
  }

  /**
   * @author: Ares
   * @description: 获取列表第一个元素
   * @description: Get first element for list
   * @time: 2022-06-07 17:25:44
   * @params: [list] 列表
   * @return: T 元素
   */
  public static <T> T firstElement(List<T> list) {
    if (isEmpty(list)) {
      return null;
    }
    return list.get(0);
  }

  /**
   * @author: Ares
   * @description: 获取set最后一个元素
   * @description: Get last element for set
   * @time: 2022-06-07 17:25:44
   * @params: [set]
   * @return: T 元素
   */
  public static <T> T lastElement(Set<T> set) {
    if (isEmpty(set)) {
      return null;
    }
    if (set instanceof SortedSet) {
      return ((SortedSet<T>) set).last();
    }

    // Full iteration necessary...
    Iterator<T> it = set.iterator();
    T last = null;
    while (it.hasNext()) {
      last = it.next();
    }
    return last;
  }

  /**
   * @author: Ares
   * @description: 获取列表最后一个元素
   * @description: Get last element for list
   * @time: 2022-06-07 17:25:44
   * @params: [list] 列表
   * @return: T 元素
   */
  public static <T> T lastElement(List<T> list) {
    if (isEmpty(list)) {
      return null;
    }
    return list.get(list.size() - 1);
  }

  /**
   * @author: Ares
   * @description: Enumeration to array
   * @time: 2022-06-07 17:27:26
   * @params: [enumeration, array]
   * @return: A[] 数组
   */
  public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
    ArrayList<A> elements = new ArrayList<>();
    while (enumeration.hasMoreElements()) {
      elements.add(enumeration.nextElement());
    }
    return elements.toArray(array);
  }

  /**
   * @author: Ares
   * @description: Enumeration to iterator
   * @time: 2022-06-07 17:28:12
   * @params: [enumeration]
   * @return: java.util.Iterator<E> 迭代器
   */
  public static <E> Iterator<E> toIterator(Enumeration<E> enumeration) {
    return (enumeration != null ? new EnumerationIterator<>(enumeration)
        : Collections.emptyIterator());
  }

  /**
   * @author: Ares
   * @description: 数组转为不可重复集合
   * @description: Convert array to set
   * @time: 2022-06-08 11:02:52
   * @params: [elements] 元素数组
   * @return: java.util.Set<E>
   */
  @SafeVarargs
  public static <E> Set<E> asSet(E... elements) {
    Set<E> set = CollectionUtil.newHashSet(elements.length);
    set.addAll(Arrays.asList(elements));
    return set;
  }

  /**
   * @author: Ares
   * @description: 数组转为有序的不可重复集合
   * @description: Convert array to linked set
   * @time: 2023-05-08 13:21:07
   * @params: [elements] 元素数组
   * @return: java.util.Set<E> out 出参
   */
  public static <E> LinkedHashSet<E> asLinkedHashSet(E... elements) {
    return new LinkedHashSet<>(Arrays.asList(elements));
  }

  /**
   * @author: Ares
   * @description: 将元素添加到不可重复集合中组合成新集合
   * @description: Compose a new collection by adding elements to a non-repeatable collection
   * @time: 2023-05-08 13:28:21
   * @params: [set, elements] 不可重复集合，元素数组
   * @return: java.util.Set<E> 新不可重复集合
   */
  public static <E> Set<E> asHashSet(Set<E> set, E... elements) {
    Set<E> newSet = new HashSet<>(set);
    if (ArrayUtil.isNotEmpty(elements)) {
      newSet.addAll(Arrays.asList(elements));
    }
    return newSet;
  }

  /**
   * @author: Ares
   * @description: 将元素添加到有序不可重复集合中组合成新集合
   * @description: Compose a new collection by adding elements to an ordered non-repeatable
   * collection
   * @time: 2023-05-08 13:29:07
   * @params: [set, elements] 不可重复集合，元素数组
   * @return: java.util.Set<E> 新不可重复集合
   */
  public static <E> LinkedHashSet<E> asLinkedHashSet(Set<E> set, E... elements) {
    LinkedHashSet<E> newSet = new LinkedHashSet<>(set);
    if (ArrayUtil.isNotEmpty(elements)) {
      Collections.addAll(newSet, elements);
    }
    return newSet;
  }

  @SafeVarargs
  public static <E> ConcurrentHashSet<E> asConcurrentHashSet(E... elements) {
    ConcurrentHashSet<E> set = CollectionUtil.newConcurrentHashSet(elements.length);
    set.addAll(Arrays.asList(elements));
    return set;
  }

  private static class EnumerationIterator<E> implements Iterator<E> {

    private final Enumeration<E> enumeration;

    public EnumerationIterator(Enumeration<E> enumeration) {
      this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
      return this.enumeration.hasMoreElements();
    }

    @Override
    public E next() {
      return this.enumeration.nextElement();
    }

    @Override
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Not supported");
    }
  }

}
