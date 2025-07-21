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
  private static final int ERROR_INT = 40;

  /**
   * 日志级别
   * 小于等于0认为是不打印
   * @see org.slf4j.spi.LocationAwareLogger#WARN_INT
   * @see org.slf4j.spi.LocationAwareLogger#ERROR_INT
   */
  private final int logLevel;

  public BusinessException(Status status) {
    this(status, true);
  }

  public BusinessException(Status status, Object... params) {
    this(ERROR_INT, status, params);
  }

  public BusinessException(Status status, Throwable throwable) {
    this(ERROR_INT, status, throwable);
  }

  public BusinessException(Status status, Throwable throwable, Object... params) {
    this(ERROR_INT, status, throwable, params);
  }

  public BusinessException(int logLevel, Status status) {
    super(status);
    this.logLevel = logLevel;
  }

  public BusinessException(int logLevel, Status status, Object... params) {
    super(status, params);
    this.logLevel = logLevel;
  }

  public BusinessException(int logLevel, Status status, Throwable throwable) {
    super(status, throwable);
    this.logLevel = logLevel;
  }

  public BusinessException(int logLevel, Status status, Throwable throwable, Object... params) {
    super(status, throwable, params);
    this.logLevel = logLevel;
  }

  public int getLogLevel() {
    return this.logLevel;
  }

}
