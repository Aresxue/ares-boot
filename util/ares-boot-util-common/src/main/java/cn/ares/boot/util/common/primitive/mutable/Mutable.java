package cn.ares.boot.util.common.primitive.mutable;

/**
 * @param <T> the type to set and get
 * @author: Ares
 * @time: 2021-12-27 18:45:31
 * @description: Provides mutable access to a value. A typical use case would be to enable a
 * primitive or string to be passed to a method and allow that method to effectively change the
 * value of the primitive/string. Another use case is to store a frequently changing primitive in a
 * collection (for example a total in a map) without needing to create new Integer/Long wrapper
 * objects.
 * @version: JDK 1.8
 */
public interface Mutable<T> {

  /**
   * Gets the value of this mutable.
   *
   * @return the stored value
   */
  T getValue();

  /**
   * Sets the value of this mutable.
   *
   * @param value the value to store
   * @throws NullPointerException if the object is null and null is invalid
   * @throws ClassCastException   if the type is invalid
   */
  void setValue(T value);

}

