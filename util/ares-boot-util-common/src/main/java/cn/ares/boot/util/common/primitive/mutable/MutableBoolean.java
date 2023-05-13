package cn.ares.boot.util.common.primitive.mutable;

import cn.ares.boot.util.common.primitive.BooleanUtil;
import java.io.Serializable;

/**
 * @author: Ares
 * @time: 2022-06-08 10:17:22
 * @description: A mutable {@code boolean} wrapper. Note that as MutableBoolean does not extend
 * Boolean, it is not treated by String.format() as a Boolean parameter.
 * @version: JDK 1.8
 */
public class MutableBoolean implements Mutable<Boolean>, Comparable<MutableBoolean>, Serializable,
    Cloneable {

  /**
   * Required for serialization support.
   *
   * @see java.io.Serializable
   */
  private static final long serialVersionUID = 8748555473950486620L;

  /**
   * The mutable value.
   */
  private boolean value;

  /**
   * Constructs a new MutableBoolean with the default value of false.
   */
  public MutableBoolean() {
  }

  /**
   * Constructs a new MutableBoolean with the specified value.
   *
   * @param value the initial value to store
   */
  public MutableBoolean(final boolean value) {
    this.value = value;
  }

  /**
   * Constructs a new MutableBoolean with the specified value.
   *
   * @param value the initial value to store, not null
   * @throws NullPointerException if the object is null
   */
  public MutableBoolean(final Boolean value) {
    this.value = value;
  }

  //-----------------------------------------------------------------------

  /**
   * Gets the value as a Boolean instance.
   *
   * @return the value as a Boolean, never null
   */
  @Override
  public Boolean getValue() {
    return this.value;
  }

  /**
   * Sets the value.
   *
   * @param value the value to set
   */
  public void setValue(final boolean value) {
    this.value = value;
  }

  /**
   * Sets the value to false.
   */
  public void setFalse() {
    this.value = false;
  }

  /**
   * Sets the value to true.
   */
  public void setTrue() {
    this.value = true;
  }

  /**
   * Sets the value from any Boolean instance.
   *
   * @param value the value to set, not null
   * @throws NullPointerException if the object is null
   */
  @Override
  public void setValue(final Boolean value) {
    this.value = value;
  }

  //-----------------------------------------------------------------------

  /**
   * Checks if the current value is {@code true}.
   *
   * @return {@code true} if the current value is {@code true}
   */
  public boolean isTrue() {
    return value;
  }

  /**
   * Checks if the current value is {@code false}.
   *
   * @return {@code true} if the current value is {@code false}
   */
  public boolean isFalse() {
    return !value;
  }

  //-----------------------------------------------------------------------

  /**
   * Returns the value of this MutableBoolean as a boolean.
   *
   * @return the boolean value represented by this object.
   */
  public boolean booleanValue() {
    return value;
  }

  //-----------------------------------------------------------------------

  /**
   * Gets this mutable as an instance of Boolean.
   *
   * @return a Boolean instance containing the value from this mutable, never null
   */
  public Boolean toBoolean() {
    return booleanValue();
  }

  //-----------------------------------------------------------------------

  /**
   * Compares this object to the specified object. The result is {@code true} if and only if the
   * argument is not {@code null} and is an {@code MutableBoolean} object that contains the same
   * {@code boolean} value as this object.
   *
   * @param obj the object to compare with, null returns false
   * @return {@code true} if the objects are the same; {@code false} otherwise.
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof MutableBoolean) {
      return value == ((MutableBoolean) obj).booleanValue();
    }
    return false;
  }

  /**
   * Returns a suitable hash code for this mutable.
   *
   * @return the hash code returned by {@code Boolean.TRUE} or {@code Boolean.FALSE}
   */
  @Override
  public int hashCode() {
    return value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
  }

  //-----------------------------------------------------------------------

  /**
   * Compares this mutable to another in ascending order.
   *
   * @param other the other mutable to compare to, not null
   * @return negative if this is less, zero if equal, positive if greater where false is less than
   * true
   */
  @Override
  public int compareTo(final MutableBoolean other) {
    return BooleanUtil.compare(this.value, other.value);
  }

  //-----------------------------------------------------------------------

  /**
   * Returns the String value of this mutable.
   *
   * @return the mutable value as a string
   */
  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public MutableBoolean clone() {
    return new MutableBoolean(this.value);
  }
}
