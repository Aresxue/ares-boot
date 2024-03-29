package cn.ares.boot.util.common;

import java.lang.reflect.Array;

/**
 * @author: Ares
 * @time: 2021-12-12 16:35:00
 * @description: Array util
 * @version: JDK 1.8
 */
public class ArrayUtil {

  /**
   * An empty immutable {@code Object} array.
   */
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  /**
   * An empty immutable {@code Class} array.
   */
  public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
  /**
   * An empty immutable {@code String} array.
   */
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  /**
   * An empty immutable {@code long} array.
   */
  public static final long[] EMPTY_LONG_ARRAY = new long[0];
  /**
   * An empty immutable {@code Long} array.
   */
  public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
  /**
   * An empty immutable {@code int} array.
   */
  public static final int[] EMPTY_INT_ARRAY = new int[0];
  /**
   * An empty immutable {@code Integer} array.
   */
  public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
  /**
   * An empty immutable {@code short} array.
   */
  public static final short[] EMPTY_SHORT_ARRAY = new short[0];
  /**
   * An empty immutable {@code Short} array.
   */
  public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
  /**
   * An empty immutable {@code byte} array.
   */
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  /**
   * An empty immutable {@code Byte} array.
   */
  public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
  /**
   * An empty immutable {@code double} array.
   */
  public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
  /**
   * An empty immutable {@code Double} array.
   */
  public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
  /**
   * An empty immutable {@code float} array.
   */
  public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
  /**
   * An empty immutable {@code Float} array.
   */
  public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
  /**
   * An empty immutable {@code boolean} array.
   */
  public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
  /**
   * An empty immutable {@code Boolean} array.
   */
  public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
  /**
   * An empty immutable {@code char} array.
   */
  public static final char[] EMPTY_CHAR_ARRAY = new char[0];
  /**
   * An empty immutable {@code Character} array.
   */
  public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];

  /**
   * The index value when an element is not found in a list or array: {@code -1}. This value is
   * returned by methods in this class and can also be used in comparisons with values returned by
   * various method from {@link java.util.List}.
   */
  public static final int INDEX_NOT_FOUND = -1;

  /**
   * @author: Ares
   * @description: 判断数组是否为空
   * @description: Determine array is empty
   * @time: 2022-06-08 11:00:23
   * @params: [array] 数组
   * @return: boolean 是否为空
   */
  public static boolean isEmpty(Object[] array) {
    return null == array || array.length == 0;
  }

  /**
   * @author: Ares
   * @description: 判断数组是否非空
   * @description: Determine array is not empty
   * @time: 2022-06-08 11:00:23
   * @params: [array] 数组
   * @return: boolean 是否非空
   */
  public static boolean isNotEmpty(Object[] array) {
    return !isEmpty(array);
  }

  /**
   * @author: Ares
   * @description: 数组形式转换
   * @description: Array form convert
   * @time: 2022-06-08 11:03:33
   * @params: [elements] 数组
   * @return: T[] out 出参
   */
  @SafeVarargs
  public static <T> T[] asArray(T... elements) {
    return elements;
  }

  /**
   * @author: Ares
   * @description: 数组是否包含搜索元素
   * @description: Array contains element
   * @time: 2022-06-08 11:04:19
   * @params: [array, searchObject] 数组，搜索元素
   * @return: boolean 是否包含
   */
  public static boolean contains(final Object[] array, final Object searchObject) {
    return indexOf(array, searchObject) != INDEX_NOT_FOUND;
  }

  /**
   * @author: Ares
   * @description: 搜索数组中指定元素的下标
   * @description: Search the subscript of the specified element in the array
   * @time: 2022-06-08 11:05:28
   * @params: [array, searchObject] 数组，搜索元素
   * @return: int 下标
   */
  public static int indexOf(final Object[] array, final Object searchObject) {
    return indexOf(array, searchObject, 0);
  }

  /**
   * @author: Ares
   * @description: 从起始下标开始搜索数组中指定元素的下标
   * @description: Search the index of the specified element in the array starting from the starting
   * index
   * @time: 2022-06-08 11:05:28
   * @params: [array, searchObject, startIndex] 数组，搜索元素，起始下标
   * @return: int 下标
   */
  public static int indexOf(final Object[] array, final Object searchObject, int startIndex) {
    if (null == array) {
      return INDEX_NOT_FOUND;
    }
    if (startIndex < 0) {
      startIndex = 0;
    }
    if (searchObject == null) {
      for (int i = startIndex; i < array.length; i++) {
        if (array[i] == null) {
          return i;
        }
      }
    } else {
      for (int i = startIndex; i < array.length; i++) {
        if (searchObject.equals(array[i])) {
          return i;
        }
      }
    }
    return INDEX_NOT_FOUND;
  }

  /**
   * <p>Returns the length of the specified array.
   * This method can deal with {@code Object} arrays and with primitive arrays.
   *
   * <p>If the input array is {@code null}, {@code 0} is returned.
   *
   * <pre>
   * ArrayUtils.getLength(null)            = 0
   * ArrayUtils.getLength([])              = 0
   * ArrayUtils.getLength([null])          = 1
   * ArrayUtils.getLength([true, false])   = 2
   * ArrayUtils.getLength([1, 2, 3])       = 3
   * ArrayUtils.getLength(["a", "b", "c"]) = 3
   * </pre>
   *
   * @param array the array to retrieve the length from, may be null
   * @return The length of the array, or {@code 0} if the array is {@code null}
   * @throws IllegalArgumentException if the object argument is not an array.
   */
  public static int getLength(final Object array) {
    if (array == null) {
      return 0;
    }
    return Array.getLength(array);
  }

}
