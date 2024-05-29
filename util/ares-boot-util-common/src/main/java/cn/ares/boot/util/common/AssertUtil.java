package cn.ares.boot.util.common;

import cn.ares.boot.util.common.function.RunnableWithException;

/**
 * @author: Ares
 * @time: 2024-05-29 13:59:46
 * @description: 断言工具
 * @description: Assert util
 * @version: JDK 1.8
 */
public class AssertUtil {

  /**
   * @author: Ares
   * @description: 断言条件是否为真是则执行相应操作
   * @description: If the assertion condition is true, the corresponding action is performed
   * @time: 2024-05-29 14:02:16
   * @params: [condition, runnable] 断言条件，执行操作
   * @return: void
   */
  public static void assertTrue(boolean condition, Runnable runnable) {
    if (condition) {
      runnable.run();
    }
  }

  /**
   * @author: Ares
   * @description: 断言条件是否为真是则执行相应操作（抛出异常）
   * @description: If the assertion condition is true, the corresponding action is performed(throw exception)
   * @time: 2024-05-29 14:02:16
   * @params: [condition, runnable] 断言条件，执行操作
   * @return: void
   */
  public static void assertTrue(boolean condition, RunnableWithException runnable)
      throws Exception {
    if (condition) {
      runnable.run();
    }
  }

}
