package cn.ares.boot.util.common;

/**
 * @author: Ares
 * @time: 2023-06-19 14:07:11
 * @description: SnowFlakeIdUtil test
 * @version: JDK 1.8
 */
public class SnowFlakeIdUtilTest {

  public static void main(String[] args) {
    System.out.println("generate id: " + SnowFlakeIdUtil.nextIdByCacheWhenClockMoved());
  }

}
