package cn.ares.boot.util.common.throwable;

/**
 * @author: Ares
 * @time: 2022-08-17 15:21:21
 * @description: 未知异常
 * @description: Unknown exception
 * @version: JDK 1.8
 */
public class UnknownException extends RuntimeException {

  private static final long serialVersionUID = -7655513487870988265L;

  public UnknownException(String message) {
    super(message);
  }

  public UnknownException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnknownException() {
    super();
  }

  public UnknownException(Throwable cause) {
    super(cause);
  }

}
