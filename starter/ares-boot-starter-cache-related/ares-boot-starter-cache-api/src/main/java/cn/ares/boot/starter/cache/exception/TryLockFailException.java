package cn.ares.boot.starter.cache.exception;

import java.time.Duration;

/**
 * @author: Ares
 * @time: 2025-04-07 16:31:33
 * @description: 尝试上锁失败异常
 * @description: Try lock fail exception
 * @version: JDK 1.8
 */
public class TryLockFailException extends IllegalStateException {

  private static final long serialVersionUID = 354778576330926436L;

  public TryLockFailException(String key, Duration waitTime, Duration leaseTime, Exception ex) {
    super("try lock fail, key: " + key + ", waitTime: " + waitTime.toMillis() + ", leaseTime: " + leaseTime.toMillis(), ex);
  }


}
