package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-12-25 19:20:44
 * @description: 四个入参的Function
 * @description: Four incoming Function
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface FourFunction<T, U, V, W, R> {

  R apply(T t, U u, V v, W w);

}
