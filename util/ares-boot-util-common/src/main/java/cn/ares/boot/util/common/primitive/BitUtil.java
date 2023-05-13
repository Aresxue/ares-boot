package cn.ares.boot.util.common.primitive;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Ares
 * @time: 2020-12-07 17:34:00
 * @description: 位操作工具类
 * @description: Bit util
 * @version: JDK 1.8
 */
public class BitUtil {

  /**
   * @author: Ares
   * @description: 将指定bit位设置为0
   * @description: Set the specified bit to 0
   * @time: 2020-09-05 17:15:00
   * @params: [flagGroup, index] flag组, 下标
   * @return: long 修改后的long
   */
  public static long setFalse(long flagGroup, int index) {
    long mask = ~(1L << index);
    return (flagGroup & (mask));
  }

  /**
   * @author: Ares
   * @description: 将指定bit位设置为1
   * @description: Set the specified bit to 1
   * @time: 2020-09-05 17:15:00
   * @params: [flagGroup, index] flag组, 下标
   * @return: long 修改后的long
   */
  public static long setTrue(long flagGroup, int index) {
    return (flagGroup | (1L << index));
  }

  /**
   * @author: Ares
   * @description: 获取指定位（1为true，0为false）
   * @description: Get the specified bit(1 is true, 0 is false)
   * @time: 2020-09-05 17:17:00
   * @params: [flagGroup, index] flag组, 下标
   * @return: boolean 结果
   */
  public static boolean getFlag(long flagGroup, int index) {
    return ((flagGroup & (1L << index)) != 0);
  }

  /**
   * @author: Ares
   * @description: 将指定bit位设置为0
   * @description: Set the specified bit to 0
   * @time: 2020-09-05 17:15:00
   * @params: [flagGroup, index] flag组, 下标
   * @return: long 修改后的long
   */
  public static long setFalse(long flagGroup, byte index) {
    return setFalse(flagGroup, Integer.valueOf(index));
  }

  /**
   * @author: Ares
   * @description: 将指定bit位设置为1
   * @description: Set the specified bit to 1
   * @time: 2020-09-05 17:15:00
   * @params: [flagGroup, index] flag组, 下标
   * @return: long 修改后的long
   */
  public static long setTrue(long flagGroup, byte index) {
    return setTrue(flagGroup, Integer.valueOf(index));
  }

  /**
   * @author: Ares
   * @description: 获取指定位（1为true，0为false）
   * @description: Get the specified bit(1 is true, 0 is false)
   * @time: 2020-09-05 17:17:00
   * @params: [flagGroup, index] flag组, 下标
   * @return: boolean 结果
   */
  public static boolean getFlag(long flagGroup, byte index) {
    return getFlag(flagGroup, Integer.valueOf(index));
  }

  /**
   * @author: Ares
   * @description: 获取所有为true的下标Set
   * @description: Get all set of subscripts that are true
   * @time: 2022-06-07 17:34:14
   * @params: [flagGroup] flag组
   * @return: java.util.Set<java.lang.Byte>
   */
  public static Set<Byte> getAllTrueIndex(long flagGroup) {
    Set<Byte> indexSet = new HashSet<>();
    for (byte index = 0; index < 64; index++) {
      if (getFlag(flagGroup, index)) {
        indexSet.add(index);
      }
    }
    return indexSet;
  }


  /**
   * @author: Ares
   * @description: 获取所有为false的下标Set
   * @description: Get all set of subscripts that are false
   * @time: 2022-06-07 17:34:14
   * @params: [flagGroup] flag组
   * @return: java.util.Set<java.lang.Byte>
   */
  public static Set<Byte> getAllFalseIndex(long flagGroup) {
    Set<Byte> indexSet = new HashSet<>();
    for (byte index = 0; index < 64; index++) {
      if (!getFlag(flagGroup, index)) {
        indexSet.add(index);
      }
    }
    return indexSet;
  }

}
