package cn.ares.boot.util.spring;

import cn.ares.boot.util.common.ArrayUtil;
import cn.ares.boot.util.common.ClassUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.common.entity.InvokeMethod;
import cn.ares.boot.util.common.function.SerializableFunction;
import java.beans.Introspector;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author: Ares
 * @time: 2021-04-05 20:45:00
 * @description: 反射工具
 * @description: Reflection util
 * @version: JDK 1.8
 */
public class ReflectionUtil1 {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil1.class);

  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
  private static final String GET_METHOD = "get";
  private static final String IS_METHOD = "is";
  private static final String LAMBDA_METHOD = "lambda$";
  private static final String WRITE_REPLACE_METHOD = "writeReplace";
  /***
   * field cache
   */
  private static final Map<SerializableFunction<?, ?>, Field> FIELD_CACHE = new ConcurrentHashMap<>();

  /*
   * @author: Ares
   * @description: 获取方法引用的名称
   * @description: Get function name
   * @time: 2022-06-08 17:52:49
   * @params: [function] 方法引用
   * @return: java.lang.String 方法引用的名称
   */
  public static String getFieldName(SerializableFunction<?, ?> function) {
    Field field = ReflectionUtil1.getField(function);
    if (null == field) {
      throw new RuntimeException("Function's field node found");
    }
    return field.getName();
  }

  /**
   * @author: Ares
   * @description: 获取方法引用代表的字段
   * @description: Get the field represented by the method reference
   * @time: 2022-06-08 17:53:44
   * @params: [function] 方法引用
   * @return: java.lang.reflect.Field
   */
  public static Field getField(SerializableFunction<?, ?> function) {
    if (null == function) {
      return null;
    }
    return FIELD_CACHE.computeIfAbsent(function, ReflectionUtil1::findField);
  }

  /**
   * @author: Ares
   * @description: 根据函数寻找合适的字段
   * @description: Find the appropriate field based on the function
   * @time: 2023-05-08 17:15:22
   * @params: [function] 函数
   * @return: java.lang.reflect.Field 字段
   */
  public static Field findField(SerializableFunction<?, ?> function) {
    Field field;
    String fieldName;
    try {
      // Get SerializedLambda
      Method method = findMethod(function.getClass(), WRITE_REPLACE_METHOD, true);
      if (null == method) {
        throw new NoSuchMethodException(WRITE_REPLACE_METHOD);
      }
      SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
      method.setAccessible(false);
      // implMethodName 即为Field对应的Getter方法名
      // ImplMethodName is the Getter method name corresponding to Field
      String implMethodName = serializedLambda.getImplMethodName();
      if (implMethodName.startsWith(GET_METHOD) && implMethodName.length() > 3) {
        fieldName = Introspector.decapitalize(implMethodName.substring(3));
      } else if (implMethodName.startsWith(IS_METHOD) && implMethodName.length() > 2) {
        fieldName = Introspector.decapitalize(implMethodName.substring(2));
      } else if (implMethodName.startsWith(LAMBDA_METHOD)) {
        // SerializableFunction不能传递lambda表达式, 只能使用方法引用
        throw new IllegalArgumentException(
            "SerializableFunction cannot pass a lambda expression, Only method references can be used");
      } else {
        throw new IllegalArgumentException(implMethodName + " not a Getter method reference");
      }
      // 获取的Class是字符串，并且包名是“/”分割，需要替换成“.”，才能获取到对应的Class对象
      // The obtained Class is a string, and the package name is divided by "/",
      // which needs to be replaced with "." to obtain the corresponding Class object
      String declaredClass = StringUtil.replace(serializedLambda.getImplClass(), "/", ".");
      Class<?> aClass = Class.forName(declaredClass, false, ClassUtils.getDefaultClassLoader());

      field = findField(aClass, fieldName, true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (null != field) {
      return field;
    }
    throw new NoSuchFieldError(fieldName);
  }

  /**
   * @author: Ares
   * @description: 根据字段名获取类的字段（设置访问限制）
   * @description: Get the fields of the class by field name (set accessible)
   * @time: 2023-05-08 17:18:27
   * @params: [clazz, fieldName, accessible] 类
   * @return: java.lang.reflect.Field 字段名
   */
  public static Field findField(Class<?> clazz, String fieldName, boolean accessible) {
    Field field = ReflectionUtils.findField(clazz, fieldName);
    if (null != field) {
      if (!field.isAccessible() && accessible) {
        field.setAccessible(true);
      }
    }
    return field;
  }

  /**
   * @author: Ares
   * @description: 设置目标对象的字段为指定的值
   * @description: Sets the field of the target object to the specified value
   * @time: 2023-05-08 17:19:25
   * @params: [target, fieldName, fieldValue] 目标对象，字段名，字段值
   * @return: boolean 设置结果
   */
  public static boolean setFieldValue(Object target, String fieldName, Object fieldValue) {
    Field field = findField(target.getClass(), fieldName, true);
    return doWithHandleException(() -> {
      if (null != field) {
        field.set(target, fieldValue);
        return true;
      }
      return false;
    });
  }

  /**
   * @author: Ares
   * @description: 设置静态常量的字段值
   * @description: set static field value
   * @time: 2022-05-20 11:41:09
   * @params: [clazz, fieldName, fieldValue] 类，字段名，字段值
   * @return: boolean 设置结果
   */
  public static boolean setStaticFinalFieldValue(Class<?> clazz, String fieldName,
      Object fieldValue) {
    Field field = findField(clazz, fieldName);
    return doWithHandleException(() -> {
      if (null != field) {
        boolean accessible = field.isAccessible();
        if (!accessible) {
          field.setAccessible(true);
        }
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        // 去掉final修饰符
        // remove modifiers
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, fieldValue);
        // 把final修饰符恢复回来
        // recover modifiers
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        if (!accessible) {
          field.setAccessible(false);
        }
        return true;
      }
      return false;
    });
  }

  /**
   * @author: Ares
   * @description: 根据字段名获取转换后的对象的字段值
   * @description: Get the field value of the transformed object based on the field name
   * @time: 2023-05-08 17:20:24
   * @params: [target, fieldName] 目标对象，字段名
   * @return: T 字段值
   */
  public static <T> T getCastFieldValue(Object target, String fieldName) {
    return (T) getFieldValue(target, fieldName);
  }

  /**
   * @author: Ares
   * @description: 根据字段名获取对象的字段值
   * @description: Get the field value of the object based on the field name
   * @time: 2023-05-08 17:21:18
   * @params: [target, fieldName] 目标对象，字段名
   * @return: java.lang.Object 字段值
   */
  public static Object getFieldValue(Object target, String fieldName) {
    Field field = findField(target.getClass(), fieldName, true);
    return doWithHandleException(() -> null == field ? null : field.get(target));
  }

  /**
   * @author: Ares
   * @description: 从类中获取字段（以指定名称）
   * @description: Get the field from the class (to specify the name)
   * @time: 2023-05-08 17:21:48
   * @params: [clazz, fieldName] 类，字段名
   * @return: java.lang.reflect.Field 字段
   */
  public static Field findField(Class<?> clazz, String fieldName) {
    return findField(clazz, fieldName, false);
  }

  /**
   * @author: Ares
   * @description: 从类中获取字段（以指定名称、类型、访问限制）
   * @description: Get fields from a class (to specify name, type, accessible)
   * @time: 2023-05-08 17:23:36
   * @params: [clazz, fieldName, fieldType, accessible] 类，字段名，字段类型，访问限制
   * @return: java.lang.reflect.Field 字段
   */
  public static Field findField(Class<?> clazz, String fieldName, Class<?> fieldType,
      boolean accessible) {
    Field field = ReflectionUtils.findField(clazz, fieldName, fieldType);
    if (null != field) {
      if (!field.isAccessible() && accessible) {
        field.setAccessible(true);
      }
    }
    return field;
  }

  /**
   * @author: Ares
   * @description: 从类中获取字段（以指定名称、类型）
   * @description: Get fields from a class (to specify name, type)
   * @time: 2023-05-08 17:25:56
   * @params: [clazz, fieldName, fieldType] 类，字段名，字段类型
   * @return: java.lang.reflect.Field 字段
   */
  public static Field findField(Class<?> clazz, String fieldName, Class<?> fieldType) {
    return findField(clazz, fieldName, fieldType, false);
  }

  /**
   * @author: Ares
   * @description: 从类中获取方法（以指定名称和访问限制）
   * @description: Getting a method from a class (to specify name and accessible)
   * @time: 2023-05-08 17:34:01
   * @params: [clazz, methodName, accessible] 类，方法名，访问限制
   * @return: java.lang.reflect.Method 方法
   */
  public static Method findMethod(Class<?> clazz, String methodName, boolean accessible) {
    Method method = ReflectionUtils.findMethod(clazz, methodName);
    if (null != method) {
      if (!method.isAccessible() && accessible) {
        method.setAccessible(true);
      }
    }
    return method;
  }

  /**
   * @author: Ares
   * @description: 从类中获取方法（以指定名称）
   * @description: Getting a method from a class (to specify the name)
   * @time: 2023-05-08 17:34:01
   * @params: [clazz, methodName, accessible] 类，方法名，访问限制
   * @return: java.lang.reflect.Method 方法
   */
  public static Method findMethod(Class<?> clazz, String methodName) {
    return findMethod(clazz, methodName, false);
  }


  /**
   * @author: Ares
   * @description: 从类中获取方法（以指定名称、访问限制、参数类型数组）
   * @description: Get a method from a class (to specify name, access restrictions, array of
   * parameter types)
   * @time: 2023-05-08 17:34:01
   * @params: [clazz, methodName, accessible, paramTypes] 类，方法名，访问限制，参数类型数组
   * @return: java.lang.reflect.Method 方法
   */
  public static Method findMethod(Class<?> clazz, String methodName, boolean accessible,
      Class<?>... paramTypes) {
    Method method = ReflectionUtils.findMethod(clazz, methodName, paramTypes);
    if (null != method) {
      if (!method.isAccessible() && accessible) {
        method.setAccessible(true);
      }
    }
    return method;
  }

  /**
   * @author: Ares
   * @description: 从类中获取方法（以指定名称、参数类型数组）
   * @description: Get a method from a class (to specify an array of name, parameter types)
   * @time: 2023-05-08 17:34:01
   * @params: [clazz, methodName, paramTypes] 类，方法名，参数类型数组
   * @return: java.lang.reflect.Method 方法
   */
  public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
    return findMethod(clazz, methodName, false, paramTypes);
  }

  /**
   * @author: Ares
   * @description: 从类中获取构造器（以指定参数类型数组）
   * @description: Get the constructor from the class (to specify an array of parameter types)
   * @time: 2023-05-08 17:39:26
   * @params: [clazz, parameterTypes] 类，参数类型数组
   * @return: java.lang.reflect.Constructor<T> 构造器
   */
  public static <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>... parameterTypes) {
    return doWithHandleException(() -> {
      Constructor<T> constructor = ReflectionUtils.accessibleConstructor(clazz, parameterTypes);
      constructor.setAccessible(true);
      return constructor;
    });
  }

  /**
   * @author: Ares
   * @description: 获取类声明的所有方法（指定访问限制）
   * @description: Get all methods declared by a class (specifying access restrictions)
   * @time: 2023-05-08 17:40:17
   * @params: [clazz, accessible] 类，访问限制
   * @return: java.lang.reflect.Method[] 方法数组
   */
  public static Method[] getAllDeclaredMethods(Class<?> clazz, boolean accessible) {
    Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
    if (ArrayUtil.isNotEmpty(methods)) {
      for (Method method : methods) {
        if (!method.isAccessible() && accessible) {
          method.setAccessible(true);
        }
      }
    }
    return methods;
  }

  /**
   * @author: Ares
   * @description: 获取类声明的所有方法
   * @description: Get all methods of a class declaration
   * @time: 2023-05-08 17:40:17
   * @params: [clazz, accessible] 类
   * @return: java.lang.reflect.Method[] 方法数组
   */
  public static Method[] getAllDeclaredMethods(Class<?> clazz) {
    return getAllDeclaredMethods(clazz, false);
  }

  /**
   * @author: Ares
   * @description: 获取类声明的所有方法（从缓存中）
   * @description: Get all methods declared by a class (from the cache)
   * @time: 2023-05-08 17:40:17
   * @params: [clazz] 类
   * @return: java.lang.reflect.Method[] 方法数组
   */
  public static Method[] getDeclaredMethods(Class<?> clazz) {
    Method[] methods = ReflectionUtils.getDeclaredMethods(clazz);
    if (ArrayUtil.isNotEmpty(methods)) {
      for (Method method : methods) {
        method.setAccessible(true);
      }
    }
    return methods;
  }

  /**
   * @author: Ares
   * @description: 使用目标对象和参数数组调用方法返回可空对象
   * @description: Call methods with target objects and an array of arguments to return nullable
   * objects
   * @time: 2023-05-08 17:46:25
   * @params: [method, target, args] 方法，目标对象，参数数组
   * @return: java.util.Optional<java.lang.Object>
   */
  public static Optional<Object> invokeMethod(Method method, Object target, Object... args) {
    if (null == method) {
      return Optional.empty();
    }
    if (!method.isAccessible()) {
      method.setAccessible(true);
    }
    return Optional.ofNullable(ReflectionUtils.invokeMethod(method, target, args));
  }

  /**
   * @author: Ares
   * @description: 调用目标对象的方法（以指定方法名称）返回泛型
   * @description: Calling a method on the target object (with the specified method name) returns
   * the generic type
   * @time: 2023-05-08 17:47:31
   * @params: [target, methodName] 目标对象，方法名
   * @return: T 方法出参
   */
  public static <T> T invokeMethodReturnT(Object target, String methodName) {
    Object result = invokeMethod(target, methodName);
    return null == result ? null : (T) result;
  }

  /**
   * @author: Ares
   * @description: 调用目标对象的方法（以指定方法名称）
   * @description: Invoke the method of the target object (to specify the method name)
   * @time: 2023-05-08 17:49:37
   * @params: [target, methodName] 目标对象，方法名称
   * @return: java.lang.Object 方法出参
   */
  public static Object invokeMethod(Object target, String methodName) {
    return invokeMethodReturnOption(target, methodName).orElse(null);
  }

  /**
   * @author: Ares
   * @description: 调用目标对象的方法获取可空对象（以指定方法名称）
   * @description: Call the target object's method to get the nullable object (to specify the method
   * name)
   * @time: 2023-05-08 17:49:37
   * @params: [target, methodName] 目标对象，方法名称
   * @return: java.lang.Object 方法出参
   */
  public static Optional<Object> invokeMethodReturnOption(Object target,
      String methodName) {
    Method method = findMethod(target.getClass(), methodName, true);
    return invokeMethod(method, target);
  }

  /**
   * @author: Ares
   * @description: 调用目标对象的方法获取指定对象（以指定方法名称）
   * @description: Call the target object's method to get the specified object (to specify the
   * method name)
   * @time: 2023-05-08 17:49:37
   * @params: [target, methodName] 目标对象，方法名称
   * @return: java.lang.Object 方法出参
   */
  public static <T> T invokeMethodReturnT(Object target, String methodName,
      Object... args) {
    Object result = invokeMethod(target, methodName, args);
    return null == result ? null : (T) result;
  }


  /**
   * @author: Ares
   * @description: 使用给定参数调用目标对象的方法获取对象（以指定方法名称）
   * @description: Get the object by calling the target object's method with the given arguments (to
   * specify the method name)
   * @time: 2023-05-08 17:49:37
   * @params: [target, methodName, args] 目标对象，方法名称，参数
   * @return: java.lang.Object 方法出参
   */
  public static Object invokeMethod(Object target, String methodName, Object... args) {
    return invokeMethodReturnOption(target, methodName, args).orElse(null);
  }

  /**
   * @author: Ares
   * @description: 使用给定参数调用目标对象的方法获取可空对象（以指定方法名称）
   * @description: Get nullable object by calling the target object's method with the given
   * arguments (to specify the method name)
   * @time: 2023-05-08 17:49:37
   * @params: [target, methodName, args] 目标对象，方法名称，参数
   * @return: java.lang.Object 方法出参
   */
  public static Optional<Object> invokeMethodReturnOption(Object target,
      String methodName, Object... args) {
    Class<?>[] parameterTypes = ClassUtil.toClass(args);
    Method method = findMethod(target.getClass(), methodName, true, parameterTypes);
    return invokeMethod(method, target, args);
  }

  /**
   * @author: Ares
   * @description: 先使用方法句柄调用如果没成功再使用方法反射
   * @description: First use the method handle to call if unsuccessful, then use method reflection
   * @time: 2021-12-04 18:10:00
   * @params: [methodHandle, method, obj, params] 方法句柄，方法，对象，参数数组
   * @return: java.lang.Object 调用结果
   */
  public static Object invokeVirtual(MethodHandle methodHandle, Method method, Object obj,
      Object... params) {
    return doWithHandleException(() -> {
      if (null != methodHandle) {
        try {
          List<Object> paramList = new ArrayList<>();
          paramList.add(obj);
          paramList.addAll(Arrays.asList(params));
          return methodHandle.invokeWithArguments(paramList);
        } catch (Throwable e) {
          if (e instanceof Exception) {
            throw (Exception) e;
          }
          LOGGER.warn("method handle invoke fail: ", e);
        }
      }
      if (null == method) {
        return null;
      }
      return method.invoke(obj, params);
    });
  }


  /**
   * @author: Ares
   * @description: 先使用方法句柄调用如果没成功再使用静态方法反射
   * @description: First use the method handle to call if it is unsuccessful, then use static method
   * reflection
   * @time: 2021-12-4 22:31：00
   * @params: [methodHandle, method, params] 方法句柄，方法，参数数组
   * @return: java.lang.Object 调用结果
   */
  public static Object invokeStatic(MethodHandle methodHandle, Method method, Object... params) {
    return doWithHandleException(() -> {
      if (null != methodHandle) {
        try {
          return methodHandle.invokeWithArguments(params);
        } catch (Throwable e) {
          if (e instanceof Exception) {
            throw (Exception) e;
          }
          LOGGER.warn("method handle invoke fail: ", e);
        }
      }
      if (null == method) {
        return null;
      }
      return method.invoke(null, params);
    });
  }

  /**
   * @author: Ares
   * @description: 从jvm方法构建调用方法
   * @description: Build invoke method from jvm method
   * @time: 2023-05-11 13:35:06
   * @params: [method] 方法
   * @return: cn.ares.boot.util.common.entity.InvokeMethod 调用方法
   */
  public static InvokeMethod buildFromMethod(Method method) {
    if (!method.isAccessible()) {
      method.setAccessible(true);
    }
    InvokeMethod invokeMethod = new InvokeMethod();
    invokeMethod.setMethod(method);
    try {
      invokeMethod.setMethodHandle(findMethodHandle(method));
    } catch (Exception e) {
      LOGGER.warn("find method handle exception: ", e);
    }
    return invokeMethod;
  }

  /**
   * @author: Ares
   * @description: 从jvm方法和目标对象构建调用方法
   * @description: Build invoke method from jvm method and target object
   * @time: 2023-05-11 13:37:00
   * @params: [method, target] 方法，目标对象
   * @return: cn.ares.boot.util.common.entity.InvokeMethod out 出参
   */
  public static InvokeMethod build(Method method, Object target) {
    InvokeMethod invokeMethod = buildFromMethod(method);
    invokeMethod.setTarget(target);
    return invokeMethod;
  }

  /**
   * @author: Ares
   * @description: 进行方法调用第一个为对象，后面为参数
   * @description: Make a method call, the first object is the object, followed by the parameters
   * @time: 2022-12-26 14:05:07
   * @params: [invokeMethod, params] 调用方法，对象和参数数组
   * @return: java.lang.Object 调用结果
   */
  public static Object invokeVirtual(InvokeMethod invokeMethod, Object... params) {
    return ReflectionUtil1.invokeVirtual(invokeMethod.getMethodHandle(), invokeMethod.getMethod(),
        invokeMethod.getTarget(), params);
  }

  /**
   * @author: Ares
   * @description: 进行静态方法调用
   * @description: Make a static method call
   * @time: 2022-12-26 14:05:33
   * @params: [invokeMethod, params] 调用方法，对象和参数数组
   * @return: java.lang.Object 调用结果
   */
  public static Object invokeStatic(InvokeMethod invokeMethod, Object... params) {
    return ReflectionUtil1.invokeStatic(invokeMethod.getMethodHandle(), invokeMethod.getMethod(),
        params);
  }

  /**
   * @author: Ares
   * @description: 从jvm参数中获取方法句柄
   * @time: 2023-05-11 13:38:06
   * @params: [method] 方法
   * @return: java.lang.invoke.MethodHandle 方法句柄
   */
  public static MethodHandle findMethodHandle(Method method) {
    return doWithHandleException(() -> LOOKUP.unreflect(method));
  }

  private static <T> T doWithHandleException(ReflectionCallback<T> reflectionCallback) {
    try {
      return reflectionCallback.doWithCatch();
    } catch (Exception e) {
      ReflectionUtils.handleReflectionException(e);
    }
    throw new IllegalStateException("Should never get here");
  }

  private interface ReflectionCallback<T> {

    T doWithCatch() throws Exception;
  }

}
