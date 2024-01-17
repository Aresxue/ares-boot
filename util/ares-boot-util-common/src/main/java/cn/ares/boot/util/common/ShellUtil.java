package cn.ares.boot.util.common;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

/**
 * @author: Ares
 * @time: 2024-01-17 10:41:40
 * @description: Shell相关的工具类
 * @description: Shell util
 * @version: JDK 1.8
 */
public class ShellUtil {

  public static int getPid() {
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    // format: "pid@hostname"
    String name = runtime.getName();
    try {
      return Integer.parseInt(name.substring(0, name.indexOf('@')));
    } catch (Exception e) {
      return -1;
    }
  }


  public static String jstack() {
    return jstack(Thread.getAllStackTraces());
  }

  public static String jstack(Map<Thread, StackTraceElement[]> map) {
    StringBuilder result = new StringBuilder();
    try {
      map.forEach((thread, elements) -> {
        if (elements != null && elements.length > 0) {
          String threadName = thread.getName();
          result.append(String.format("%-40sTID: %d STATE: %s%n", threadName, thread.getId(),
              thread.getState()));
          for (StackTraceElement element : elements) {
            result.append(String.format("%-40s%s%n", threadName, element.toString()));
          }
          result.append("\n");
        }
      });
    } catch (Throwable throwable) {
      result.append(exceptionSimpleDesc(throwable));
    }

    return result.toString();
  }

  private static String exceptionSimpleDesc(final Throwable throwable) {
    StringBuilder builder = new StringBuilder();
    if (throwable != null) {
      builder.append(throwable);

      StackTraceElement[] stackTrace = throwable.getStackTrace();
      if (stackTrace != null && stackTrace.length > 0) {
        StackTraceElement element = stackTrace[0];
        builder.append(", ");
        builder.append(element.toString());
      }
    }

    return builder.toString();
  }

}
