package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-11-17 11:09:07
 * @description: 抛异常的FourConsumer
 * @description: FourConsumer with throw Exception
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface FourConsumerWithException<T, U, K, E> {

  void accept(T t, U u, K k, E e) throws Exception;

}
