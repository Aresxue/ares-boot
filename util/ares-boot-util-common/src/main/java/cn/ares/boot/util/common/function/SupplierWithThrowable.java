package cn.ares.boot.util.common.function;

/**
 * @author: Ares
 * @time: 2023-05-05 19:14:10
 * @description: 抛出的Supplier
 * @description: Supplier with throw Throwable
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface SupplierWithThrowable<T> {

  T get() throws Throwable;

}
