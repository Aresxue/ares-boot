package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-12-25 19:20:44
 * @description: 抛异常的ThirdFunction
 * @description: ThirdFunction with throw Exception
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface ThirdFunctionWithException<T, U, V, R> {

  R apply(T t, U u, V v) throws Exception;

  ;

}
