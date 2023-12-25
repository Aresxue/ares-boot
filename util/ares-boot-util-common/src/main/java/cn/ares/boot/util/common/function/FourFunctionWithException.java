package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-12-25 19:20:44
 * @description: 抛异常的FourFunction
 * @description: FourFunction with throw Exception
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface FourFunctionWithException<T, U, V, W, R> {

  R apply(T t, U u, V v, W w) throws Exception;

}
