package cn.ares.boot.util.common.throwable;

/**
 * @author: Ares
 * @time: 2023-12-24 13:22:49
 * @description: 没有堆栈的异常（运行时异常）
 * @description: Stackless exception(runtime exception)
 * @version: JDK 1.8
 */
public class StacklessException extends RuntimeException {

  private static final long serialVersionUID = -5613987125025778083L;

  public StacklessException() {
  }

  public StacklessException(String message) {
    super(message);
  }

  public StacklessException(String message, Throwable cause) {
    super(message, cause);
  }

  public StacklessException(Throwable cause) {
    super(cause);
  }

  public StacklessException(String message, Throwable cause, boolean enableSuppression) {
    super(message, cause, enableSuppression, false);
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }

}
