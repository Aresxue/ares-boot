package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-12-25 19:20:44
 * @description: 三个入参的Function
 * @description: Three incoming Function
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface ThirdFunction<T, U, V, R> {

  R apply(T t, U u, V v);

}
