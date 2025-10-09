package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2023-11-29 11:44:21
 * @description: StringUtil test
 * @version: JDK 1.8
 */
public class StringUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(StringUtilTest.class);

  public static void main(String[] args) {
    LOGGER.info(StringUtil.getCommonPrefix(null));
    LOGGER.info(StringUtil.getCommonPrefix("ares", "ar"));
    LOGGER.info(StringUtil.getCommonPrefix("cn/ares", "cn/ares/boot"));
    LOGGER.info(StringUtil.join("/", Arrays.asList("cn.ares.business.Service", "method")));
    LOGGER.info("not alphabet and number result: " + StringUtil.notAlphabetAndNumber("ares"));
    LOGGER.info("not alphabet and number result: " + StringUtil.notAlphabetAndNumber("ares-520"));

    String className = StringUtil.class.getName();
    String[] splitArr = className.split("\\.");
    LOGGER.info("split array result: " + Arrays.toString(splitArr));
    List<String> splitList = StringUtil.listSplit(className, ".");
    LOGGER.info("split list result: " + splitList);

    long start = System.currentTimeMillis();
    for (int i = 0; i < 1_000_000; i++) {
      String[] split = className.split("\\.");
    }
    LOGGER.info("list split time: " + (System.currentTimeMillis() - start));
    start = System.currentTimeMillis();
    for (int i = 0; i < 1_000_000; i++) {
      List<String> split = StringUtil.listSplit(className, ".");
    }
    LOGGER.info("list split time: " + (System.currentTimeMillis() - start));
  }

}
