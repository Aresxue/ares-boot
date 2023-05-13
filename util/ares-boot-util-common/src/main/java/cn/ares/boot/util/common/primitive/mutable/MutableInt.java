package cn.ares.boot.util.common.primitive.mutable;


/**
 * @author: Ares
 * @time: 2021-12-27 18:41:43
 * @description: mutable int
 * @version: JDK 1.8
 */
public class MutableInt extends Number implements Comparable<MutableInt>, Mutable<Number>,
    Cloneable {

  private static final long serialVersionUID = 8347796710252350916L;

  private int value;

  public MutableInt() {
  }

  public MutableInt(final int value) {
    this.value = value;
  }

  public MutableInt(final Number value) {
    this.value = value.intValue();
  }


  public MutableInt(final String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public Integer getValue() {
    return this.value;
  }

  /**
   * Sets the value.
   *
   * @param value the value to set
   */
  public void setValue(final int value) {
    this.value = value;
  }


  @Override
  public void setValue(final Number value) {
    this.value = value.intValue();
  }


  public void increment() {
    value++;
  }


  public int getAndIncrement() {
    final int last = value;
    value++;
    return last;
  }


  public int incrementAndGet() {
    value++;
    return value;
  }


  public void decrement() {
    value--;
  }


  public int getAndDecrement() {
    final int last = value;
    value--;
    return last;
  }


  public int decrementAndGet() {
    value--;
    return value;
  }

  public void add(final int operand) {
    this.value += operand;
  }


  public void add(final Number operand) {
    this.value += operand.intValue();
  }


  public void subtract(final int operand) {
    this.value -= operand;
  }


  public void subtract(final Number operand) {
    this.value -= operand.intValue();
  }


  public int addAndGet(final int operand) {
    this.value += operand;
    return value;
  }


  public int addAndGet(final Number operand) {
    this.value += operand.intValue();
    return value;
  }

  public int getAndAdd(final int operand) {
    final int last = value;
    this.value += operand;
    return last;
  }

  public int getAndAdd(final Number operand) {
    final int last = value;
    this.value += operand.intValue();
    return last;
  }

  @Override
  public int intValue() {
    return value;
  }

  @Override
  public long longValue() {
    return value;
  }

  @Override
  public float floatValue() {
    return value;
  }


  @Override
  public double doubleValue() {
    return value;
  }

  public Integer toInteger() {
    return intValue();
  }


  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof MutableInt) {
      return value == ((MutableInt) obj).intValue();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return value;
  }

  @Override
  public int compareTo(final MutableInt other) {
    if (this.value == other.value) {
      return 0;
    }
    return this.value < other.value ? -1 : 1;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public MutableInt clone() {
    return new MutableInt(this.value);
  }

}
