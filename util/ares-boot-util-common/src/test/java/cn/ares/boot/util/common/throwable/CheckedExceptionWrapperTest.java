package cn.ares.boot.util.common.throwable;

import cn.ares.boot.util.common.ClassUtil;
import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2023-12-14 13:56:31
 * @description: WrapRuntimeException test
 * @version: JDK 1.8
 */
public class CheckedExceptionWrapperTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(CheckedExceptionWrapperTest.class);

  public static void main(String[] args) {
    CheckedExceptionWrapper checkedExceptionWrapper = new CheckedExceptionWrapper();
    JdkLoggerUtil.info(LOGGER, checkedExceptionWrapper instanceof RuntimeException);
    JdkLoggerUtil.info(LOGGER, RuntimeException.class.isInstance(checkedExceptionWrapper));
    JdkLoggerUtil.info(LOGGER,
        ClassUtil.isSameClass(RuntimeException.class, checkedExceptionWrapper.getClass()));
  }

}
