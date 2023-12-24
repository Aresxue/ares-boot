package cn.ares.boot.util.common.throwable;

/**
 * @author: Ares
 * @time: 2023-12-24 13:12:47
 * @description: 没有堆栈的抛出
 * @description: Stackless throwable
 * @version: JDK 1.8
 */
public class StacklessThrowable extends Throwable {

  private static final long serialVersionUID = -3903953052131925722L;

  public StacklessThrowable() {
  }

  public StacklessThrowable(String message) {
    super(message);
  }

  public StacklessThrowable(String message, Throwable cause) {
    super(message, cause);
  }

  public StacklessThrowable(Throwable cause) {
    super(cause);
  }

  public StacklessThrowable(String message, Throwable cause, boolean enableSuppression) {
    super(message, cause, enableSuppression, false);
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

}
