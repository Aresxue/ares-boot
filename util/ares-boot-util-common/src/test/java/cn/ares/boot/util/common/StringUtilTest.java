package cn.ares.boot.util.common;

/**
 * @author: Ares
 * @time: 2023-11-29 11:44:21
 * @description: StringUtil test
 * @version: JDK 1.8
 */
public class StringUtilTest {

  public static void main(String[] args) {
    System.out.println(StringUtil.getCommonPrefix(null));
    System.out.println(StringUtil.getCommonPrefix("ares", "ar"));
    System.out.println(StringUtil.getCommonPrefix("cn/ares", "cn/ares/boot"));
  }

}
