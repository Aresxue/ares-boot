package cn.ares.boot.util.common;

import static cn.ares.boot.util.common.ArrayUtil.EMPTY_CLASS_ARRAY;
import static cn.ares.boot.util.common.constant.SymbolConstant.DOLLAR;
import static cn.ares.boot.util.common.constant.SymbolConstant.DOUBLE_DOLLAR;
import static cn.ares.boot.util.common.constant.SymbolConstant.LEFT_BRACKET;
import static cn.ares.boot.util.common.constant.SymbolConstant.LEFT_SQ_BRACKET;
import static cn.ares.boot.util.common.constant.SymbolConstant.RIGHT_BRACKET;
import static cn.ares.boot.util.common.constant.SymbolConstant.TILDE;

import cn.ares.boot.util.common.entity.MethodSpec;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: Ares
 * @time: 2019-05-08 17:11:00
 * @description: Class type util
 * @version: JDK 1.8
 */
public class ClassUtil {

  private static final String JDK_PROXY_CLASS_NAME = "com.sun.proxy.$Proxy";

  /**
   * Primitive type wrapper list
   */
  private static final List<String> PRIMITIVE_WRAP_TYPE_NAME_LIST = new ArrayList<>();
  private static final List<Class<?>> PRIMITIVE_WRAP_TYPE_LIST = Arrays.asList(Integer.class,
      Double.class, Long.class, Short.class, Byte.class, Boolean.class, Character.class,
      Float.class);
  /**
   * Primitive type list
   */
  private static final List<String> PRIMITIVE_TYPE_NAME_LIST = new ArrayList<>();
  private static final List<Class<?>> PRIMITIVE_TYPE_LIST = Arrays.asList(int.class, double.class,
      long.class, short.class, byte.class, boolean.class, char.class, float.class);

  /**
   * 基本类型签名映射 primitive type signature mapping
   */
  private static final Map<String, Class<?>> IDENTIFIER_TO_BASE_CLASS_MAP = MapUtil.newHashMap(16);
  private static final Map<Class<?>, String> BASE_CLASS_TO_IDENTIFIER_MAP = MapUtil.newHashMap(16);
  private final static String ARRAY_IDENTIFIER = LEFT_SQ_BRACKET;

  static {
    PRIMITIVE_TYPE_LIST.forEach(clazz -> PRIMITIVE_TYPE_NAME_LIST.add(clazz.getCanonicalName()));
    PRIMITIVE_WRAP_TYPE_LIST.forEach(
        clazz -> PRIMITIVE_WRAP_TYPE_NAME_LIST.add(clazz.getCanonicalName()));

    BASE_CLASS_TO_IDENTIFIER_MAP.put(void.class, "V");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(boolean.class, "Z");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(byte.class, "B");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(char.class, "C");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(short.class, "S");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(int.class, "I");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(long.class, "J");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(float.class, "F");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(double.class, "D");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(boolean[].class, "[Z");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(byte[].class, "[B");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(char[].class, "[C");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(short[].class, "[S");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(int[].class, "[I");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(long[].class, "[J");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(float[].class, "[F");
    BASE_CLASS_TO_IDENTIFIER_MAP.put(double[].class, "[D");

    PRIMITIVE_TYPE_LIST.forEach(
        clazz -> IDENTIFIER_TO_BASE_CLASS_MAP.put(BASE_CLASS_TO_IDENTIFIER_MAP.get(clazz), clazz));
    IDENTIFIER_TO_BASE_CLASS_MAP.put("[Z", boolean[].class);
    IDENTIFIER_TO_BASE_CLASS_MAP.put("[B", byte[].class);
    IDENTIFIER_TO_BASE_CLASS_MAP.put("[C", char[].class);
    IDENTIFIER_TO_BASE_CLASS_MAP.put("[S", short[].class);
    IDENTIFIER_TO_BASE_CLASS_MAP.put("[I", int[].class);
    IDENTIFIER_TO_BASE_CLASS_MAP.put("[J", long[].class);
    IDENTIFIER_TO_BASE_CLASS_MAP.put("[F", float[].class);
    IDENTIFIER_TO_BASE_CLASS_MAP.put("[D", double[].class);
  }

