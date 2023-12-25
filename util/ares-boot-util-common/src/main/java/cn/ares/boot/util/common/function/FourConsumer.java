package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-11-17 11:09:07
 * @description: 四个入参的Consumer
 * @description: Four incoming Consumer
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface FourConsumer<T, U, V, W> {

  void accept(T t, U u, V v, W w);

}
