package cn.ares.boot.util.common.structure;

/**
 * @author: Ares
 * @time: 2021-07-29 10:49:00
 * @description: Tuple
 * @version: JDK 1.8
 */
public class Tuple<A, B> {

  private static final Tuple EMPTY = new Tuple<>();

  private A first;
  private B second;

  private Tuple() {
  }

  public static <A, B> Tuple<A, B> empty() {
    return EMPTY;
  }

  public static <A, B> Tuple<A, B> of(A first, B second) {
    Tuple<A, B> tuple = new Tuple<>();
    tuple.setFirst(first);
    tuple.setSecond(second);
    return tuple;
  }

  public A getFirst() {
    return first;
  }

  public void setFirst(A first) {
    this.first = first;
  }

  public B getSecond() {
    return second;
  }

  public void setSecond(B second) {
    this.second = second;
  }
}