  /**
   * @author: Ares
   * @description: 判断类名是否为基本类型
   * @description: Determine if a class name is a primitive type
   * @time: 2019-05-08 17:48:00
   * @params: [className] 类名
   * @return: boolean 是否为基本类型
   **/
  public static boolean isPrimitive(String className) {
    return PRIMITIVE_TYPE_NAME_LIST.contains(className);
  }

  /**
   * @author: Ares
   * @description: 判断类名是否为基本类型包装类名
   * @description: Determine whether the class name is a primitive type wrapper class name
   * @time: 2019-06-14 10:01:00
   * @params: [className] 类名
   * @return: boolean 是否为基本类型包装类名
   */
  public static boolean isPrimitiveWrap(String className) {
    return PRIMITIVE_WRAP_TYPE_NAME_LIST.contains(className);
  }

  /**
   * @author: Ares
   * @description: 判断类是否为基本类型包装类
   * @description: Determine whether the class is a primitive type wrapper class
   * @time: 2019-06-14 10:01:00
   * @params: [clazz] 类
   * @return: boolean 是否为基本类型包装类
   */
  public static boolean isPrimitiveWrap(Class<?> clazz) {
    return isPrimitiveWrap(clazz.getCanonicalName());
  }

  /**
   * @author: Ares
   * @description: 判断类名是否为基本类型或其包装类名
   * @description: Determine whether the class name is a primitive type or its wrapper class name
   * @time: 2019-06-14 10:07:00
   * @params: [className] 类名
   * @return: boolean 是否为基本类型或其包装类名
   */
  public static boolean isPrimitiveOrWrap(String className) {
    return isPrimitive(className) || isPrimitiveWrap(className);
  }

  /**
   * @author: Ares
   * @description: 判断类是否为基本类型或其包装类
   * @description: Determine whether a class is a primitive type or its wrapper class
   * @time: 2019-06-14 10:08:00
   * @params: [clazz] 类
   * @return: boolean response
   */
  public static boolean isPrimitiveOrWrap(Class<?> clazz) {
    return isPrimitiveOrWrap(clazz.getCanonicalName());
  }

  /**
   * @author: Ares
   * @description: 判断类是否为基本类型或其包装类或字符串
   * @description: Determine if a class is a primitive type or its wrapper class or a string
   * @time: 2021-07-02 15:21:00
   * @params: [clazz] 类
   * @return: boolean 是否为基本类型或其包装类或字符串
   */
  public static boolean isPrimitiveOrWrapOrString(Class<?> clazz) {
    return isPrimitiveOrWrap(clazz.getCanonicalName()) || isSameClass(clazz, String.class);
  }

  /**
   * @author: Ares
   * @description: 判断对象是否为基本类型或其包装类
   * @description: Determine whether an object is a primitive type or its wrapper class
   * @time: 2019-06-14 10:09:00
   * @params: [object] 对象
   * @return: boolean response
   */
  public static boolean isPrimitiveOrWrap(Object object) {
    return null != object && isPrimitiveOrWrap(object.getClass());
  }

  /**
   * @author: Ares
   * @description: 判断两个类是否相同
   * @description: Check if two classes are the same
   * @time: 2020-08-27 14:50:00
   * @params: [clazz, otherClass] 类，另一个类
   * @return: boolean 是否相同
   */
  public static boolean isSameClass(Class<?> clazz, Class<?> otherClass) {
    if (null == clazz && null == otherClass) {
      return true;
    }
    if (null == clazz || null == otherClass) {
      return false;
    }
    return clazz.isAssignableFrom(otherClass) && otherClass.isAssignableFrom(clazz)
        && clazz.getCanonicalName().equals(otherClass.getCanonicalName())
        && clazz.getClassLoader() == otherClass.getClassLoader();
  }

