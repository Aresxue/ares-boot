package cn.ares.boot.util.common.throwable;

import cn.ares.boot.util.common.throwable.StacklessException;

/**
 * @author: Ares
 * @time: 2023-12-24 13:27:52
 * @description: 带状态的无堆栈异常
 * @description: No stack exception with status
 * @version: JDK 1.8
 */
public class StatusStacklessException extends StacklessException {

  private static final long serialVersionUID = -5473874309778800895L;

  private final int code;

  public StatusStacklessException(int code) {
    this.code = code;
  }

  public StatusStacklessException(String message, int code) {
    super(message);
    this.code = code;
  }

  public StatusStacklessException(String message, Throwable cause, int code) {
    super(message, cause);
    this.code = code;
  }

  public StatusStacklessException(Throwable cause, int code) {
    super(cause);
    this.code = code;
  }

  public StatusStacklessException(String message, Throwable cause, boolean enableSuppression,
      int code) {
    super(message, cause, enableSuppression);
    this.code = code;
  }

  public int getCode() {
    return code;
  }

}
