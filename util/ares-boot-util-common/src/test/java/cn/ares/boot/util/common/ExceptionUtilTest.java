package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2024-10-24 19:18:19
 * @description: ExceptionUtil test
 * @version: JDK 1.8
 */
public class ExceptionUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(ExceptionUtilTest.class);

  public static void main(String[] args) {
    RuntimeException runtimeException = new RuntimeException("origin exception");
    UndeclaredThrowableException undeclaredThrowable = new UndeclaredThrowableException(
        runtimeException);
    RuntimeException runtimeException1 = new RuntimeException(undeclaredThrowable);
    RuntimeException runtimeException2 = new RuntimeException(runtimeException1);
    LOGGER.info(ExceptionUtil.toString(ExceptionUtil.getOriginThrowable(runtimeException2)));
  }

}
