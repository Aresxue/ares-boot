package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2024-06-14 19:24:35
 * @description: 可抛出的Runnable
 * @description: Runnable with throw Throwable
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface RunnableWithThrowable {

  void run() throws Throwable;

}
