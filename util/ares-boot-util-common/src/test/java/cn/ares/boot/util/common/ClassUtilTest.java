package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2022-12-05 20:26:11
 * @description: ClassUtil test
 * @version: JDK 1.8
 */
public class ClassUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(ClassUtilTest.class);

  public static void main(String[] args) {
    int i = 0;
    // 这里出来是Integer.class, 因为是数组所以隐式地带上了一个类型装箱
    JdkLoggerUtil.info(LOGGER, Arrays.toString(ClassUtil.toClass(i)));

    JdkLoggerUtil.info(LOGGER, "description: " + ClassUtil.constructParameterTypeSignature(null));
    JdkLoggerUtil.info(LOGGER, "description: " + ClassUtil.constructParameterTypeSignature(new Class[]{}));
    JdkLoggerUtil.info(LOGGER, "description: " + ClassUtil.constructParameterTypeSignature(new Class[]{String.class}));
    JdkLoggerUtil.info(LOGGER, "description: " + ClassUtil.constructParameterTypeSignature(new Class[]{String.class, List.class}));
    JdkLoggerUtil.info(LOGGER, "description: " + ClassUtil.constructParameterTypeSignature(new Class[]{int.class}));
    JdkLoggerUtil.info(LOGGER, "description: " + ClassUtil.constructParameterTypeSignature(new Class[]{int.class, List.class}));
  }

}
