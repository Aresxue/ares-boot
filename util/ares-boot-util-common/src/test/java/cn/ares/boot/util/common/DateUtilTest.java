package cn.ares.boot.util.common;

/**
 * @author: Ares
 * @time: 2022-12-19 16:12:30
 * @description: DateUtil test
 * @version: JDK 1.8
 */
public class DateUtilTest {

  public static void main(String[] args) {
    System.out.println(DateUtil.timestampToLocalDateTime(System.currentTimeMillis()));
    System.out.println(DateUtil.timestampToLocalDateTimeWithMilli(System.currentTimeMillis()));
  }

}