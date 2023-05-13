package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-05-05 19:24:35
 * @description: 抛异常的Consumer
 * @description: Consumer with throw Exception
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface ConsumerWithException<T> {

  void accept(T t) throws Exception;

}
