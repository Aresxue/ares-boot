package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.Arrays;
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
  }

}
