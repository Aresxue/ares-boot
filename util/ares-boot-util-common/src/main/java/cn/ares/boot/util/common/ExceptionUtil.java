package cn.ares.boot.util.common;

import static cn.ares.boot.util.common.constant.StringConstant.JAVA;
import static cn.ares.boot.util.common.constant.StringConstant.SUN;

import cn.ares.boot.util.common.function.RunnableWithException;
import cn.ares.boot.util.common.throwable.CheckedExceptionWrapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * @author: Ares
 * @time: 2021-11-09 10:53:00
 * @description: Exception util
 * @version: JDK 1.8
 */
public class ExceptionUtil {

  /**
   * jdk包名不可重复集合
   */
  public static final Set<String> JDK_PACKAGE_NAME_SET = CollectionUtil.asSet(JAVA, SUN);

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
    } else if (ClassUtil.isSameClass(RuntimeException.class, throwable.getClass())
        || throwable instanceof CheckedExceptionWrapper) {
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
        throw new CheckedExceptionWrapper(e);
      }
    }
  }

  /**
   * @author: Ares
   * @description: 执行操作发生异常时捕获(抛出RuntimeException)
   * @description: Catch when an exception occurs during an execution (Throws a RuntimeException)
   * @time: 2022-06-08 13:56:54
   * @params: [runnable] 无返回的操作
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
        } catch (Throwable ex) {
          throw new CheckedExceptionWrapper(e);
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
   */
  public static void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new RuntimeException(message);
    }
  }

  /**
   * @author: Ares
   * @description: 迭代异常并做指定处理（处理抑制异常）
   * @description: Iterate over the exception and do the assignment processing(handle suppressedExceptions)）
   * @time: 2023-12-07 14:46:08
   * @params: [throwable, consumer] 待迭代异常，异常处理
   */
  public static void iterableThrowable(Throwable throwable, Consumer<Throwable> consumer) {
    iterableThrowable(throwable, consumer, true);
  }

  /**
   * @author: Ares
   * @description: 迭代异常并做指定处理（可指定是否处理抑制异常）
   * @description: Iterate over the exception and do the assignment processing(Can specify whether to handle suppressedExceptions)
   * @time: 2023-12-07 14:46:08
   * @params: [throwable, consumer, handleSuppressed] 待迭代异常，异常处理, 是否处理抑制异常
   */
  public static void iterableThrowable(Throwable throwable, Consumer<Throwable> consumer, boolean handleSuppressed) {
    if (null == throwable) {
      return;
    }
    // 循环引用检测Set
    // Loop reference detection Set
    Set<Throwable> circularReferenceDetectSet = Collections.newSetFromMap(new IdentityHashMap<>());
    circularReferenceDetectSet.add(throwable);

    iterableThrowable(throwable, consumer, circularReferenceDetectSet, false, handleSuppressed);
  }

  private static void iterableThrowable(Throwable throwable, Consumer<Throwable> consumer,
      Set<Throwable> circularReferenceDetectSet, boolean detect, boolean handleSuppressed) {
    if (detect && circularReferenceDetectSet.contains(throwable)) {
      return;
    }
    circularReferenceDetectSet.add(throwable);

    consumer.accept(throwable);

    if (handleSuppressed) {
      Throwable[] suppressedExceptions = throwable.getSuppressed();
      for (Throwable suppressed : suppressedExceptions) {
        iterableThrowable(suppressed, consumer, circularReferenceDetectSet, true, handleSuppressed);
      }
    }

    Throwable cause = throwable.getCause();
    if (null != cause) {
      iterableThrowable(cause, consumer, circularReferenceDetectSet, true, handleSuppressed);
    }
  }

  /**
   * @author: Ares
   * @description: 为异常生成身份标识（还原字节码改写导致的类名和方法名变更）
   * @description: Generate identifiers for exceptions (undo class and method name changes caused by bytecode rewriting)
   * @time: 2023-11-30 15:47:52
   * @params: [throwable] 异常
   * @return: java.lang.String 异常标识
   */
  public static String identity(Throwable throwable) {
    if (null == throwable) {
      return null;
    }
    StringBuilder stringBuilder = new StringBuilder();

    iterableThrowable(throwable, tempThrowable -> {
      stringBuilder.append(tempThrowable);
      StackTraceElement[] stackTraceElements = tempThrowable.getStackTrace();
      if (ArrayUtil.isNotEmpty(stackTraceElements)) {
        for (StackTraceElement stackTraceElement : stackTraceElements) {
          if (isConcern(stackTraceElement)) {
            stringBuilder.append(ClassUtil.getOriginClassName(stackTraceElement.getClassName()));
            stringBuilder.append(ClassUtil.getOriginMethodName(stackTraceElement.getMethodName()));
            stringBuilder.append(stackTraceElement.getFileName());
            stringBuilder.append(stackTraceElement.getLineNumber());
          }
        }
      }
    });

    return stringBuilder.toString();
  }

  /**
   * @author: Ares
   * @description: 获取最深层次的异常原因
   * @description: Get the deepest cause
   * @time: 2024-04-11 14:12:45
   * @params: [throwable] 可抛出的异常
   * @return: java.lang.Throwable 最深层次的异常原因
   */
  public static Throwable getDeepestCause(Throwable throwable) {
    if (null == throwable) {
      return null;
    }
    while (null != throwable.getCause()) {
      throwable = throwable.getCause();
    }
    return throwable;
  }

  private static boolean isConcern(StackTraceElement stackTrace) {
    // 排除jdk的类和sun包下的类（值得一提的是由于反射会被优化会导致堆栈变化成sun.reflect.GeneratedMethodAccessor）
    return JDK_PACKAGE_NAME_SET.stream()
        .noneMatch(packageName -> stackTrace.getClassName().startsWith(packageName));
  }

  private static <E extends RuntimeException> void validWrapperException(Class<E> wrapperException,
      Exception e) {
    if (null == wrapperException) {
      throw new CheckedExceptionWrapper(e);
    }
  }

}
