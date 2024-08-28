package cn.ares.boot.base.model.exception;

import cn.ares.boot.base.model.status.Status;

/**
 * @author: Ares
 * @time: 2024-07-10 15:56:15
 * @description: 业务异常
 * @description: Business exception
 * @version: JDK 1.8
 */
public class BusinessException extends BaseException {

  private static final long serialVersionUID = -2231818567180712545L;

  /**
   * 是否输出到日志
   */
  private final boolean log;

  public BusinessException(Status status) {
    this(status, true);
  }

  public BusinessException(Status status, Object... params) {
    this(true, status, params);
  }

  public BusinessException(Status status, Throwable throwable) {
    this(true, status, throwable);
  }

  public BusinessException(Status status, Throwable throwable, Object... params) {
    this(true, status, throwable, params);
  }

  public BusinessException(boolean log, Status status) {
    super(status);
    this.log = log;
  }

  public BusinessException(boolean log, Status status, Object... params) {
    super(status, params);
    this.log = log;
  }

  public BusinessException(boolean log, Status status, Throwable throwable) {
    super(status, throwable);
    this.log = log;
  }

  public BusinessException(boolean log, Status status, Throwable throwable, Object... params) {
    super(status, throwable, params);
    this.log = log;
  }

  public boolean isLog() {
    return log;
  }

}
