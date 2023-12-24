package cn.ares.boot.util.common.throwable;

/**
 * @author: Ares
 * @time: 2023-12-14 11:59:37
 * @description: 编译时异常包装
 * @description: Checked exception wrapper
 * @version: JDK 1.8
 */
public class CheckedExceptionWrapper extends RuntimeException {

  private static final long serialVersionUID = -8092405346958033084L;

  public CheckedExceptionWrapper() {
  }

  public CheckedExceptionWrapper(Throwable cause) {
    super(cause);
  }

  public CheckedExceptionWrapper(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public CheckedExceptionWrapper(String message) {
    super(message);
  }

  public CheckedExceptionWrapper(String message, Throwable cause) {
    super(message, cause);
  }

  public Throwable unwrap() {
    return getCause();
  }

}
