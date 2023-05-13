package cn.ares.boot.util.common.primitive.mutable;

import cn.ares.boot.util.common.NumberUtil;

/**
 * @author: Ares
 * @time: 2022-06-08 10:55:31
 * @description: A mutable {@code short} wrapper. Note that as MutableShort does not extend Short,
 * it is not treated by String.format() as a Short parameter.
 * @version: JDK 1.8
 */
public class MutableShort extends Number implements Comparable<MutableShort>, Mutable<Number>,
    Cloneable {

  /**
   * Required for serialization support.
   *
   * @see java.io.Serializable
   */
  private static final long serialVersionUID = -7287569864797248342L;

  /**
   * The mutable value.
   */
  private short value;

  /**
   * Constructs a new MutableShort with the default value of zero.
   */
  public MutableShort() {
  }

  /**
   * Constructs a new MutableShort with the specified value.
   *
   * @param value the initial value to store
   */
  public MutableShort(final short value) {
    this.value = value;
  }

  /**
   * Constructs a new MutableShort with the specified value.
   *
   * @param value the initial value to store, not null
   * @throws NullPointerException if the object is null
   */
  public MutableShort(final Number value) {
    this.value = value.shortValue();
  }

  /**
   * Constructs a new MutableShort parsing the given string.
   *
   * @param value the string to parse, not null
   * @throws NumberFormatException if the string cannot be parsed into a short
   */
  public MutableShort(final String value) {
    this.value = Short.parseShort(value);
  }

  //-----------------------------------------------------------------------

  /**
   * Gets the value as a Short instance.
   *
   * @return the value as a Short, never null
   */
  @Override
  public Short getValue() {
    return this.value;
  }

  /**
   * Sets the value.
   *
   * @param value the value to set
   */
  public void setValue(final short value) {
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
    this.value = value.shortValue();
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
  public short getAndIncrement() {
    final short last = value;
    value++;
    return last;
  }

  /**
   * Increments this instance's value by 1; this method returns the value associated with the
   * instance immediately after the increment operation. This method is not thread safe.
   *
   * @return the value associated with the instance after it is incremented
   */
  public short incrementAndGet() {
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
  public short getAndDecrement() {
    final short last = value;
    value--;
    return last;
  }

  /**
   * Decrements this instance's value by 1; this method returns the value associated with the
   * instance immediately after the decrement operation. This method is not thread safe.
   *
   * @return the value associated with the instance after it is decremented
   */
  public short decrementAndGet() {
    value--;
    return value;
  }

  //-----------------------------------------------------------------------

  /**
   * Adds a value to the value of this instance.
   *
   * @param operand the value to add, not null
   */
  public void add(final short operand) {
    this.value += operand;
  }

  /**
   * Adds a value to the value of this instance.
   *
   * @param operand the value to add, not null
   * @throws NullPointerException if the object is null
   */
  public void add(final Number operand) {
    this.value += operand.shortValue();
  }

  /**
   * Subtracts a value from the value of this instance.
   *
   * @param operand the value to subtract, not null
   */
  public void subtract(final short operand) {
    this.value -= operand;
  }

  /**
   * Subtracts a value from the value of this instance.
   *
   * @param operand the value to subtract, not null
   * @throws NullPointerException if the object is null
   */
  public void subtract(final Number operand) {
    this.value -= operand.shortValue();
  }

  /**
   * Increments this instance's value by {@code operand}; this method returns the value associated
   * with the instance immediately after the addition operation. This method is not thread safe.
   *
   * @param operand the quantity to add, not null
   * @return the value associated with this instance after adding the operand
   */
  public short addAndGet(final short operand) {
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
  public short addAndGet(final Number operand) {
    this.value += operand.shortValue();
    return value;
  }

  /**
   * Increments this instance's value by {@code operand}; this method returns the value associated
   * with the instance immediately prior to the addition operation. This method is not thread safe.
   *
   * @param operand the quantity to add, not null
   * @return the value associated with this instance immediately before the operand was added
   */
  public short getAndAdd(final short operand) {
    final short last = value;
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
  public short getAndAdd(final Number operand) {
    final short last = value;
    this.value += operand.shortValue();
    return last;
  }

  //-----------------------------------------------------------------------
  // byteValue relies on Number implementation

  /**
   * Returns the value of this MutableShort as a short.
   *
   * @return the numeric value represented by this object after conversion to type short.
   */
  @Override
  public short shortValue() {
    return value;
  }

  /**
   * Returns the value of this MutableShort as an int.
   *
   * @return the numeric value represented by this object after conversion to type int.
   */
  @Override
  public int intValue() {
    return value;
  }

  /**
   * Returns the value of this MutableShort as a long.
   *
   * @return the numeric value represented by this object after conversion to type long.
   */
  @Override
  public long longValue() {
    return value;
  }

  /**
   * Returns the value of this MutableShort as a float.
   *
   * @return the numeric value represented by this object after conversion to type float.
   */
  @Override
  public float floatValue() {
    return value;
  }

  /**
   * Returns the value of this MutableShort as a double.
   *
   * @return the numeric value represented by this object after conversion to type double.
   */
  @Override
  public double doubleValue() {
    return value;
  }

  //-----------------------------------------------------------------------

  /**
   * Gets this mutable as an instance of Short.
   *
   * @return a Short instance containing the value from this mutable, never null
   */
  public Short toShort() {
    return shortValue();
  }

  //-----------------------------------------------------------------------

  /**
   * Compares this object to the specified object. The result is {@code true} if and only if the
   * argument is not {@code null} and is a {@code MutableShort} object that contains the same {@code
   * short} value as this object.
   *
   * @param obj the object to compare with, null returns false
   * @return {@code true} if the objects are the same; {@code false} otherwise.
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof MutableShort) {
      return value == ((MutableShort) obj).shortValue();
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
  public int compareTo(final MutableShort other) {
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
  public MutableShort clone() {
    return new MutableShort(this.value);
  }
}

