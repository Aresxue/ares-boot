package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-05-05 19:24:35
 * @description: 抛出的Consumer
 * @description: Consumer with throw Throwable
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface ConsumerWithThrowable<T> {

  void accept(T t) throws Throwable;

}
