package cn.ares.boot.util.common.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author: Ares
 * @time: 2023-05-05 19:52:55
 * @description: 序列化函数
 * @description: Serializable function
 * @version: JDK 1.8
 */
@FunctionalInterface
public interface SerializableFunction<K, R> extends Function<K, R>, Serializable {

}
