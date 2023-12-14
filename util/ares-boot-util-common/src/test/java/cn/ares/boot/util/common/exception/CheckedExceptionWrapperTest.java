package cn.ares.boot.util.common.exception;

import cn.ares.boot.util.common.ClassUtil;

/**
 * @author: Ares
 * @time: 2023-12-14 13:56:31
 * @description: WrapRuntimeException test
 * @version: JDK 1.8
 */
public class CheckedExceptionWrapperTest {

  public static void main(String[] args) {
    CheckedExceptionWrapper checkedExceptionWrapper = new CheckedExceptionWrapper();
    System.out.println(checkedExceptionWrapper instanceof RuntimeException);
    System.out.println(RuntimeException.class.isInstance(checkedExceptionWrapper));
    System.out.println(ClassUtil.isSameClass(RuntimeException.class, checkedExceptionWrapper.getClass()));
  }

}
