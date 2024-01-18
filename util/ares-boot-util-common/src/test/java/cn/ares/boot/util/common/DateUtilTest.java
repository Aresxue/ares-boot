package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2022-12-19 16:12:30
 * @description: DateUtil test
 * @version: JDK 1.8
 */
public class DateUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(DateUtilTest.class);

  public static void main(String[] args) {
    JdkLoggerUtil.info(LOGGER, DateUtil.timestampToLocalDateTime(System.currentTimeMillis()));
    JdkLoggerUtil.info(LOGGER, DateUtil.timestampToLocalDateTimeWithMilli(System.currentTimeMillis()));
  }

}