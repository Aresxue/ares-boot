package cn.ares.boot.util.common.structure;

/**
 * @author: Ares
 * @time: 2024-09-11 22:03:35
 * @description: 三个对象的元组
 * @description: Tuple for three objects
 * @version: JDK 1.8
 */
public class ThreeTuple<A, B, C> extends Tuple<A, B> {

  private static final ThreeTuple EMPTY = new ThreeTuple<>();

  public C third;

  public ThreeTuple() {
  }

  public static <A, B, C> ThreeTuple<A, B, C> emptyThirdTuple() {
    return EMPTY;
  }

  public static <A, B, C> ThreeTuple<A, B, C> of(A first, B second, C third) {
    ThreeTuple<A, B, C> tuple = new ThreeTuple<>();
    tuple.setFirst(first);
    tuple.setSecond(second);
    tuple.setThird(third);
    return tuple;
  }

  public C getThird() {
    return third;
  }

  public void setThird(C third) {
    this.third = third;
  }

}
