package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-11-17 11:09:07
 * @description: 三个入参的Consumer
 * @description: Three incoming consumers
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface ThirdConsumer<T, U, K> {

  void accept(T t, U u, K k);

}
