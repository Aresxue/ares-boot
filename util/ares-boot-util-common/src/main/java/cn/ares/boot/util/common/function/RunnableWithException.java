package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-05-05 19:24:35
 * @description: 抛异常的Runnable
 * @description: Runnable with throw Exception
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface RunnableWithException {

  void run() throws Exception;

}
