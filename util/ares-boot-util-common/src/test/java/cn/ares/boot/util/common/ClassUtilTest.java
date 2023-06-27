package cn.ares.boot.util.common;

import java.util.Arrays;
import java.util.List;

/**
 * @author: Ares
 * @time: 2022-12-05 20:26:11
 * @description: ClassUtil test
 * @version: JDK 1.8
 */
public class ClassUtilTest {

  public static void main(String[] args) {
    int i = 0;
    // 这里出来是Integer.class, 因为是数组所以隐式地带上了一个类型装箱
    System.out.println(Arrays.toString(ClassUtil.toClass(i)));

    System.out.println("dDescription: " + ClassUtil.buildDescription(null));
    System.out.println("dDescription: " + ClassUtil.buildDescription(new Class[]{}));
    System.out.println("dDescription: " + ClassUtil.buildDescription(new Class[]{String.class}));
    System.out.println("dDescription: " + ClassUtil.buildDescription(new Class[]{String.class, List.class}));
  }

}
