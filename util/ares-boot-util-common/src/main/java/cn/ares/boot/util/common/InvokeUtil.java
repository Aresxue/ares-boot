package cn.ares.boot.util.common;

import cn.ares.boot.util.common.function.FourFunction;
import cn.ares.boot.util.common.function.SupplierWithThrowable;
import cn.ares.boot.util.common.function.ThirdFunction;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author: Ares
 * @time: 2023-12-23 21:20:37
 * @description: 对invoke包下类的操作工具
 * @description: Util for manipulating classes under the invoke package
 * @version: JDK 1.8
 * @see java.lang.invoke
 */
public class InvokeUtil {


  /**
   * @author: Ares
   * @description: 从jvm参数中获取方法句柄
   * @description: Gets the method handle from the jvm parameter
   * @time: 2023-05-11 13:38:06
   * @params: [method] 方法
   * @return: java.lang.invoke.MethodHandle 方法句柄
   */
  public static MethodHandle findMethodHandle(Method method) {
    return findMethodHandle(method, true);
  }

  /**
   * @author: Ares
   * @description: 从jvm参数中获取方法句柄
   * @description: Gets the method handle from the jvm parameter
   * @time: 2023-05-11 13:38:06
   * @params: [method, generic] 方法，是否将所有类型转为Object
   * @return: java.lang.invoke.MethodHandle 方法句柄
   */
  public static MethodHandle findMethodHandle(Method method, boolean generic) {
    if (null == method) {
      return null;
    }
    MethodHandle methodHandle = ExceptionUtil.get(
        () -> MethodHandleUtil.getLookup(method, false).unreflect(method));
    if (generic) {
      methodHandle = methodHandle.asType(methodHandle.type().generic());
    }
    return methodHandle;
  }

  /**
   * @author: Ares
   * @description: 基于方法动态生成函数
   * @description: Generate function dynamically based on method
   * @time: 2023-12-23 21:34:44
   * @params: [method] 方法
   * @return: java.util.function.Function<T, R> 函数
   */
  public static <T, R> Function<T, R> generateFunction(Method method) {
    return generateFunction(method, () -> {
      CallSite site = getCallSite(method, Function.class);
      return (Function<T, R>) (site.getTarget().invokeExact());
    });
  }

  /**
   * @author: Ares
   * @description: 基于方法动态生成两个入参的函数
   * @description: Generates two incoming function dynamically based on the method
   * @time: 2023-12-25 19:40:25
   * @params: [method] 方法
   * @return: java.util.function.BiFunction<T,U,R> 两个入参的函数
   */
  public static <T, U, R> BiFunction<T, U, R> generateBiFunction(Method method) {
    return generateFunction(method, () -> {
      CallSite site = getCallSite(method, BiFunction.class);
      return (BiFunction<T, U, R>) (site.getTarget().invokeExact());
    });
  }


  /**
   * @author: Ares
   * @description: 基于方法动态生成三个入参的函数
   * @description: Generates three incoming function dynamically based on the method
   * @time: 2023-12-25 19:40:25
   * @params: [method] 方法
   * @return: cn.ares.boot.util.common.function.ThirdFunction<T, U, V, R> 三个入参的函数
   */
  public static <T, U, V, R> ThirdFunction<T, U, V, R> generateThirdFunction(Method method) {
    return generateFunction(method, () -> {
      CallSite site = getCallSite(method, ThirdFunction.class);
      return (ThirdFunction<T, U, V, R>) (site.getTarget().invokeExact());
    });
  }


  /**
   * @author: Ares
   * @description: 基于方法动态生成四个入参的函数
   * @description: Generates four incoming function dynamically based on the method
   * @time: 2023-12-25 19:40:25
   * @params: [method] 方法
   * @return: cn.ares.boot.util.common.function.FourFunction<T, U, V, W, R> 四个入参的函数
   */
  public static <T, U, V, W, R> FourFunction<T, U, V, W, R> generateFourFunction(Method method) {
    return generateFunction(method, () -> {
      CallSite site = getCallSite(method, FourFunction.class);
      return (FourFunction<T, U, V, W, R>) (site.getTarget().invokeExact());
    });
  }


  private static <T> T generateFunction(Method method, SupplierWithThrowable<T> supplier) {
    if (null == method) {
      return null;
    }
    try {
      return supplier.get();
    } catch (Throwable e) {
      throw new IllegalArgumentException("Function creation failed for method (" + method + ").",
          e);
    }
  }

  private static <T> CallSite getCallSite(Method method, Class<T> functionClass)
      throws IllegalAccessException, LambdaConversionException {
    CallSite site;
    MethodHandles.Lookup lookup = MethodHandleUtil.getLookup(method, true);
    MethodHandle methodHandle = lookup.unreflect(method);
    MethodType methodType = methodHandle.type();
    site = LambdaMetafactory.metafactory(lookup,
        "apply",
        MethodType.methodType(functionClass),
        methodType.generic(),
        methodHandle,
        methodType);
    return site;
  }

}
