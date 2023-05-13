package cn.ares.boot.util.common.primitive.mutable;

/**
 * @author: Ares
 * @time: 2022-06-08 10:41:26
 * @description: A mutable {@code double} wrapper. Note that as MutableDouble does not extend
 * Double, it is not treated by String.format() as a Double parameter.
 * @version: JDK 1.8
 */
public class MutableDouble extends Number implements Comparable<MutableDouble>, Mutable<Number>,
    Cloneable {

  /**
   * Required for serialization support.
   *
   * @see java.io.Serializable
   */
  private static final long serialVersionUID = 5475507096872220528L;

  /**
   * The mutable value.
   */
  private double value;

  /**
   * Constructs a new MutableDouble with the default value of zero.
   */
  public MutableDouble() {
  }

  /**
   * Constructs a new MutableDouble with the specified value.
   *
   * @param value the initial value to store
   */
  public MutableDouble(final double value) {
    this.value = value;
  }

  /**
   * Constructs a new MutableDouble with the specified value.
   *
   * @param value the initial value to store, not null
   * @throws NullPointerException if the object is null
   */
  public MutableDouble(final Number value) {
    this.value = value.doubleValue();
  }

  /**
   * Constructs a new MutableDouble parsing the given string.
   *
   * @param value the string to parse, not null
   * @throws NumberFormatException if the string cannot be parsed into a double
   */
  public MutableDouble(final String value) {
    this.value = Double.parseDouble(value);
  }

  //-----------------------------------------------------------------------

  /**
   * Gets the value as a Double instance.
   *
   * @return the value as a Double, never null
   */
  @Override
  public Double getValue() {
    return Double.valueOf(this.value);
  }

  /**
   * Sets the value.
   *
   * @param value the value to set
   */
  public void setValue(final double value) {
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
    this.value = value.doubleValue();
  }

  //-----------------------------------------------------------------------

  /**
   * Checks whether the double value is the special NaN value.
   *
   * @return true if NaN
   */
  public boolean isNaN() {
    return Double.isNaN(value);
  }

  /**
   * Checks whether the double value is infinite.
   *
   * @return true if infinite
   */
  public boolean isInfinite() {
    return Double.isInfinite(value);
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
  public double getAndIncrement() {
    final double last = value;
    value++;
    return last;
  }

  /**
   * Increments this instance's value by 1; this method returns the value associated with the
   * instance immediately after the increment operation. This method is not thread safe.
   *
   * @return the value associated with the instance after it is incremented
   */
  public double incrementAndGet() {
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
  public double getAndDecrement() {
    final double last = value;
    value--;
    return last;
  }

  /**
   * Decrements this instance's value by 1; this method returns the value associated with the
   * instance immediately after the decrement operation. This method is not thread safe.
   *
   * @return the value associated with the instance after it is decremented
   */
  public double decrementAndGet() {
    value--;
    return value;
  }

  //-----------------------------------------------------------------------

  /**
   * Adds a value to the value of this instance.
   *
   * @param operand the value to add
   */
  public void add(final double operand) {
    this.value += operand;
  }

  /**
   * Adds a value to the value of this instance.
   *
   * @param operand the value to add, not null
   * @throws NullPointerException if the object is null
   */
  public void add(final Number operand) {
    this.value += operand.doubleValue();
  }

  /**
   * Subtracts a value from the value of this instance.
   *
   * @param operand the value to subtract, not null
   */
  public void subtract(final double operand) {
    this.value -= operand;
  }

  /**
   * Subtracts a value from the value of this instance.
   *
   * @param operand the value to subtract, not null
   * @throws NullPointerException if the object is null
   */
  public void subtract(final Number operand) {
    this.value -= operand.doubleValue();
  }

  /**
   * Increments this instance's value by {@code operand}; this method returns the value associated
   * with the instance immediately after the addition operation. This method is not thread safe.
   *
   * @param operand the quantity to add, not null
   * @return the value associated with this instance after adding the operand
   */
  public double addAndGet(final double operand) {
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
  public double addAndGet(final Number operand) {
    this.value += operand.doubleValue();
    return value;
  }

  /**
   * Increments this instance's value by {@code operand}; this method returns the value associated
   * with the instance immediately prior to the addition operation. This method is not thread safe.
   *
   * @param operand the quantity to add, not null
   * @return the value associated with this instance immediately before the operand was added
   */
  public double getAndAdd(final double operand) {
    final double last = value;
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
  public double getAndAdd(final Number operand) {
    final double last = value;
    this.value += operand.doubleValue();
    return last;
  }

  //-----------------------------------------------------------------------
  // shortValue and byteValue rely on Number implementation

  /**
   * Returns the value of this MutableDouble as an int.
   *
   * @return the numeric value represented by this object after conversion to type int.
   */
  @Override
  public int intValue() {
    return (int) value;
  }

  /**
   * Returns the value of this MutableDouble as a long.
   *
   * @return the numeric value represented by this object after conversion to type long.
   */
  @Override
  public long longValue() {
    return (long) value;
  }

  /**
   * Returns the value of this MutableDouble as a float.
   *
   * @return the numeric value represented by this object after conversion to type float.
   */
  @Override
  public float floatValue() {
    return (float) value;
  }

  /**
   * Returns the value of this MutableDouble as a double.
   *
   * @return the numeric value represented by this object after conversion to type double.
   */
  @Override
  public double doubleValue() {
    return value;
  }

  //-----------------------------------------------------------------------

  /**
   * Gets this mutable as an instance of Double.
   *
   * @return a Double instance containing the value from this mutable, never null
   */
  public Double toDouble() {
    return doubleValue();
  }

  //-----------------------------------------------------------------------

  /**
   * Compares this object against the specified object. The result is {@code true} if and only if
   * the argument is not {@code null} and is a {@code Double} object that represents a double that
   * has the identical bit pattern to the bit pattern of the double represented by this object. For
   * this purpose, two {@code double} values are considered to be the same if and only if the method
   * {@link Double#doubleToLongBits(double)}returns the same long value when applied to each.
   * <p>
   * Note that in most cases, for two instances of class {@code Double},{@code d1} and {@code d2},
   * the value of {@code d1.equals(d2)} is {@code true} if and only if <blockquote>
   *
   * <pre>
   *   d1.doubleValue()&nbsp;== d2.doubleValue()
   * </pre>
   *
   * </blockquote>
   * <p>
   * also has the value {@code true}. However, there are two exceptions:
   * <ul>
   * <li>If {@code d1} and {@code d2} both represent {@code Double.NaN}, then the
   * {@code equals} method returns {@code true}, even though {@code Double.NaN==Double.NaN} has
   * the value {@code false}.
   * <li>If {@code d1} represents {@code +0.0} while {@code d2} represents {@code -0.0},
   * or vice versa, the {@code equal} test has the value {@code false}, even though
   * {@code +0.0==-0.0} has the value {@code true}. This allows hashtable to operate properly.
   * </ul>
   *
   * @param obj the object to compare with, null returns false
   * @return {@code true} if the objects are the same; {@code false} otherwise.
   */
  @Override
  public boolean equals(final Object obj) {
    return obj instanceof MutableDouble
        && Double.doubleToLongBits(((MutableDouble) obj).value) == Double.doubleToLongBits(value);
  }

  /**
   * Returns a suitable hash code for this mutable.
   * @return a suitable hash code
   */
  @Override
  public int hashCode() {
    final long bits = Double.doubleToLongBits(value);
    return (int) (bits ^ bits >>> 32);
  }

  //-----------------------------------------------------------------------

  /**
   * Compares this mutable to another in ascending order.
   *
   * @param other the other mutable to compare to, not null
   * @return negative if this is less, zero if equal, positive if greater
   */
  @Override
  public int compareTo(final MutableDouble other) {
    return Double.compare(this.value, other.value);
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
  public MutableDouble clone() {
    return new MutableDouble(this.value);
  }
}