  /**
   * @author: Ares
   * @description: 判断两个类的类名是否一致（可能在两个类加载器中）
   * @description: judge two class is same name(maybe has different classLoader)
   * @time: 2022-12-02 15:06:00
   * @params: [clazz, otherClass] 类，另一个类
   * @return: boolean 是否相同
   */
  public static boolean isSameNameClass(Class<?> clazz, Class<?> otherClass) {
    if (null == clazz && null == otherClass) {
      return true;
    }
    if (null == clazz || null == otherClass) {
      return false;
    }
    return clazz.getCanonicalName().equals(otherClass.getCanonicalName());
  }

  /**
   * @author: Ares
   * @description: 获取类的短名
   * @description: Get the short name of the class
   * @time: 2022-06-08 11:31:40
   * @params: [className] 原类名
   * @return: java.lang.String 短名
   */
  public static String getShortClassName(String className) {
    if (className == null) {
      return null;
    } else {
      String[] ss = className.split("\\.");
      StringBuilder sb = new StringBuilder(className.length());

      for (int i = 0; i < ss.length; ++i) {
        String s = ss[i];
        if (i != ss.length - 1) {
          sb.append(s.charAt(0)).append('.');
        } else {
          sb.append(s);
        }
      }

      return sb.toString();
    }
  }

  /**
   * @author: Ares
   * @description: 判断是否是jdk原生的类型, 如List.class、Map.class
   * @description: Determine whether it is a native type of jdk, such as List, Map.
   * @time: 2021-12-13 19:36:00
   * @params: [clazz] 类
   * @return: boolean 是否是jdk原生的类型
   */
  public static boolean isOriginJdkType(Class<?> clazz) {
    return null != clazz && null == clazz.getClassLoader();
  }

  /**
   * <p>Converts an array of {@code Object} in to an array of {@code Class} objects.
   * If any of these objects is null, a null element will be inserted into the array.</p>
   *
   * <p>因为是数组所以隐式地带上了一个类型装箱如果有基本类型会转为对应的包装类，这可能会导致反射找方法时找不到使用时要慎重</p>
   * <p>This method returns {@code null} for a {@code null} input array.</p>
   *
   * @param array an {@code Object} array
   * @return a {@code Class} array, {@code null} if null array input
   */
  public static Class<?>[] toClass(final Object... array) {
    if (array == null) {
      return null;
    } else if (array.length == 0) {
      return EMPTY_CLASS_ARRAY;
    }
    final Class<?>[] classes = new Class[array.length];
    for (int i = 0; i < array.length; i++) {
      classes[i] = array[i] == null ? null : array[i].getClass();
    }
    return classes;
  }

  /**
   * @author: Ares
   * @description: 将参数类型数组转为字符串表示
   * @description: Converts an array of parameter types to a string representation
   * @time: 2023-05-08 11:03:43
   * @params: [argTypes] 参数类型数组
   * @return: java.lang.String 转换后字符串
   */
  public static String argumentTypesToString(Class<?>[] argTypes) {
    StringBuilder buf = new StringBuilder();
    buf.append("(");
    if (argTypes != null) {
      for (int i = 0; i < argTypes.length; i++) {
        if (i > 0) {
          buf.append(", ");
        }
        Class<?> c = argTypes[i];
        buf.append((c == null) ? "null" : c.getName());
      }
    }
    buf.append(")");
    return buf.toString();
  }

  /**
   * @author: Ares
   * @description: 将参数类型数组转为字符串表示
   * @description: Converts an array of parameter types to a string representation
   * @time: 2023-05-08 13:26:48
   * @params: [parameterTypes] 参数类型数组
   * @return: java.lang.String[] 参数类型字符串数组
   */
  public static String[] transformClass(Class<?>[] parameterTypes) {
    if (ArrayUtil.isEmpty(parameterTypes)) {
      return null;
    }
    List<String> paramTypes = new ArrayList<>();
    for (Class<?> clazz : parameterTypes) {
      paramTypes.add(clazz.getCanonicalName());
    }
    return paramTypes.toArray(new String[0]);
  }

