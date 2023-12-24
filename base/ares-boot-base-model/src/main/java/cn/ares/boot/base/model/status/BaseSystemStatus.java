package cn.ares.boot.base.model.status;

/**
 * @author: Ares
 * @time: 2022-06-10 15:54:53
 * @description: 基础系统状态
 * @description: Base system status
 * @version: JDK 1.8
 */
public enum BaseSystemStatus implements SystemStatus {
  /**
   * 0~3 digits are the application identification (from 1000~9999), 4~5 digits are abnormal
   * categories (00 means system related), 6~8 digits represent the specific situation
   * 0~3位为应用标识（从1000~9999），4~5位为异常大类（00表示系统相关），6~8位表示具体情况
   */
  SUCCESS(1000_00_200, "");

  private final int code;
  private final String message;

  BaseSystemStatus(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
