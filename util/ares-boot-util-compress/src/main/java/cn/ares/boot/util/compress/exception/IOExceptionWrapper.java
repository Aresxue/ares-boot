package cn.ares.boot.util.compress.exception;

/**
 * @author: Ares
 * @time: 2024-09-18 16:37:48
 * @description: IO异常包装
 * @description: IO exception wrapper
 * @version: JDK 1.8
 */
public class IOExceptionWrapper extends RuntimeException {

  private static final long serialVersionUID = -6434026878946679842L;

  public IOExceptionWrapper() {
  }

  public IOExceptionWrapper(String message) {
    super(message);
  }

  public IOExceptionWrapper(String message, Throwable cause) {
    super(message, cause);
  }

  public IOExceptionWrapper(Throwable cause) {
    super(cause);
  }

}
