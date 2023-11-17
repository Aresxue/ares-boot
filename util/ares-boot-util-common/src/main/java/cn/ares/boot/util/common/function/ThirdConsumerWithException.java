package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-11-17 11:09:07
 * @description: 抛异常的ThirdConsumer
 * @description: ThirdConsumer with throw Exception
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface ThirdConsumerWithException<T, U, K> {

  void accept(T t, U u, K k) throws Exception;

}
