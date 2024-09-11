package cn.ares.boot.util.common.structure;

/**
 * @author: Ares
 * @time: 2024-09-11 22:03:35
 * @description: 四个对象的元组
 * @description: Tuple for four objects
 * @version: JDK 1.8
 */
public class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {

  private static final FourTuple EMPTY = new FourTuple<>();

  public D four;

  public FourTuple() {
  }

  public static <A, B, C, D> FourTuple<A, B, C, D> emptyFourTuple() {
    return EMPTY;
  }

  public static <A, B, C, D> FourTuple<A, B, C, D> of(A first, B second, C third, D four) {
    FourTuple<A, B, C, D> tuple = new FourTuple<>();
    tuple.setFirst(first);
    tuple.setSecond(second);
    tuple.setThird(third);
    tuple.setFour(four);
    return tuple;
  }

  public D getFour() {
    return four;
  }

  public void setFour(D four) {
    this.four = four;
  }

}
