package cn.ares.boot.util.common;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Ares
 * @time: 2023-12-23 21:20:37
 * @description: 对invoke包下类的操作工具
 * @description: Util for manipulating classes under the invoke package
 * @version: JDK 1.8
 * @see java.lang.invoke
 */
public class InvokeUtil {

  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

  /**
   * @author: Ares
   * @description: 从jvm参数中获取方法句柄
   * @description: Gets the method handle from the jvm parameter
   * @time: 2023-05-11 13:38:06
   * @params: [method] 方法
   * @return: java.lang.invoke.MethodHandle 方法句柄
   */
  public static MethodHandle findMethodHandle(Method method) throws IllegalAccessException {
    MethodHandle methodHandle = LOOKUP.unreflect(method);
    List<Class<?>> classList = new ArrayList<>(method.getParameterCount() + 1);
    for (int i = 0; i < method.getParameterCount(); i++) {
      classList.add(Object.class);
    }
    MethodType methodType;
    if (Modifier.isStatic(method.getModifiers())) {
      methodType = MethodType.methodType(Object.class, classList);
    } else {
      classList.add(Object.class);
      methodType = MethodType.methodType(Object.class, classList);
    }
    methodHandle = methodHandle.asType(methodType);
    return methodHandle;
  }

}
