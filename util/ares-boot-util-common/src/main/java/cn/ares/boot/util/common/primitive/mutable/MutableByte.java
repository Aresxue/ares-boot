package cn.ares.boot.util.common.primitive.mutable;

import cn.ares.boot.util.common.NumberUtil;

/**
 * @author: Ares
 * @time: 2021-12-27 21:42:42
 * @description: A mutable {@code byte} wrapper. Note that as MutableByte does not extend Byte, it
 * is not treated by String.format() as a Byte parameter.
 * @version: JDK 1.8
 */
public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number>,
    Cloneable {

  private static final long serialVersionUID = -7846379905346861323L;

  /**
   * The mutable value.
   */
  private byte value;

  /**
   * Constructs a new MutableByte with the default value of zero.
   */
  public MutableByte() {
  }

  /**
   * Constructs a new MutableByte with the specified value.
   *
   * @param value the initial value to store
   */
  public MutableByte(final byte value) {
    this.value = value;
  }

  /**
   * Constructs a new MutableByte with the specified value.
   *
   * @param value the initial value to store, not null
   * @throws NullPointerException if the object is null
   */
  public MutableByte(final Number value) {
    this.value = value.byteValue();
  }

  /**
   * Constructs a new MutableByte parsing the given string.
   *
   * @param value the string to parse, not null
   * @throws NumberFormatException if the string cannot be parsed into a byte
   */
  public MutableByte(final String value) {
    this.value = Byte.parseByte(value);
  }

  //-----------------------------------------------------------------------

  /**
   * Gets the value as a Byte instance.
   *
   * @return the value as a Byte, never null
   */
  @Override
  public Byte getValue() {
    return this.value;
  }

  /**
   * Sets the value.
   *
   * @param value the value to set
   */
  public void setValue(final byte value) {
    this.value = value;
  }

  /**
   * Sets the value from any Number instance.
   *
   * @param value the value to set, not null
   * @throws NullPointerException if the object is null
   */
  @Override
  public void setValue(final Number value) {
    this.value = value.byteValue();
  }

  //-----------------------------------------------------------------------

  /**
   * Increments the value.
   */
  public void increment() {
    value++;
  }

  /**
   * Increments this instance's value by 1; this method returns the value associated with the
   * instance immediately prior to the increment operation. This method is not thread safe.
   *
   * @return the value associated with the instance before it was incremented
   */
  public byte getAndIncrement() {
    final byte last = value;
    value++;
    return last;
  }

  /**
   * Increments this instance's value by 1; this method returns the value associated with the
   * instance immediately after the increment operation. This method is not thread safe.
   *
   * @return the value associated with the instance after it is incremented
   */
  public byte incrementAndGet() {
    value++;
    return value;
  }

  /**
   * Decrements the value.
   */
  public void decrement() {
    value--;
  }

  /**
   * Decrements this instance's value by 1; this method returns the value associated with the
   * instance immediately prior to the decrement operation. This method is not thread safe.
   *
   * @return the value associated with the instance before it was decremented
   */
  public byte getAndDecrement() {
    final byte last = value;
    value--;
    return last;
  }

  /**
   * Decrements this instance's value by 1; this method returns the value associated with the
   * instance immediately after the decrement operation. This method is not thread safe.
   *
   * @return the value associated with the instance after it is decremented
   */
  public byte decrementAndGet() {
    value--;
    return value;
  }

  //-----------------------------------------------------------------------

  /**
   * Adds a value to the value of this instance.
   *
   * @param operand the value to add, not null
   */
  public void add(final byte operand) {
    this.value += operand;
  }

  /**
   * Adds a value to the value of this instance.
   *
   * @param operand the value to add, not null
   * @throws NullPointerException if the object is null
   */
  public void add(final Number operand) {
    this.value += operand.byteValue();
  }

  /**
   * Subtracts a value from the value of this instance.
   *
   * @param operand the value to subtract, not null
   */
  public void subtract(final byte operand) {
    this.value -= operand;
  }

  /**
   * Subtracts a value from the value of this instance.
   *
   * @param operand the value to subtract, not null
   * @throws NullPointerException if the object is null
   */
  public void subtract(final Number operand) {
    this.value -= operand.byteValue();
  }

  /**
   * Increments this instance's value by {@code operand}; this method returns the value associated
   * with the instance immediately after the addition operation. This method is not thread safe.
   *
   * @param operand the quantity to add, not null
   * @return the value associated with this instance after adding the operand
   */
  public byte addAndGet(final byte operand) {
    this.value += operand;
    return value;
  }

  /**
   * Increments this instance's value by {@code operand}; this method returns the value associated
   * with the instance immediately after the addition operation. This method is not thread safe.
   *
   * @param operand the quantity to add, not null
   * @return the value associated with this instance after adding the operand
   * @throws NullPointerException if {@code operand} is null
   */
  public byte addAndGet(final Number operand) {
    this.value += operand.byteValue();
    return value;
  }

  /**
   * Increments this instance's value by {@code operand}; this method returns the value associated
   * with the instance immediately prior to the addition operation. This method is not thread safe.
   *
   * @param operand the quantity to add, not null
   * @return the value associated with this instance immediately before the operand was added
   */
  public byte getAndAdd(final byte operand) {
    final byte last = value;
    this.value += operand;
    return last;
  }

  /**
   * Increments this instance's value by {@code operand}; this method returns the value associated
   * with the instance immediately prior to the addition operation. This method is not thread safe.
   *
   * @param operand the quantity to add, not null
   * @return the value associated with this instance immediately before the operand was added
   * @throws NullPointerException if {@code operand} is null
   */
  public byte getAndAdd(final Number operand) {
    final byte last = value;
    this.value += operand.byteValue();
    return last;
  }

  //-----------------------------------------------------------------------
  // shortValue relies on Number implementation

  /**
   * Returns the value of this MutableByte as a byte.
   *
   * @return the numeric value represented by this object after conversion to type byte.
   */
  @Override
  public byte byteValue() {
    return value;
  }

  /**
   * Returns the value of this MutableByte as an int.
   *
   * @return the numeric value represented by this object after conversion to type int.
   */
  @Override
  public int intValue() {
    return value;
  }

  /**
   * Returns the value of this MutableByte as a long.
   *
   * @return the numeric value represented by this object after conversion to type long.
   */
  @Override
  public long longValue() {
    return value;
  }

  /**
   * Returns the value of this MutableByte as a float.
   *
   * @return the numeric value represented by this object after conversion to type float.
   */
  @Override
  public float floatValue() {
    return value;
  }

  /**
   * Returns the value of this MutableByte as a double.
   *
   * @return the numeric value represented by this object after conversion to type double.
   */
  @Override
  public double doubleValue() {
    return value;
  }

  //-----------------------------------------------------------------------

  /**
   * Gets this mutable as an instance of Byte.
   *
   * @return a Byte instance containing the value from this mutable
   */
  public Byte toByte() {
    return byteValue();
  }

  //-----------------------------------------------------------------------

  /**
   * Compares this object to the specified object. The result is {@code true} if and only if the
   * argument is not {@code null} and is a {@code MutableByte} object that contains the same {@code
   * byte} value as this object.
   *
   * @param obj the object to compare with, null returns false
   * @return {@code true} if the objects are the same; {@code false} otherwise.
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof MutableByte) {
      return value == ((MutableByte) obj).byteValue();
    }
    return false;
  }

  /**
   * Returns a suitable hash code for this mutable.
   *
   * @return a suitable hash code
   */
  @Override
  public int hashCode() {
    return value;
  }

  //-----------------------------------------------------------------------

  /**
   * Compares this mutable to another in ascending order.
   *
   * @param other the other mutable to compare to, not null
   * @return negative if this is less, zero if equal, positive if greater
   */
  @Override
  public int compareTo(final MutableByte other) {
    return NumberUtil.compare(this.value, other.value);
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
  public MutableByte clone() {
    return new MutableByte(value);
  }

}
