package cn.ares.boot.util.common;

import static cn.ares.boot.util.common.ArrayUtil.EMPTY_CLASS_ARRAY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Ares
 * @time: 2019-05-08 17:11:00
 * @description: class type util
 * @version: JDK 1.8
 */
public class ClassUtil {

  /**
   * base type wrapper list
   */
  private static final List<String> BASE_WRAP_TYPE_NAME_LIST = new ArrayList<>();
  private static final List<Class<?>> BASE_WRAP_TYPE_LIST = Arrays.asList(Integer.class,
      Double.class, Long.class, Short.class, Byte.class, Boolean.class, Character.class,
      Float.class);
  /**
   * base type list
   */
  private static final List<String> BASE_TYPE_NAME_LIST = new ArrayList<>();
  private static final List<Class<?>> BASE_TYPE_LIST = Arrays.asList(int.class, double.class,
      long.class, short.class, byte.class, boolean.class, char.class, float.class);


  static {
    BASE_TYPE_LIST.forEach(clazz -> BASE_TYPE_NAME_LIST.add(clazz.getCanonicalName()));
    BASE_WRAP_TYPE_LIST.forEach(clazz -> BASE_WRAP_TYPE_NAME_LIST.add(clazz.getCanonicalName()));
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
    return BASE_TYPE_NAME_LIST.contains(className);
  }

  /**
   * @author: Ares
   * @description: 判断类名是否为基本类型包装类名
   * @description: Determine whether the class name is a basic type wrapper class name
   * @time: 2019-06-14 10:01:00
   * @params: [className] 类名
   * @return: boolean 是否为基本类型包装类名
   */
  public static boolean isBaseWrap(String className) {
    return BASE_WRAP_TYPE_NAME_LIST.contains(className);
  }

  /**
   * @author: Ares
   * @description: 判断类是否为基本类型包装类
   * @description: Determine whether the class is a primitive type wrapper class
   * @time: 2019-06-14 10:01:00
   * @params: [clazz] 类
   * @return: boolean 是否为基本类型包装类
   */
  public static boolean isBaseWrap(Class<?> clazz) {
    return isBaseWrap(clazz.getCanonicalName());
  }

  /**
   * @author: Ares
   * @description: 判断类名是否为基本类型或其包装类名
   * @description: Determine whether the class name is a basic type or its wrapper class name
   * @time: 2019-06-14 10:07:00
   * @params: [className] 类名
   * @return: boolean 是否为基本类型或其包装类名
   */
  public static boolean isBaseOrWrap(String className) {
    return isPrimitive(className) || isBaseWrap(className);
  }

  /**
   * @author: Ares
   * @description: 判断类是否为基本类型或其包装类
   * @description: Determine whether a class is a primitive type or its wrapper class
   * @time: 2019-06-14 10:08:00
   * @params: [clazz] 类
   * @return: boolean response
   */
  public static boolean isBaseOrWrap(Class<?> clazz) {
    return isBaseOrWrap(clazz.getCanonicalName());
  }

  /**
   * @author: Ares
   * @description: 判断类是否为基本类型或其包装类或字符串
   * @description: Determine if a class is a primitive type or its wrapper class or a string
   * @time: 2021-07-02 15:21:00
   * @params: [clazz] 类
   * @return: boolean 是否为基本类型或其包装类或字符串
   */
  public static boolean isBaseOrWrapOrString(Class<?> clazz) {
    return isBaseOrWrap(clazz.getCanonicalName()) || isSameClass(clazz, String.class);
  }

  /**
   * @author: Ares
   * @description: 判断对象是否为基本类型或其包装类
   * @description: Determine whether an object is a primitive type or its wrapper class
   * @time: 2019-06-14 10:09:00
   * @params: [object] 对象
   * @return: boolean response
   */
  public static boolean isBaseOrWrap(Object object) {
    return null != object && isBaseOrWrap(object.getClass());
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

}
