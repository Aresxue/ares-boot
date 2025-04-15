package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2025-04-14 21:51:16
 * @description: ArrayUtil test
 * @version: JDK 1.8
 */
public class ArrayUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(ArrayUtilTest.class);

  public static void main(String[] args) {
    Object[] objArray = new Object[]{1, 2, 3};

    JdkLoggerUtil.info(LOGGER, ArrayUtil.isOddLength(objArray));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.isEmpty(objArray));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.isNotEmpty(objArray));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.anyIsNull(objArray));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.anyIsNotNull(objArray));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.allIsNull(objArray));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.allIsNotNull(objArray));

    String[] strArr = new String[]{"ares", "kele"};
    JdkLoggerUtil.info(LOGGER, ArrayUtil.isOddLength(strArr));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.isEmpty(strArr));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.isNotEmpty(strArr));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.anyIsNull(strArr));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.anyIsNotNull(strArr));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.allIsNull(strArr));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.allIsNotNull(strArr));

    byte[] byteArr = new byte[]{1, 2, 3};
    JdkLoggerUtil.info(LOGGER, ArrayUtil.isEmpty(byteArr));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.isNotEmpty(byteArr));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.allIsNotEmpty(byteArr));
    JdkLoggerUtil.info(LOGGER, ArrayUtil.anyIsNotEmpty(byteArr));
  }

}
