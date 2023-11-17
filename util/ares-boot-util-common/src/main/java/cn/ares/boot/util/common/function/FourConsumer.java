package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-11-17 11:09:07
 * @description: 四个入参的Consumer
 * @description: Four incoming consumers
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface FourConsumer<T, U, K, E> {

  void accept(T t, U u, K k, E e);

}
