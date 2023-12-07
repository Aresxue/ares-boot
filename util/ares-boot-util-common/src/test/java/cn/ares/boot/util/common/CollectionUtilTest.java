package cn.ares.boot.util.common;

import java.util.List;
import java.util.Set;

/**
 * @author: Ares
 * @time: 2023-12-07 11:57:29
 * @description: CollectionUtil test
 * @version: JDK 1.8
 */
public class CollectionUtilTest {

  public static void main(String[] args) {
    List<Integer> list = CollectionUtil.asList(1, 2);
    System.out.println(list);
    System.out.println(CollectionUtil.asList(list, 2, 3));
    System.out.println(CollectionUtil.asList(null, 2, 3));

    Set<Integer> set = CollectionUtil.asSet(1, 2);
    System.out.println(set);
    System.out.println(CollectionUtil.asHashSet(set, 2, 3));
    System.out.println(CollectionUtil.asHashSet(null, 2, 3));

    System.out.println(CollectionUtil.asSet(set, 2, 3));
    System.out.println(CollectionUtil.asSet(null, 2, 3));
  }

}
