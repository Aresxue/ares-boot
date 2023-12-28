package cn.ares.boot.util.spring;

import static org.apache.commons.lang3.ArrayUtils.EMPTY_METHOD_ARRAY;

import cn.ares.boot.util.common.ArrayUtil;
import cn.ares.boot.util.common.ClassUtil;
import cn.ares.boot.util.common.CollectionUtil;
import cn.ares.boot.util.common.InvokeUtil;
import cn.ares.boot.util.common.MapUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.common.entity.InvokeMethod;
import cn.ares.boot.util.common.function.FourFunction;
import cn.ares.boot.util.common.function.SerializableFunction;
import cn.ares.boot.util.common.function.ThirdFunction;
import cn.ares.boot.util.common.throwable.CheckedExceptionWrapper;
import java.beans.Introspector;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.SerializedLambda;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * @author: Ares
 * @time: 2021-04-05 20:45:00
 * @description: 反射工具
 * @description: Reflection util
 * @version: JDK 1.8
 */
public class ReflectionUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

  private static final String GET_METHOD = "get";
  private static final String IS_METHOD = "is";
  private static final String LAMBDA_METHOD = "lambda$";
  private static final String WRITE_REPLACE_METHOD = "writeReplace";
  /***
   * 字段缓存
   * field cache
   */
  private static final Map<SerializableFunction<?, ?>, Field> FIELD_CACHE = new ConcurrentHashMap<>();
  /**
   * Cache for {@link Class#getDeclaredFields()}, allowing for fast iteration.
   * 改自org.springframework.util.ReflectionUtils#declaredFieldsCache
   * 原有的value是数组导致随着字段增加性能会下降这里改成hashMap时间复杂度为O(1)但会使用更多的内存 To change the
   * org.springframework.util.ReflectionUtils#declaredFieldsCache The original value is an array,
   * which degrades performance as fields are added. Here we change the time complexity to hashMap
   * to O(1), but it uses more memory
   */
  private static final Map<Class<?>, Map<String, List<Field>>> DECLARED_FIELD_MAP_CACHE = new ConcurrentReferenceHashMap<>(
      256);
  /**
   * Cache for {@link Class#getDeclaredMethods()} plus equivalent default methods from Java 8 based
   * interfaces, allowing for fast iteration.
   * 改自org.springframework.util.ReflectionUtils#declaredMethodsCache
   */
  private static final Map<Class<?>, Map<String, List<Method>>> DECLARED_METHOD_MAP_CACHE = new ConcurrentReferenceHashMap<>(
      256);
  /**
   * 方法句柄参数长度上限，超过该个数将不会使用方法句柄，按照JVM的规范方法句柄调用的方法最多不能超过255（JVM方法是256）
   * @see java.lang.invoke.MethodType#MAX_JVM_ARITY
   * Upper limit of the length of a method handle parameter. If this number is exceeded, the method handle will not be usedThe maximum number of methods called according to the JVM's specification method handle cannot exceed 255 (JVM method is 256).
   */
  private static final int METHOD_HANDLE_ARGS_LENGTH_UPPER = 10;

  /*
   * @author: Ares
   * @description: 获取方法引用的名称
   * @description: Get function name
   * @time: 2022-06-08 17:52:49
   * @params: [function] 方法引用
   * @return: java.lang.String 方法引用的名称
   */
  public static String getFieldName(SerializableFunction<?, ?> function) {
    Field field = getField(function);
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
    Field field = findFieldInner(clazz, fieldName, fieldType);
    if (accessible && null != field) {
      makeAccessible(field);
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
    return findAllFields(target, accessible, false);
  }

  /**
   * @author: Ares
   * @description: 从目标对象中获取所有的字段（包括父类但除了Object）
   * @description: Gets all the fields from the target Object (including the parent class but except Object)
   * @time: 2023-07-12 21:02:07
   * @params: [target, accessible, ignoreOverrideField] 目标对象，访问限制，忽略重写字段（保留子类）
   * @return: java.util.List<java.lang.reflect.Field> 字段列表
   */
  public static List<Field> findAllFields(Object target, boolean accessible,
      boolean ignoreOverrideField) {
    return findAllFields(target, accessible, ignoreOverrideField, field -> true);
  }

  /**
   * @author: Ares
   * @description: 从目标对象中获取所有的非synthetic字段（包括父类但除了Object）
   * @description: Gets all non-synthetic fields from the target Object (including the parent class but except Object)
   * @time: 2023-12-12 21:02:07
   * @params: [target, accessible, ignoreOverrideField] 目标对象，访问限制，忽略重写字段（保留子类）
   * @return: java.util.List<java.lang.reflect.Field> 字段列表
   */
  public static List<Field> findAllFieldsNotSynthetic(Object target, boolean accessible,
      boolean ignoreOverrideField) {
    return findAllFields(target, accessible, ignoreOverrideField, field -> !field.isSynthetic());
  }

  /**
   * @author: Ares
   * @description: 从目标对象中获取所有通过筛选的字段（包括父类但除了Object）
   * @description: Gets all fields that pass the filter from the target Object (including the parent class but except Object)
   * @time: 2023-07-12 21:02:07
   * @params: [target, accessible, ignoreOverrideField, predicate] 目标对象，访问限制，忽略重写字段（保留子类），字段筛选
   * @return: java.util.List<java.lang.reflect.Field> 字段列表
   */
  public static List<Field> findAllFields(Object target, boolean accessible,
      boolean ignoreOverrideField, Predicate<Field> predicate) {
    if (null == target) {
      return null;
    }
    Class<?> clazz = getClass(target);
    List<Field> fieldList = new ArrayList<>();
    Set<String> fieldNameSet = ignoreOverrideField ? new HashSet<>() : Collections.emptySet();
    ReflectionUtils.doWithFields(clazz, field -> {
      if (predicate.test(field)) {
        if (ignoreOverrideField) {
          String fieldName = field.getName();
          if (fieldNameSet.contains(fieldName)) {
            return;
          }
          fieldNameSet.add(fieldName);
        }
        if (accessible) {
          makeAccessible(field);
        }
        fieldList.add(field);
      }
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
        makeAccessible(field);
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
   * @description: 根据字段和目标对象获取字段值 开销较低
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
      return getT(field.get(target));
    });
  }

  /**
   * @author: Ares
   * @description: 根据字段名称数组获取目标对象的字段值列表（不忽略null值）
   * @description: Gets a list of field values for the target object from the field name array (null values are not ignored)
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
   * @description: Gets a list of field values for the target object from the field name array (Specifies whether null is skipped)
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
   * @description: Gets a list of field values for the target object in addition to the specified array of field names
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

    return fieldList.stream().map(field -> {
          makeAccessible(field);
          return getFieldValue(field, target);
        })
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
    Field field = findField(getClass(target), name, true);
    return setFieldValue(field, target, fieldValue);
  }

  /**
   * @author: Ares
   * @description: 设置目标对象的字段为指定的值 开销较低
   * @time: 2023-12-20 21:31:59
   * @params: [field, target, fieldValue] 字段，目标对象，字段值
   * @return: boolean 设置结果
   */
  public static boolean setFieldValue(Field field, Object target, Object fieldValue) {
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
        makeAccessible(field);
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
   * @description: Get a method from a class (to specify methodName, access restrictions, array of
   * parameter types)
   * @time: 2023-05-08 17:34:01
   * @params: [target, methodName, accessible, paramTypes] 目标对象，方法名，访问限制，参数类型数组
   * @return: java.lang.reflect.Method 方法
   */
  public static Method findMethod(Object target, String methodName, boolean accessible,
      Class<?>... paramTypes) {
    if (null == target) {
      return null;
    }
    Class<?> clazz = getClass(target);
    Method method = findMethodInner(clazz, methodName, paramTypes);
    if (accessible && null != method) {
      makeAccessible(method);
    }
    return method;
  }

  public static MethodHandle findMethodHandle(Object target, String methodName,
      Class<?>... paramTypes) {
    return InvokeUtil.findMethodHandle(findMethod(target, methodName, true, paramTypes));
  }

  public static <T, R> Function<T, R> generateFunction(Object target, String methodName,
      Class<?>... paramTypes) {
    return InvokeUtil.generateFunction(findMethod(target, methodName, true, paramTypes));
  }

  public static <T, U, R> BiFunction<T, U, R> generateBiFunction(Object target, String methodName,
      Class<?>... paramTypes) {
    return InvokeUtil.generateBiFunction(findMethod(target, methodName, true, paramTypes));
  }

  public static <T, U, V, R> ThirdFunction<T, U, V, R> generateThirdFunction(Object target, String methodName,
      Class<?>... paramTypes) {
    return InvokeUtil.generateThirdFunction(findMethod(target, methodName, true, paramTypes));
  }

  public static <T, U, V, W, R> FourFunction<T, U, V, W, R> generateFourFunction(Object target,
      String methodName, Class<?>... paramTypes) {
    return InvokeUtil.generateFourFunction(findMethod(target, methodName, true, paramTypes));
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
   * @description: Gets all methods declared by a class (access restrictions can be specified from the cache)
   * @time: 2023-05-08 17:40:17
   * @params: [clazz, accessible] 类，访问限制
   * @return: java.lang.reflect.Method[] 方法数组
   */
  public static Method[] getDeclaredMethods(Class<?> clazz, boolean accessible) {
    Map<String, List<Method>> methodMap = getDeclaredMethodMap(clazz, true);
    if (CollectionUtil.isEmpty(methodMap.values())) {
      return EMPTY_METHOD_ARRAY;
    } else {
      List<Method> totalMethodList = new ArrayList<>();
      for (List<Method> methodList : methodMap.values()) {
        totalMethodList.addAll(methodList);
      }
      if (accessible) {
        for (Method method : totalMethodList) {
          makeAccessible(method);
        }
      }
      return totalMethodList.toArray(new Method[0]);
    }
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
    Method[] methods = ReflectionUtils.getDeclaredMethods(clazz);
    if (accessible) {
      makeAccessible(methods);
    }
    return methods;
  }


  /**
   * @author: Ares
   * @description: 使用参数数组调用目标对象的方法获取指定对象 开销较低
   * @description: Gets the specified object by calling the target object's methods using an array of arguments
   * @time: 2023-05-11 16:16:34
   * @params: [method, target, args] 方法，目标对象，参数数组
   * @return: T 调用结果
   */
  public static <T> T invokeMethod(Method method, Object target, Object... args) {
    if (null == method) {
      return null;
    }
    return getT(ReflectionUtils.invokeMethod(method, target, args));
  }

  /**
   * @author: Ares
   * @description: 用参数数组调用对象指定方法名的方法获取指定对象
   * @description: Gets the specified object by calling the method named by the object with an array of arguments
   * @time: 2023-05-11 16:16:04
   * @params: [target, methodName, args] 目标对象，方法名，参数数组
   * @return: T 调用结果
   */
  public static <T> T invokeMethod(Object target, String methodName, Object... args) {
    Class<?>[] parameterTypes = ClassUtil.toClass(args);
    Method method = findMethod(target, methodName, true, parameterTypes);
    return invokeMethod(method, target, args);
  }

  /**
   * @author: Ares
   * @description: 用参数数组调用对象指定方法名的方法获取可空对象
   * @description: Gets a nullable object using a method whose name is specified by the argument array call object
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
    return invokeMethod(invokeMethod.getMethodHandle(), invokeMethod.getMethod(),
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
          if (null == target || target instanceof Class) {
            if (ArrayUtil.isEmpty(args)) {
              return methodHandle.invokeExact();
            } else if (args.length == 1) {
              return methodHandle.invokeExact(args[0]);
            } else if (args.length == 2) {
              return methodHandle.invokeExact(args[0], args[1]);
            } else if (args.length == 3) {
              return methodHandle.invokeExact(args[0], args[1], args[2]);
            } else if (args.length == 4) {
              return methodHandle.invokeExact(args[0], args[1], args[2], args[3]);
            } else if (args.length == 5) {
              return methodHandle.invokeExact(args[0], args[1], args[2], args[3], args[4]);
            } else if (args.length == 6) {
              return methodHandle.invokeExact(args[0], args[1], args[2], args[3], args[4], args[5]);
            } else if (args.length == 7) {
              return methodHandle.invokeExact(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            } else if (args.length == 9) {
              return methodHandle.invokeExact(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            } else if (args.length == METHOD_HANDLE_ARGS_LENGTH_UPPER) {
              return methodHandle.invokeExact(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            }
          } else {
            if (ArrayUtil.isEmpty(args)) {
              return methodHandle.invokeExact(target);
            } else if (args.length == 1) {
              return methodHandle.invokeExact(target, args[0]);
            } else if (args.length == 2) {
              return methodHandle.invokeExact(target, args[0], args[1]);
            } else if (args.length == 3) {
              return methodHandle.invokeExact(target, args[0], args[1], args[2]);
            } else if (args.length == 4) {
              return methodHandle.invokeExact(target, args[0], args[1], args[2], args[3]);
            } else if (args.length == 5) {
              return methodHandle.invokeExact(target, args[0], args[1], args[2], args[3], args[4]);
            } else if (args.length == 6) {
              return methodHandle.invokeExact(target, args[0], args[1], args[2], args[3], args[4], args[5]);
            } else if (args.length == 7) {
              return methodHandle.invokeExact(target, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            } else if (args.length == 9) {
              return methodHandle.invokeExact(target, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            } else if (args.length == METHOD_HANDLE_ARGS_LENGTH_UPPER) {
              return methodHandle.invokeExact(target, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            }
          }
        } catch (Throwable t) {
          if (t instanceof WrongMethodTypeException) {
            // 发生错误方法类型异常做告警不阻断
            // An error occurs. Method Type Abnormal Alarms are not blocked
            LOGGER.warn("method handle invoke fail: ", t);
          } else if (t instanceof Error) {
            throw new CheckedExceptionWrapper(t);
          } else if (t instanceof Exception) {
            throw (Exception) t;
          }
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
   * @description: 根据访问限制和参数数组获取目标对象（可能是类）的构造器
   * @description: Gets the constructor of the target object (possibly a class) based on access restrictions and an array of arguments
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
      if (accessible && null != constructor) {
        makeAccessible(constructor);
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
    return invokeConstructor(clazz, args);
  }

  /**
   * @author: Ares
   * @description: 根据类和参数构造对象
   * @description: Constructs objects from classes and parameters
   * @time: 2023-05-11 17:53:01
   * @params: [clazz, args] 类， 参数
   * @return: T 对象
   */
  public static <T> T invokeConstructor(Class<?> clazz, Object... args) {
    Constructor<T> constructor = findConstructor(clazz, true, args);
    return invokeConstructor(constructor, args);
  }

  /**
   * @author: Ares
   * @description: 根据构造器和参数构造对象 开销较低
   * @description: Constructs objects based on constructors and parameters
   * @time: 2023-12-20 21:38:50
   * @params: [constructor, args] 构造器，参数
   * @return: T 构造出的对象
   */
  public static <T> T invokeConstructor(Constructor<?> constructor, Object... args) {
    return doWithHandleException(() -> {
      if (null == constructor) {
        return null;
      }
      return getT(constructor.newInstance(args));
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
    Method method = findMethod(target, methodName, parameterTypes);
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
    makeAccessible(method);
    InvokeMethod invokeMethod = new InvokeMethod();
    invokeMethod.setMethod(method);
    invokeMethod.setTarget(target);
    try {
      invokeMethod.setMethodHandle(InvokeUtil.findMethodHandle(method));
    } catch (Exception e) {
      LOGGER.warn("find method handle exception: ", e);
    }
    return invokeMethod;
  }

  /**
   * @author: Ares
   * @description: 浅克隆
   * @description: shallow clone
   * @time: 2023-07-12 21:38:01
   * @params: [source] 源对象
   * @return: java.lang.Object 克隆出的对象
   */
  public static Object shallowClone(Object target) {
    return shallowClone(target, true);
  }

  /**
   * @author: Ares
   * @description: 浅克隆
   * @description: shallow clone
   * @time: 2023-07-12 21:38:01
   * @params: [source, includeSuperFields] 源对象，是否包含父类字段
   * @return: java.lang.Object 克隆出的对象
   */
  public static Object shallowClone(Object source, boolean includeSuperFields) {
    if (null == source) {
      return null;
    }
    Class<?> clazz = source.getClass();
    Object target = invokeConstructor(clazz);
    shallowCopy(source, target, includeSuperFields);
    return target;
  }

  /**
   * @author: Ares
   * @description: 浅拷贝
   * @description: shallow copy
   * @time: 2023-07-12 21:42:18
   * @params: [source, target ] 源对象，目标对象
   * @return: void
   */
  public static void shallowCopy(Object source, Object target) {
    shallowCopy(source, target, true);
  }

  /**
   * @author: Ares
   * @description: 浅拷贝
   * @description: shallow copy
   * @time: 2023-07-12 21:42:18
   * @params: [source, target, includeSuperFields] 源对象，目标对象，是否包含父类字段
   * @return: void
   */
  public static void shallowCopy(Object source, Object target, boolean includeSuperFields) {
    shallowCopy(source, target, includeSuperFields, true);
  }

  /**
   * @author: Ares
   * @description: 浅拷贝
   * @description: shallow copy
   * @time: 2023-07-12 21:42:18
   * @params: [source, target, includeSuperFields, ignoreNull] 源对象，目标对象，是否包含父类字段，是否忽略null值
   * @return: void
   */
  public static void shallowCopy(Object source, Object target, boolean includeSuperFields,
      boolean ignoreNull) {
    if (null == target || null == source) {
      return;
    }
    Class<?> clazz = target.getClass();
    Class<?> sourceClazz = source.getClass();
    // 不是一个类不作处理
    if (!ClassUtil.isSameClass(clazz, sourceClazz)) {
      return;
    }
    FieldCallback fieldCallback = field -> {
      // 非静态变量，都是同一个类静态变量无论如何都不需要操作
      if (!Modifier.isStatic(field.getModifiers())) {
        makeAccessible(field);
        Object fieldValue = field.get(source);
        if (!ignoreNull || null != fieldValue) {
          field.set(target, fieldValue);
        }
      }
    };
    if (includeSuperFields) {
      ReflectionUtils.doWithFields(clazz, fieldCallback);
    } else {
      ReflectionUtils.doWithLocalFields(clazz, fieldCallback);
    }
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

  /**
   * @author: Ares
   * @description: 修改方法访问符为true
   * @description: Change the methods accessible to true
   * @time: 2023-12-20 20:40:57
   * @params: [methods] 方法数组
   * @return: void
   */
  public static void makeAccessible(Method... methods) {
    if (ArrayUtil.isNotEmpty(methods)) {
      for (Method method : methods) {
        makeAccessible(method);
      }
    }
  }

  /**
   * @author: Ares
   * @description: 修改方法访问符为true
   * @description: Change the method accessible to true
   * @time: 2023-12-20 20:40:57
   * @params: [method] 方法
   * @return: void
   */
  public static void makeAccessible(Method method) {
    // 改自org.springframework.util.ReflectionUtils.makeAccessible(java.lang.reflect.Method) 将isAccessible的判断提前 相对比原来性能有大幅提升
    if (!method.isAccessible() && (!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(
        method.getDeclaringClass().getModifiers()))) {
      method.setAccessible(true);
    }
  }

  /**
   * @author: Ares
   * @description: 修改字段访问符为true
   * @description: Change the fields accessible to true
   * @time: 2023-12-20 20:40:57
   * @params: [fields] 字段数组
   * @return: void
   */
  public static void makeAccessible(Field... fields) {
    if (ArrayUtil.isNotEmpty(fields)) {
      for (Field field : fields) {
        makeAccessible(field);
      }
    }
  }

  /**
   * @author: Ares
   * @description: 修改字段访问符为true
   * @description: Change the field accessible to true
   * @time: 2023-12-20 20:40:57
   * @params: [field] 字段
   * @return: void
   */
  public static void makeAccessible(Field field) {
    // 改自org.springframework.util.ReflectionUtils.makeAccessible(java.lang.reflect.Field) 将isAccessible的判断提前 相对比原来性能有大幅提升
    if (!field.isAccessible() && (!Modifier.isPublic(field.getModifiers()) ||
        !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
        Modifier.isFinal(field.getModifiers()))) {
      field.setAccessible(true);
    }
  }

  /**
   * @author: Ares
   * @description: 修改构造器访问符为true
   * @description: Change the constructors accessible to true
   * @time: 2023-12-20 20:40:57
   * @params: [constructors] 构造器数组
   * @return: void
   */
  public static void makeAccessible(Constructor<?>... constructors) {
    if (ArrayUtil.isNotEmpty(constructors)) {
      for (Constructor<?> constructor : constructors) {
        makeAccessible(constructor);
      }
    }
  }

  /**
   * @author: Ares
   * @description: 修改构造器访问符为true
   * @description: Change the constructor accessible to true
   * @time: 2023-12-20 20:40:57
   * @params: [constructor] 构造器数组
   * @return: void
   */
  public static void makeAccessible(Constructor<?> constructor) {
    // 改自org.springframework.util.ReflectionUtils.makeAccessible(java.lang.reflect.Constructor) 将isAccessible的判断提前 相对比原来性能有大幅提升
    if (!constructor.isAccessible() && (!Modifier.isPublic(constructor.getModifiers()) ||
        !Modifier.isPublic(constructor.getDeclaringClass().getModifiers()))) {
      constructor.setAccessible(true);
    }
  }


  /**
   * @author: Ares
   * @description: 查找字段 相比于反射自身的开销，查找字段的开销很高
   * @description: Finding a field is expensive compared to the overhead of reflecting itself
   * @time: 2023-12-20 22:40:58
   * @params: [clazz, fieldName, fieldType] 类，字段名，字段类型
   * @return: java.lang.reflect.Field 字段
   */
  @Nullable
  private static Field findFieldInner(Class<?> clazz, String fieldName, Class<?> fieldType) {
    Class<?> searchType = clazz;
    while (Object.class != searchType && searchType != null) {
      List<Field> fieldList = getDeclaredFieldMap(searchType).get(fieldName);
      if (CollectionUtil.isNotEmpty(fieldList)) {
        if (null == fieldType) {
          return fieldList.get(0);
        }
        for (Field field : fieldList) {
          if (fieldType == field.getType()) {
            return field;
          }
        }
      }
      searchType = searchType.getSuperclass();
    }
    return null;
  }

  private static Map<String, List<Field>> getDeclaredFieldMap(Class<?> clazz) {
    Map<String, List<Field>> result = DECLARED_FIELD_MAP_CACHE.get(clazz);
    if (result == null) {
      try {
        Field[] declaredFields = clazz.getDeclaredFields();
        result = Arrays.stream(declaredFields).collect(Collectors.groupingBy(Field::getName));
        DECLARED_FIELD_MAP_CACHE.put(clazz,
            MapUtil.isEmpty(result) ? Collections.emptyMap() : result);
      } catch (Throwable ex) {
        throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
            "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
      }
    }
    return result;
  }

  /**
   * @author: Ares
   * @description: 查找方法 相比于反射自身的开销，查找方法的开销很高
   * @description: The search method is expensive compared to the cost of reflecting itself
   * @time: 2023-12-20 22:39:17
   * @params: [clazz, methodName, paramTypes] 类，方法名，参数类型
   * @return: java.lang.reflect.Method 方法
   */
  @Nullable
  private static Method findMethodInner(Class<?> clazz, String methodName, Class<?>... paramTypes) {
    Assert.notNull(methodName, "Method name must not be null");
    Class<?> searchType = clazz;
    while (searchType != null) {
      Map<String, List<Method>> methodMap;
      // 这里逻辑从org.springframework.util.ReflectionUtils.findMethod(Class<?>, String, Class<?>...)复制而来，后续思考为什么这么做如果可以的话接口的method也可以缓存
      if (searchType.isInterface()) {
        for (Method method : searchType.getMethods()) {
          if (methodName.equals(method.getName()) && (paramTypes == null || hasSameParams(method,
              paramTypes))) {
            return method;
          }
        }
      } else {
        methodMap = getDeclaredMethodMap(searchType, false);
        List<Method> methodList = methodMap.getOrDefault(methodName, Collections.emptyList());
        for (Method method : methodList) {
          if (paramTypes == null || hasSameParams(method, paramTypes)) {
            return method;
          }
        }
      }
      searchType = searchType.getSuperclass();
    }
    return null;
  }

  private static boolean hasSameParams(Method method, Class<?>[] paramTypes) {
    return paramTypes.length == method.getParameterCount()
        && org.apache.commons.lang3.ClassUtils.isAssignable(paramTypes, method.getParameterTypes(),
        true);
  }

  private static Map<String, List<Method>> getDeclaredMethodMap(Class<?> clazz, boolean defensive) {
    Assert.notNull(clazz, "Class must not be null");
    Map<String, List<Method>> result = DECLARED_METHOD_MAP_CACHE.get(clazz);
    if (result == null) {
      try {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
        if (defaultMethods != null) {
          result = MapUtil.newHashMap(declaredMethods.length + defaultMethods.size());
          for (Method declaredMethod : declaredMethods) {
            result.computeIfAbsent(declaredMethod.getName(), key -> new ArrayList<>(1))
                .add(declaredMethod);
          }
          for (Method defaultMethod : defaultMethods) {
            result.computeIfAbsent(defaultMethod.getName(), key -> new ArrayList<>(1))
                .add(defaultMethod);
          }
        } else {
          result = Arrays.stream(declaredMethods).collect(Collectors.groupingBy(Method::getName));
        }
        DECLARED_METHOD_MAP_CACHE.put(clazz,
            (MapUtil.isEmpty(result) ? Collections.emptyMap() : result));
      } catch (Throwable ex) {
        throw new IllegalStateException(
            "Failed to introspect Class [" + clazz.getName() + "] from ClassLoader ["
                + clazz.getClassLoader() + "]", ex);
      }
    }
    return (MapUtil.isEmpty(result) || !defensive) ? result : new HashMap<>(result);
  }

  @Nullable
  private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
    List<Method> result = null;
    for (Class<?> ifc : clazz.getInterfaces()) {
      for (Method ifcMethod : ifc.getMethods()) {
        if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
          if (result == null) {
            result = new ArrayList<>();
          }
          result.add(ifcMethod);
        }
      }
    }
    return result;
  }

}
