package cn.ares.boot.util.common;

import cn.ares.boot.util.common.function.RunnableWithException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.Callable;

/**
 * @author: Ares
 * @time: 2021-11-09 10:53:00
 * @description: Exception util
 * @version: JDK 1.8
 */
public class ExceptionUtil {

  /**
   * @author: Ares
   * @description: Get origin throwable
   * @time: 2021-11-09 10:54：00
   * @params: [throwable]
   * @return: java.lang.Throwable origin Throwable
   */
  public static Throwable getOriginThrowable(Throwable throwable) {
    Throwable originalThrowable = throwable;
    if (throwable instanceof InvocationTargetException
        || throwable instanceof UndeclaredThrowableException) {
      originalThrowable = getOriginThrowable(throwable.getCause());
    }
    return originalThrowable;
  }

  /**
   * @author: Ares
   * @description: Get origin Throwable
   * @time: 2022-06-08 13:53:24
   * @params: [runtimeException] 运行时异常
   * @return: java.lang.Throwable
   */
  public static Throwable getOriginException(RuntimeException runtimeException) {
    Throwable throwable = runtimeException.getCause();
    if (null == throwable) {
      return runtimeException;
    } else if (throwable instanceof RuntimeException) {
      return getOriginException((RuntimeException) throwable);
    } else {
      return throwable;
    }
  }

  /**
   * @author: Ares
   * @description: throwable转为字符串
   * @description: Throwable to string
   * @time: 2022-06-08 13:54:34
   * @params: [throwable]
   * @return: java.lang.String 转换后字符串
   */
  public static String toString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }

  /**
   * @author: Ares
   * @description: throwable转为字符串
   * @description: Throwable to string
   * @time: 2022-06-08 13:55:31
   * @params: [throwable, retainLength] throwable, 保留长度
   * @return: java.lang.String 转换后字符串
   */
  public static String toString(Throwable throwable, int retainLength) {
    if (retainLength <= 0) {
      throw new RuntimeException("Length don't allow less than zero");
    }
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString().substring(0, retainLength);
  }

  /**
   * @author: Ares
   * @description: 执行操作发生异常时捕获(抛出RuntimeException)
   * @description: Catch when an exception occurs during an execution (Throws a RuntimeException)
   * @time: 2022-06-08 13:56:54
   * @params: [callable] 带返回的操作
   * @return: T
   */
  public static <T> T get(Callable<T> callable) {
    return get(callable, false);
  }

  /**
   * @author: Ares
   * @description: 执行操作发生异常时捕获(可指定是否忽略异常)
   * @description: Catch when an exception occurs during an execution (you can specify whether to
   * ignore the exception)
   * @time: 2022-06-08 13:56:54
   * @params: [callable, ignore] 带返回的操作，忽略异常
   * @return: T
   */
  public static <T> T get(Callable<T> callable, boolean ignore) {
    return get(callable, ignore, null);
  }

  /**
   * @author: Ares
   * @description: 执行操作发生异常时捕获(抛出指定异常不指定抛出RuntimeException)
   * @description: Catch when an exception occurs during execution (throwing specified exception
   * does not specify throwing RuntimeException)
   * @time: 2022-06-08 13:56:54
   * @params: [callable, wrapperException] 带返回的操作，包装异常
   * @return: T
   */
  public static <T, E extends RuntimeException> T get(Callable<T> callable,
      Class<E> wrapperException) {
    return get(callable, false, wrapperException);
  }

  /**
   * @author: Ares
   * @description: 执行操作发生异常时捕获(可忽略和抛出)
   * @description: Catch exceptions when performing operations (can be ignored and thrown)
   * @time: 2022-06-08 13:56:54
   * @params: [callback, ignore, wrapperException] 带返回的操作，忽略异常，包装异常
   * @return: T
   */
  public static <T, E extends RuntimeException> T get(Callable<T> callable, boolean ignore,
      Class<E> wrapperException) {
    try {
      return callable.call();
    } catch (Exception e) {
      if (ignore) {
        return null;
      }
      validWrapperException(wrapperException, e);
      try {
        RuntimeException runtimeException = wrapperException.newInstance();
        runtimeException.initCause(e);
        throw runtimeException;
      } catch (Exception ex) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * @author: Ares
   * @description: 执行操作发生异常时捕获(抛出RuntimeException)
   * @description: Catch when an exception occurs during an execution (Throws a RuntimeException)
   * @time: 2022-06-08 13:56:54
   * @params: [runnable] 无返回的操作
   * @return: void
   */
  public static void run(RunnableWithException runnable) {
    run(runnable, false);
  }

  /**
   * @author: Ares
   * @description: 执行操作发生异常时捕获(可指定是否忽略异常)
   * @description: Catch when an exception occurs during an execution (you can specify whether to
   * ignore the exception)
   * @time: 2022-06-08 13:56:54
   * @params: [runnable, ignore] 无返回的操作，忽略异常
   * @return: void
   */
  public static void run(RunnableWithException runnable, boolean ignore) {
    run(runnable, ignore, null);
  }

  /**
   * @author: Ares
   * @description: 执行操作发生异常时捕获(抛出指定异常不指定抛出RuntimeException)
   * @description: Catch when an exception occurs during execution (throwing specified exception
   * does not specify throwing RuntimeException)
   * @time: 2022-06-08 13:56:54
   * @params: [runnable, wrapperException] 无返回的操作，包装异常
   * @return: void
   */
  public static <E extends RuntimeException> void run(RunnableWithException runnable,
      Class<E> wrapperException) {
    run(runnable, false, wrapperException);
  }

  /**
   * @author: Ares
   * @description: 执行操作发生异常时捕获(可忽略和抛出)
   * @description: Catch exceptions when performing operations (can be ignored and thrown)
   * @time: 2022-06-08 13:56:54
   * @params: [runnable, ignore, wrapperException] 无返回的操作，忽略异常，包装异常
   * @return: void
   */
  public static <E extends RuntimeException> void run(RunnableWithException runnable,
      boolean ignore, Class<E> wrapperException) {
    try {
      runnable.run();
    } catch (Exception e) {
      if (!ignore) {
        validWrapperException(wrapperException, e);
        try {
          RuntimeException runtimeException = wrapperException.newInstance();
          runtimeException.initCause(e);
          throw runtimeException;
        } catch (Exception ex) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  /**
   * @author: Ares
   * @description: 不满足条件时抛出异常
   * @description: Throw an exception when the condition is not met
   * @time: 2022-06-08 13:59:49
   * @params: [condition, message] 条件，信息
   * @return: void
   */
  public static void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new RuntimeException(message);
    }
  }

  private static <E extends RuntimeException> void validWrapperException(Class<E> wrapperException,
      Exception e) {
    if (null == wrapperException) {
      throw new RuntimeException(e);
    }
  }

}
