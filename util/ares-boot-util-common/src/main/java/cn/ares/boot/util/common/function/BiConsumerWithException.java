package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-05-05 19:24:35
 * @description: 抛异常的BiConsumer
 * @description: BiConsumer with throw Exception
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface BiConsumerWithException<T, U> {

  void accept(T t, U u) throws Exception;

}
