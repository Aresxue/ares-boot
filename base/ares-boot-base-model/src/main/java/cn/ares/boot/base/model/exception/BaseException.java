package cn.ares.boot.base.model.exception;

import cn.ares.boot.base.model.status.Status;

/**
 * @author: Ares
 * @time: 2024-07-10 15:42:07
 * @description: 基础异常类
 * @description: Base exception
 * @version: JDK 1.8
 */
public class BaseException extends RuntimeException {

  private static final long serialVersionUID = -5382848575213818084L;

  /**
   * 状态
   */
  private final Status status;

  /**
   * 错误信息
   */
  private final String message;

  public BaseException(Status status) {
    super(status.getMessage());
    this.status = status;
    this.message = status.getMessage();
  }

  public BaseException(Status status, Object... params) {
    super(status.getMessage());
    this.status = status;
    this.message = String.format(status.getMessage(), params);
  }

  public BaseException(Status status, Throwable throwable) {
    super(status.getMessage(), throwable);
    this.status = status;
    this.message = status.getMessage();
  }

  public BaseException(Status status, Throwable throwable, Object... params) {
    super(status.getMessage(), throwable);
    this.status = status;
    this.message = String.format(status.getMessage(), params);
  }

  public Status getStatus() {
    return status;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "BaseException{" +
        "statusCode=" + status.getCode() +
        ", message=" + message +
        '}';
  }


}