  /**
   * @author: Ares
   * @description: 判断类是否存在
   * @description: Determines whether the class exists
   * @time: 2023-04-21 14:52:07
   * @params: [className, classLoader] 类名，类加载器
   * @return: boolean 是否存在
   */
  public static boolean existClass(String className, ClassLoader classLoader) {
    try {
      Class.forName(className, false, classLoader);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * @author: Ares
   * @description: 判断类是否存在
   * @description: Determines whether the class exists
   * @time: 2023-04-21 14:52:07
   * @params: [className] 类名
   * @return: boolean 是否存在
   */
  public static boolean existClass(String className) {
    return existClass(className, Thread.currentThread().getContextClassLoader());
  }

  /**
   * @author: Ares
   * @description: 是否是类数组
   * @description: Class array or not
   * @time: 2023-05-11 17:20:57
   * @params: [args] 参数数组
   * @return: boolean 是否是类数组
   */
  public static Class<?>[] toClassArray(Object... args) {
    if (ArrayUtil.isEmpty(args)) {
      return EMPTY_CLASS_ARRAY;
    }
    Class<?>[] classes = new Class[args.length];
    for (int i = 0; i < args.length; i++) {
      if (!(args[i] instanceof Class)) {
        return null;
      } else {
        classes[i] = (Class<?>) args[i];
      }
    }
    return classes;
  }

  /**
   * @author: Ares
   * @description: 判断类是否为final
   * @description: Determines whether the class is final
   * @time: 2023-06-02 13:07:06
   * @params: [clazz] 类
   * @return: boolean 是否为final
   */
  public static boolean isFinal(Class<?> clazz) {
    return Modifier.isFinal(clazz.getModifiers());
  }

  /**
   * @author: Ares
   * @description: 根据参数类型构造签名
   * @description: Construct signatures based on classes
   * @time: 2023-06-27 13:15:46
   * @params: [classes] 类数组
   * @return: java.lang.String 描述
   * @return: java.lang.String description
   */
  public static String constructParameterTypeSignature(Class<?>[] classes) {
    if (ArrayUtil.isEmpty(classes)) {
      return "";
    }

    StringBuilder signatureBuilder = new StringBuilder();

    for (Class<?> clazz : classes) {
      signatureBuilder.append(getTypeSignature(clazz));
    }

    return signatureBuilder.toString();
  }

  /**
   * @author: Ares
   * @description: 获取类型签名
   * @description: Get type signature
   * @time: 2023-06-27 13:54:21
   * @params: [clazz] 类
   * @return: java.lang.String 字符串标识
   */
  public static String getTypeSignature(Class<?> clazz) {
    if (clazz.isArray()) {
      return "[" + getTypeSignature(clazz.getComponentType());
    }

    if (clazz.isPrimitive()) {
      return BASE_CLASS_TO_IDENTIFIER_MAP.get(clazz);
    }

    return "L" + clazz.getName().replace('.', '/') + ";";
  }


  /**
   * 解析方法签名（不加载类）
   * Parse method signatures (without loading classes)
   *
   * @param methodDesc 方法签名
   * @return 分离出的方法签名
   */
  public static MethodSpec parseIdentifier(String methodDesc) {
    MethodSpec methodSpec = new MethodSpec();
    String paramDesc = StringUtil.substringBetween(methodDesc, "(", ")");
    if (StringUtil.isEmpty(methodDesc)) {
      methodSpec.setParamIdentifiers(new String[0]);
    } else {
      List<String> paramIdentifierList = new ArrayList<>();
      char[] chars = paramDesc.toCharArray();
      StringBuilder builder = new StringBuilder();
      for (char c : chars) {
        switch (c) {
          case 'Z':
          case 'B':
          case 'C':
          case 'S':
          case 'I':
          case 'J':
          case 'F':
          case 'D':
            builder.append(c);
            if (builder.length() <= 2) {
              paramIdentifierList.add(builder.toString());
              builder.setLength(0);
            }
            break;
          case ';':
            builder.append(c);
            paramIdentifierList.add(builder.toString());
            builder.setLength(0);
            break;
          default:
            builder.append(c);
            break;
        }
      }
      methodSpec.setParamIdentifiers(paramIdentifierList.toArray(new String[0]));
    }
    methodSpec.setReturnIdentifier(StringUtil.substringAfter(methodDesc, ")"));
    return methodSpec;
  }

  /**
   * 加载类数组
   * Loaded class array
   *
   * @param identifiers 根据方法签名解析的标志组合
   * @param classLoader 类加载器
   * @return 类数组
   * @throws ClassNotFoundException 找不到类异常
   */
  public static Class<?>[] loadClass(String[] identifiers, ClassLoader classLoader)
      throws ClassNotFoundException {
    Class<?>[] classes;
    if (ArrayUtil.isEmpty(identifiers)) {
      classes = new Class[0];
    } else {
      classes = new Class[identifiers.length];
      for (int i = 0; i < identifiers.length; i++) {
        classes[i] = loadClass(identifiers[i], classLoader);
      }
    }
    return classes;
  }

  /**
   * 加载单个类
   * Loading a single class
   *
   * @param identifier  根据方法签名解析的标志
   * @param classLoader 类加载器
   * @return 类数组
   * @throws ClassNotFoundException 找不到类异常
   */
  public static Class<?> loadClass(String identifier, ClassLoader classLoader)
      throws ClassNotFoundException {
    Class<?> clazz = IDENTIFIER_TO_BASE_CLASS_MAP.get(identifier);
    if (null != clazz) {
      return clazz;
    }
    if (classLoader == null) {
      classLoader = ClassLoader.getSystemClassLoader();
    }
    // 全类名查找;[可以用Class.forName去加载
    if (identifier.startsWith(ARRAY_IDENTIFIER)) {
      return Class.forName(toNormalClass(identifier), true, classLoader);
    } else {
      return classLoader.loadClass(toNormalClass(extractClassName(identifier)));
    }
  }

  /**
   * 提取类名
   * Extract class name
   *
   * @param identifier 根据方法签名解析的标志
   * @return 类名
   */
  public static String extractClassName(String identifier) {
    return StringUtil.substringBetween(identifier, "L", ";");
  }

  /**
   * 转换成通用类路径
   * Convert to a generic classpath
   *
   * @param identifier 根据方法签名解析的标志
   * @return 类路径
   */
  public static String toNormalClass(String identifier) {
    return identifier.replace("/", ".");
  }

/**
   * 构造方法签名
   * Build method signature
   *
   * @param methodName 方法名
   * @param paramTypeNames 参数类型
   * @return 方法签名
   */
  public static String buildMethodDesc(String methodName, String paramTypeNames){
    StringBuilder builder = new StringBuilder(methodName + TILDE + LEFT_BRACKET);
    if (StringUtil.isNotEmpty(paramTypeNames)) {
      builder.append(paramTypeNames);
    }
    builder.append(RIGHT_BRACKET);
    return builder.toString();
  }

  /**
   * @author: Ares
   * @description: 两个类是否都是java.lang包下的类
   * @description: Whether both classes are classes in the java.lang package
   * @time: 2023-12-07 14:26:39 
   * @params: [leftClass, rightClass] 左类，右类 
   * @return: boolean 是否都是java.lang包下的类
   */
  public static boolean isBothJavaLang(Class<?> leftClass, Class<?> rightClass) {
    if (null == leftClass || null == rightClass) {
      return false;
    }
    return isJavaLang(leftClass) && isJavaLang(rightClass);
  }

  /**
   * @author: Ares
   * @description: 两个类是否都是java.math包下的类
   * @description: Whether both classes are classes in the java.math package
   * @time: 2023-12-07 14:26:39 
   * @params: [leftClass, rightClass] 左类，右类 
   * @return: boolean 是否都是java.math包下的类
   */
  public static boolean isBothJavaMath(Class<?> leftClass, Class<?> rightClass) {
    if (null == leftClass || null == rightClass) {
      return false;
    }
    return isJavaMath(leftClass) && isJavaMath(rightClass);
  }

  /**
   * @author: Ares
   * @description: 两个类是否都是java.time包下的类
   * @description: Whether both classes are classes in the java.time package
   * @time: 2023-12-07 14:26:39 
   * @params: [leftClass, rightClass] 左类，右类 
   * @return: boolean 是否都是java.time包下的类
   */
  public static boolean isBothJavaTime(Class<?> leftClass, Class<?> rightClass) {
    if (null == leftClass || null == rightClass) {
      return false;
    }
    return isJavaTime(leftClass) && isJavaTime(rightClass);
  }

  /**
   * @author: Ares
   * @description: 两个类是否都是java.util包下的类
   * @description: Whether both classes are classes in the java.util package
   * @time: 2023-12-07 14:26:39 
   * @params: [leftClass, rightClass] 左类，右类 
   * @return: boolean 是否都是java.util包下的类
   */
  public static boolean isBothJavaUtil(Class<?> leftClass, Class<?> rightClass) {
    if (null == leftClass || null == rightClass) {
      return false;
    }
    return isJavaUtil(leftClass) && isJavaUtil(rightClass);
  }

  /**
   * @author: Ares
   * @description: 两个类是否都是基本类型
   * @description: Whether both classes are primitive types
   * @time: 2023-12-07 14:26:39
   * @params: [leftClass, rightClass] 左类，右类
   * @return: boolean 是否都是基本类型
   */
  public static boolean isBasicType(Class<?> leftClass, Class<?> rightClass) {
    if (null == leftClass || null == rightClass) {
      return false;
    }
    return leftClass.isPrimitive() && rightClass.isPrimitive();
  }

  /**
   * @author: Ares
   * @description: 两个类是否都是Map类型
   * @description: Whether both classes are Map types
   * @time: 2023-12-07 14:26:39
   * @params: [leftClass, rightClass] 左类，右类
   * @return: boolean 是否都是Map类型
   */
  public static boolean isMap(Class<?> leftClass, Class<?> rightClass) {
    if (null == leftClass || null == rightClass) {
      return false;
    }
    return Map.class.isAssignableFrom(leftClass) && Map.class.isAssignableFrom(rightClass);
  }

  /**
   * @author: Ares
   * @description: 两个类是否都是数组类型
   * @description: Whether both classes are array types
   * @time: 2023-12-07 14:26:39
   * @params: [leftClass, rightClass] 左类，右类
   * @return: boolean 是否都是数组类型
   */
  public static boolean isArray(Class<?> leftClass, Class<?> rightClass) {
    if (null == leftClass || null == rightClass) {
      return false;
    }
    return leftClass.isArray() && rightClass.isArray();
  }

  /**
   * @author: Ares
   * @description: 两个类是否都是集合类型
   * @description: Whether both classes are collection types
   * @time: 2023-12-07 14:26:39
   * @params: [leftClass, rightClass] 左类，右类
   * @return: boolean 是否都是集合类型
   */
  public static boolean isCollection(Class<?> leftClass, Class<?> rightClass) {
    if (null == leftClass || null == rightClass) {
      return false;
    }
    return Collection.class.isAssignableFrom(leftClass) && Collection.class.isAssignableFrom(rightClass);
  }

  /**
   * @author: Ares
   * @description: 两个类是否都是原生的jdk类型
   * @description: Whether both classes are native jdk types
   * @time: 2023-12-07 14:26:39
   * @params: [leftClass, rightClass] 左类，右类
   * @return: boolean 是否都是原生的jdk类型
   */
  public static boolean isOriginJdkType(Class<?> leftClass, Class<?> rightClass) {
    if (null == leftClass || null == rightClass) {
      return false;
    }
    return null == leftClass.getClassLoader() && null == rightClass.getClassLoader();
  }

  /**
   * @author: Ares
   * @description: 类是否是指定包下的
   * @description: Whether the class belongs to the specified package
   * @time: 2023-12-07 14:26:39
   * @params: [clazz, packagePrefix] 类，包名前缀
   * @return: boolean 是否是指定包下的
   */
  public static boolean isSamePackagePrefix(Class<?> clazz, String packagePrefix) {
    if (null == clazz) {
      return false;
    }
    String canonicalName = clazz.getCanonicalName();
    return null != canonicalName && canonicalName.startsWith(packagePrefix);
  }

  /**
   * @author: Ares
   * @description: 类是否是java.lang包下的
   * @description: Whether the class is in the java.lang package
   * @time: 2023-12-07 14:32:53
   * @params: [clazz] 类
   * @return: boolean 是否是java.lang包下的
   */
  public static boolean isJavaLang(Class<?> clazz) {
    if (null == clazz) {
      return false;
    }
    return isSamePackagePrefix(clazz, "java.lang");
  }

  /**
   * @author: Ares
   * @description: 类是否是java.time包下的
   * @description: Whether the class is in the java.time package
   * @time: 2023-12-07 14:32:53
   * @params: [clazz] 类
   * @return: boolean 是否是java.time包下的
   */
  public static boolean isJavaTime(Class<?> clazz) {
    if (null == clazz) {
      return false;
    }
    return isSamePackagePrefix(clazz, "java.time");
  }

  /**
   * @author: Ares
   * @description: 类是否是java.math包下的
   * @description: Whether the class is in the java.math package
   * @time: 2023-12-07 14:32:53
   * @params: [clazz] 类
   * @return: boolean 是否是java.math包下的
   */
  public static boolean isJavaMath(Class<?> clazz) {
    if (null == clazz) {
      return false;
    }
    return isSamePackagePrefix(clazz, "java.math");
  }

  /**
   * @author: Ares
   * @description: 类是否是java.util包下的
   * @description: Whether the class is in the java.util package
   * @time: 2023-12-07 14:32:53
   * @params: [clazz] 类
   * @return: boolean 是否是java.util包下的
   */
  public static boolean isJavaUtil(Class<?> clazz) {
    if (null == clazz) {
      return false;
    }
    return isSamePackagePrefix(clazz, "java.util");
  }

  /**
   * @author: Ares
   * @description: 获取原始的类名(代理类会解析出原始类名)
   * @description: Get the original class name (the proxy class resolves the original class name)
   * @time: 2023-12-07 14:41:45
   * @params: [className] 类名
   * @return: java.lang.String 原始类名
   */
  public static String getOriginClassName(String className) {
    if (StringUtil.isEmpty(className)) {
      return className;
    }
    // jdk代理类去掉数字
    // jdk proxy classes remove numbers
    if (className.startsWith(JDK_PROXY_CLASS_NAME)) {
      return JDK_PROXY_CLASS_NAME;
    }

    // 代码类去除$$及以后
    // Code class removed $$and later
    int index = className.indexOf(DOUBLE_DOLLAR);
    if (index > 0) {
      className = className.substring(0, index);
    }
    // 再按照$去除，这种可能是字节码工具生成的代理类
    // Then remove by $, this may be the bytecode tool generated proxy class
    index = className.indexOf(DOLLAR);
    if (index > 0) {
      className = className.substring(0, index);
    }
    return className;
  }

  /**
   * @author: Ares
   * @description: 是否是jdk代理类名
   * @description: Whether it is a jdk proxy class name
   * @time: 2023-12-07 14:43:46
   * @params: [className] 类名
   * @return: boolean 是否是jdk代理类名
   */
  public static boolean isJdkProxyClass(String className) {
    return className.startsWith(JDK_PROXY_CLASS_NAME);
  }

  /**
   * @author: Ares
   * @description: 获取原始的方法名(代理方法会解析出代理方法)
   * @description: Get the original method name (the proxy method resolves the proxy method)
   * @time: 2023-12-07 14:41:45
   * @params: [methodName] 方法名
   * @return: java.lang.String 原始方法名
   */
  public static String getOriginMethodName(String methodName) {
    if (StringUtil.isEmpty(methodName)) {
      return methodName;
    }
    // 按照$去除，这种可能是字节码工具生成的代理方法
    // Removed by $, this may be a proxy method generated by the bytecode tool
    int index = methodName.indexOf(DOLLAR);
    if (index > 0) {
      methodName = methodName.substring(0, index);
    }
    return methodName;
  }

}
