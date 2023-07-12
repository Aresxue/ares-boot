package cn.ares.boot.util.spring;

import cn.ares.boot.util.common.ArrayUtil;
import cn.ares.boot.util.common.ClassUtil;
import cn.ares.boot.util.common.CollectionUtil;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
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
public class ReflectionUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);
  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
  private static final String GET_METHOD = "get";
  private static final String IS_METHOD = "is";
  private static final String LAMBDA_METHOD = "lambda$";
  private static final String WRITE_REPLACE_METHOD = "writeReplace";
  /***
   * 字段缓存
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
    Field field = ReflectionUtil.getField(function);
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
    return FIELD_CACHE.computeIfAbsent(function, ReflectionUtil::findField);
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
      // 获取SerializedLambda
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

      // 第4步  Spring 中的反射工具类获取Class中定义的Field
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
   * @description: 从类中获取字段（以指定名称）
   * @description: Get the field from the class (to specify the name)
   * @time: 2023-05-08 17:21:48
   * @params: [target, fieldName] 目标对象，字段名
   * @return: java.lang.reflect.Field 字段
   */
  public static Field findField(Object target, String fieldName) {
    return findField(target, fieldName, false);
  }

  /**
   * @author: Ares
   * @description: 根据字段名获取类的字段（设置访问限制）
   * @description: Get the fields of the class by field fieldName (set accessible)
   * @time: 2023-05-08 17:18:27
   * @params: [target, fieldName, accessible] 目标对象，字段名，访问限制
   * @return: java.lang.reflect.Field 字段
   */
  public static Field findField(Object target, String fieldName, boolean accessible) {
    return findField(target, fieldName, null, accessible);
  }

  /**
   * @author: Ares
   * @description: 从类中获取字段（以指定名称）
   * @description: Get the field from the class (to specify the fieldName)
   * @time: 2023-05-08 17:21:48
   * @params: [target, fieldName] 目标对象，字段名
   * @return: java.lang.reflect.Field 字段
   */
  public static Field findField(Object target, String fieldName, Class<?> fieldType) {
    return findField(target, fieldName, fieldType, false);
  }

  /**
   * @author: Ares
   * @description: 从类中获取字段（以指定名称、类型、访问限制）
   * @description: Get fields from a class (to specify fieldName, type, accessible)
   * @time: 2023-05-08 17:23:36
   * @params: [target, fieldName, fieldType, accessible] 目标对象，字段名，字段类型，访问限制
   * @return: java.lang.reflect.Field 字段
   */
  public static Field findField(Object target, String fieldName, Class<?> fieldType,
      boolean accessible) {
    if (null == target) {
      return null;
    }
    Class<?> clazz = getClass(target);
    Field field = ReflectionUtils.findField(clazz, fieldName, fieldType);
    if (null != field) {
      if (accessible) {
        ReflectionUtils.makeAccessible(field);
      }
    }
    return field;
  }

  /**
   * @author: Ares
   * @description: 从目标对象中获取所有的字段（包括父类但除了Object）
   * @description: Gets all the fields from the target Object (including the parent class but except
   * Object)
   * @time: 2023-07-12 21:02:07
   * @params: [target] 目标对象
   * @return: java.util.List<java.lang.reflect.Field> 字段列表
   */
  public static List<Field> findAllFields(Object target) {
    return findAllFields(target, false);
  }

  /**
   * @author: Ares
   * @description: 从目标对象中获取所有的字段（包括父类但除了Object）
   * @description: Gets all the fields from the target Object (including the parent class but except
   * Object)
   * @time: 2023-07-12 21:02:07
   * @params: [target, accessible] 目标对象，访问限制
   * @return: java.util.List<java.lang.reflect.Field> 字段列表
   */
  public static List<Field> findAllFields(Object target, boolean accessible) {
    if (null == target) {
      return null;
    }
    Class<?> clazz = getClass(target);
    List<Field> fieldList = new ArrayList<>();
    ReflectionUtils.doWithFields(clazz, field -> {
      if (accessible) {
        ReflectionUtils.makeAccessible(field);
      }
      fieldList.add(field);
    });
    return fieldList;
  }

  /**
   * @author: Ares
   * @description: 从目标对象中获取当前类的所有字段
   * @description: Gets all the fields of the current class from the target object
   * @time: 2023-07-12 21:02:07
   * @params: [target] 目标对象
   * @return: java.util.List<java.lang.reflect.Field> 字段列表
   */
  public static List<Field> findFields(Object target) {
    return findFields(target, false);
  }

  /**
   * @author: Ares
   * @description: 从目标对象中获取当前类的所有字段
   * @description: Gets all the fields of the current class from the target object
   * @time: 2023-07-12 21:02:07
   * @params: [target, accessible] 目标对象，访问限制
   * @return: java.util.List<java.lang.reflect.Field> 字段列表
   */
  public static List<Field> findFields(Object target, boolean accessible) {
    if (null == target) {
      return null;
    }
    Class<?> clazz = getClass(target);
    List<Field> fieldList = new ArrayList<>();
    ReflectionUtils.doWithLocalFields(clazz, field -> {
      if (accessible) {
        ReflectionUtils.makeAccessible(field);
      }
      fieldList.add(field);
    });
    return fieldList;
  }

  /**
   * @author: Ares
   * @description: 获取目标对象指定字段名称的字段值
   * @description: Gets the field value for the specified field name of the target object
   * @time: 2023-05-11 15:37:45
   * @params: [target, fieldName] 目标对象，字段值
   * @return: T out 出参
   */
  public static <T> T getFieldValue(Object target, String fieldName) {
    Field field = findField(target, fieldName, true);
    return getFieldValue(field, target);
  }

  /**
   * @author: Ares
   * @description: 根据字段和目标对象获取字段值
   * @description: Gets the field value based on the field and the target object
   * @time: 2023-05-11 15:15:54
   * @params: [field, target] 字段，目标对象
   * @return: T 字段值
   */
  public static <T> T getFieldValue(Field field, Object target) {
    return doWithHandleException(() -> {
      if (null == field) {
        return null;
      }
      ReflectionUtils.makeAccessible(field);
      return getT(field.get(target));
    });
  }

  /**
   * @author: Ares
   * @description: 根据字段名称数组获取目标对象的字段值列表（不忽略null值）
   * @description: Gets a list of field values for the target object from the field name array (null
   * values are not ignored)
   * @time: 2023-05-11 15:39:53
   * @params: [target, skipNull, fieldNames] 目标对象，是否跳过null，字段名称数组
   * @return: java.util.List<java.lang.Object> 字段值列表
   */
  public static List<Object> getFieldValueList(Object target, String... fieldNames) {
    return getFieldValueList(target, false, fieldNames);
  }

  /**
   * @author: Ares
   * @description: 根据字段名称数组获取目标对象的字段值列表（指定是否跳过null）
   * @description: Gets a list of field values for the target object from the field name array
   * (Specifies whether null is skipped)
   * @time: 2023-05-11 15:39:53
   * @params: [target, skipNull, fieldNames] 目标对象，是否跳过null，字段名称数组
   * @return: java.util.List<java.lang.Object> 字段值列表
   */
  public static List<Object> getFieldValueList(Object target, boolean skipNull,
      String... fieldNames) {
    if (null == target || ArrayUtil.isEmpty(fieldNames)) {
      return Collections.emptyList();
    }
    List<Object> list = new ArrayList<>(fieldNames.length);
    for (String fieldName : fieldNames) {
      Object value = getFieldValue(target, fieldName);
      if (!skipNull || null != value) {
        list.add(value);
      }
    }
    return list;
  }

  /**
   * @author: Ares
   * @description: 获取目标对象除了指定字段名称数组外的字段值列表
   * @description: Gets a list of field values for the target object in addition to the specified
   * array of field names
   * @time: 2023-05-11 15:44:03
   * @params: [target, excludeFieldNames] 目标对象，排除的字段名称数组
   * @return: java.util.List<java.lang.Object> 字段值列表
   */
  public static List<Object> getOtherFieldValue(Object target, String... excludeFieldNames) {
    if (null == target) {
      return Collections.emptyList();
    }
    List<Field> fieldList = new ArrayList<>();
    Class<?> clazz = getClass(target);
    ReflectionUtils.doWithFields(clazz, fieldList::add,
        field -> !ArrayUtil.contains(excludeFieldNames, field.getName()));
    if (CollectionUtil.isEmpty(fieldList)) {
      return Collections.emptyList();
    }

    return fieldList.stream().map(field -> getFieldValue(field, target))
        .collect(Collectors.toList());
  }

  /**
   * @author: Ares
   * @description: 设置目标对象的字段为指定的值
   * @description: Sets the field of the target object to the specified value
   * @time: 2023-05-08 17:19:25
   * @params: [target, fieldName, fieldValue] 目标对象，字段名，字段值
   * @return: boolean 设置结果
   */
  public static boolean setFieldValue(Object target, String name, Object fieldValue) {
    Field field = findField(target.getClass(), name, true);
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
   * @description: 设置final常量的字段值
   * @description: set final field value
   * @time: 2022-05-20 11:41:09
   * @params: [target, fieldName, fieldValue] 目标对象，字段名，字段值
   * @return: boolean 设置结果
   */
  public static boolean setFinalFieldValue(Object target, String fieldName, Object fieldValue) {
    Field field = findField(target, fieldName);
    return doWithHandleException(() -> {
      if (null != field) {
        ReflectionUtils.makeAccessible(field);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        boolean modifiersAccessible = field.isAccessible();
        if (!modifiersAccessible) {
          modifiers.setAccessible(true);
        }
        // 去掉final修饰符
        // remove final modifiers
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(target, fieldValue);
        // 把final修饰符恢复回来
        // recover final modifiers
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        return true;
      }
      return false;
    });
  }

  /**
   * @author: Ares
   * @description: 从类中获取方法（以指定名称、参数类型数组）
   * @description: Get a method from a class (to specify an array of name, parameter types)
   * @time: 2023-05-08 17:34:01
   * @params: [target, methodName, paramTypes] 目标对象，方法名，参数类型数组
   * @return: java.lang.reflect.Method 方法
   */
  public static Method findMethod(Object target, String name, Class<?>... paramTypes) {
    return findMethod(target, name, false, paramTypes);
  }

  /**
   * @author: Ares
   * @description: 从类中获取方法（以指定名称、访问限制、参数类型数组）
   * @description: Get a method from a class (to specify name, access restrictions, array of
   * parameter types)
   * @time: 2023-05-08 17:34:01
   * @params: [target, methodName, accessible, paramTypes] 目标对象，方法名，访问限制，参数类型数组
   * @return: java.lang.reflect.Method 方法
   */
  public static Method findMethod(Object target, String name, boolean accessible,
      Class<?>... paramTypes) {
    if (null == target) {
      return null;
    }
    Class<?> clazz = getClass(target);
    Method method = ReflectionUtils.findMethod(clazz, name, paramTypes);
    if (null == method) {
      // 触发兜底，避免带有基本类型的方法无法找到
      // Trigger the bottom pocket to avoid methods with basic types that cannot be found
      method = MethodUtils.getMatchingMethod(clazz, name, paramTypes);
    }
    if (null != method) {
      if (accessible) {
        ReflectionUtils.makeAccessible(method);
      }
    }
    return method;
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
    return getDeclaredMethods(clazz, false);
  }

  /**
   * @author: Ares
   * @description: 获取类声明的所有方法（从缓存中可指定访问限制）
   * @description: Gets all methods declared by a class (access restrictions can be specified from
   * the cache)
   * @time: 2023-05-08 17:40:17
   * @params: [clazz, accessible] 类，访问限制
   * @return: java.lang.reflect.Method[] 方法数组
   */
  public static Method[] getDeclaredMethods(Class<?> clazz, boolean accessible) {
    Method[] methods = ReflectionUtils.getDeclaredMethods(clazz);
    makeAccessible(accessible, methods);
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
   * @description: 获取类声明的所有方法（指定访问限制）
   * @description: Get all methods declared by a class (specifying access restrictions)
   * @time: 2023-05-08 17:40:17
   * @params: [clazz, accessible] 类，访问限制
   * @return: java.lang.reflect.Method[] 方法数组
   */
  public static Method[] getAllDeclaredMethods(Class<?> clazz, boolean accessible) {
    Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
    makeAccessible(accessible, methods);
    return methods;
  }

  /**
   * @author: Ares
   * @description: 从jvm参数中获取方法句柄
   * @description: Gets the method handle from the jvm parameter
   * @time: 2023-05-11 13:38:06
   * @params: [method] 方法
   * @return: java.lang.invoke.MethodHandle 方法句柄
   */
  public static MethodHandle findMethodHandle(Method method) {
    return doWithHandleException(() -> LOOKUP.unreflect(method));
  }

  /**
   * @author: Ares
   * @description: 使用参数数组调用目标对象的方法获取指定对象
   * @description: Gets the specified object by calling the target object's methods using an array
   * of arguments
   * @time: 2023-05-11 16:16:34
   * @params: [method, target, args] 方法，目标对象，参数数组
   * @return: T 调用结果
   */
  public static <T> T invokeMethod(Method method, Object target, Object... args) {
    if (null == method) {
      return null;
    }
    ReflectionUtils.makeAccessible(method);
    return getT(ReflectionUtils.invokeMethod(method, target, args));
  }

  /**
   * @author: Ares
   * @description: 用参数数组调用对象指定方法名的方法获取指定对象
   * @description: Gets the specified object by calling the method named by the object with an array
   * of arguments object
   * @time: 2023-05-11 16:16:04
   * @params: [target, methodName, args] 目标对象，方法名，参数数组
   * @return: T 调用结果
   */
  public static <T> T invokeMethod(Object target, String methodName, Object... args) {
    Class<?>[] parameterTypes = ClassUtil.toClass(args);
    Method method = findMethod(target, methodName, parameterTypes);
    return invokeMethod(method, target, args);
  }

  /**
   * @author: Ares
   * @description: 用参数数组调用对象指定方法名的方法获取可空对象
   * @description: Gets a nullable object using a method whose name is specified by the argument
   * array call object
   * @time: 2023-05-11 16:22:48
   * @params: [target, methodName, args] 目标对象，方法名，参数数组
   * @return: java.util.Optional<T> 可空对象
   */
  public static <T> Optional<T> invokeMethodReturnOption(Object target, String methodName,
      Object... args) {
    return Optional.ofNullable(invokeMethod(target, methodName, args));
  }

  /**
   * @author: Ares
   * @description: 使用参数数组调用目标对象的调用方法
   * @description: Invoke the calling method of the target object using an array of arguments
   * @time: 2023-05-11 16:56:20
   * @params: [invokeMethod, args] 调用方法，参数数组
   * @return: T 调用对象
   */
  public static <T> T invoke(InvokeMethod invokeMethod, Object... args) {
    if (null == invokeMethod) {
      return null;
    }
    return ReflectionUtil.invokeMethod(invokeMethod.getMethodHandle(), invokeMethod.getMethod(),
        invokeMethod.getTarget(), args);
  }


  /**
   * @author: Ares
   * @description: 使用参数数组调用目标对象（方法句柄优先）
   * @description: Call the target object with an array of arguments (method handle first)
   * @time: 2023-05-11 16:54:45
   * @params: [methodHandle, method, target, args] 方法句柄，方法，目标对象，参数数组
   * @return: T 调用结果
   */
  public static <T> T invokeMethod(MethodHandle methodHandle, Method method, Object target,
      Object... args) {
    return getT(doWithHandleException(() -> {
      if (null != methodHandle) {
        try {
          List<Object> paramList = new ArrayList<>();
          if (null != target && !(target instanceof Class)) {
            paramList.add(target);
          }
          paramList.addAll(Arrays.asList(args));
          return methodHandle.invokeWithArguments(paramList);
        } catch (Throwable e) {
          // 异常阻断Error允许向下执行
          // Exception blocking Error allows downward execution
          if (e instanceof Exception) {
            throw (Exception) e;
          }
          LOGGER.warn("method handle invoke fail: ", e);
        }
      }
      if (null == method) {
        return null;
      }
      return method.invoke(target, args);
    }));
  }

  /**
   * @author: Ares
   * @description: 根据参数数组获取类的构造器
   * @description: Gets the constructor of a class from an array of arguments
   * @time: 2023-05-11 17:47:27
   * @params: [target, args] 目标对象，参数数组
   * @return: java.lang.reflect.Constructor<T> 构造器
   */
  public static <T> Constructor<T> findConstructor(Object target, Object... args) {
    return findConstructor(target, false, args);
  }

  /**
   * @author: Ares
   * @description: 根据访问限制和参数数组获取目标对象（可能是类）的构造器
   * @description: Gets the constructor of the target object (possibly a class) based on access
   * restrictions and an array of arguments
   * @time: 2023-05-11 17:47:27
   * @params: [target, accessible, args] 目标对象，访问限制，参数数组
   * @return: java.lang.reflect.Constructor<T> 构造器
   */
  public static <T> Constructor<T> findConstructor(Object target, boolean accessible,
      Object... args) {
    return doWithHandleException(() -> {
      Class<?>[] parameterTypes = getClasses(args);
      Class<T> clazz = getClass(target);
      Constructor<T> constructor;
      try {
        constructor = ReflectionUtils.accessibleConstructor(clazz, parameterTypes);
      } catch (NoSuchMethodException e) {
        constructor = ConstructorUtils.getMatchingAccessibleConstructor(clazz, parameterTypes);
      }
      if (accessible) {
        ReflectionUtils.makeAccessible(constructor);
      }

      return constructor;
    });
  }

  /**
   * @author: Ares
   * @description: 根据类名和参数构造对象
   * @description: Constructs an object from the class name and parameters
   * @time: 2023-05-11 17:53:34
   * @params: [clazzName, args] 类名，参数数组
   * @return: T 对象
   */
  public static <T> T invokeConstructor(String clazzName, Object... args)
      throws ClassNotFoundException {
    Class<?> clazz = Class.forName(clazzName);
    return getT(invokeConstructor(clazz, args));
  }

  /**
   * @author: Ares
   * @description: 根据类和参数构造对象
   * @description: Constructs objects from classes and parameters
   * @time: 2023-05-11 17:53:01
   * @params: [clazz, args] 类， 参数
   * @return: T 对象
   */
  public static <T> T invokeConstructor(Class<T> clazz, Object... args) {
    return doWithHandleException(() -> {
      Constructor<T> constructor = findConstructor(clazz, true, args);
      return constructor.newInstance(args);
    });
  }

  /**
   * @author: Ares
   * @description: 从目标对象获取指定方法名称的调用方法
   * @description: Gets the calling method of the specified method name from the target object
   * @time: 2023-05-11 17:02:47
   * @params: [target, methodName] 目标对象，方法名称
   * @return: cn.ares.boot.util.common.entity.InvokeMethod 调用方法
   */
  public static InvokeMethod buildInvokeMethod(Object target, String methodName, Object... args) {
    Class<?>[] parameterTypes = getClasses(args);
    Method method = ReflectionUtil.findMethod(target, methodName, parameterTypes);
    return buildInvokeMethod(method, target);
  }

  /**
   * @author: Ares
   * @description: 从jvm方法和对象对象构建调用方法
   * @description: Call methods are built from jvm methods and target object
   * @time: 2023-05-11 13:35:06
   * @params: [method, target] 方法，目标对象
   * @return: cn.ares.boot.util.common.entity.InvokeMethod 调用方法
   */
  public static InvokeMethod buildInvokeMethod(Method method, Object target) {
    if (null == method) {
      return null;
    }
    ReflectionUtils.makeAccessible(method);
    InvokeMethod invokeMethod = new InvokeMethod();
    invokeMethod.setMethod(method);
    invokeMethod.setTarget(target);
    try {
      invokeMethod.setMethodHandle(findMethodHandle(method));
    } catch (Exception e) {
      LOGGER.warn("find method handle exception: ", e);
    }
    return invokeMethod;
  }

  private static <V> V doWithHandleException(Callable<V> callable) {
    try {
      return callable.call();
    } catch (Exception e) {
      ReflectionUtils.handleReflectionException(e);
    }
    throw new IllegalStateException("Should never get here");
  }

  private static <T> Class<T> getClass(Object target) {
    Class<T> clazz;
    if (target instanceof Class) {
      clazz = (Class<T>) target;
    } else {
      clazz = (Class<T>) target.getClass();
    }
    return clazz;
  }

  private static Class<?>[] getClasses(Object... args) {
    Class<?>[] parameterTypes = ClassUtil.toClassArray(args);
    if (null == parameterTypes) {
      parameterTypes = ClassUtil.toClass(args);
    }
    return parameterTypes;
  }

  private static <T> T getT(Object result) {
    return null == result ? null : (T) result;
  }

  private static void makeAccessible(boolean accessible, Method[] methods) {
    if (ArrayUtil.isNotEmpty(methods)) {
      for (Method method : methods) {
        if (accessible) {
          ReflectionUtils.makeAccessible(method);
        }
      }
    }
  }

}
