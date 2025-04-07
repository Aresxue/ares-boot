package cn.ares.boot.starter.cache.constant;

/**
 * @author: Ares
 * @time: 2025-04-07 16:18:20
 * @description: 尝试上锁失败后的处理方式
 * @description: Try lock fail action
 * @version: JDK 1.8
 */
public enum TryLockFailAction {

  THROW_EXCEPTION("throw TryLockFailException"),
  LOG_DEBUG("log use debug level"),
  LOG_WARN("log use warn level"),
  ;
  private final String description;

  TryLockFailAction(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

}
