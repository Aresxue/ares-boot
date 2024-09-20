package cn.ares.boot.base.model.status;

/**
 * @author: Ares
 * @time: 2022-06-10 15:46:46
 * @description: 状态
 * @description: Status
 * @version: JDK 1.8
 */
public interface Status {

  /**
   * @author: Ares
   * @description: Get status code
   * @description: 获取状态码
   * @time: 2022-06-10 15:47:46
   * @return: int 状态码
   */
  int getCode();

  /**
   * @author: Ares
   * @description: Get status message
   * @description: 获取状态信息
   * @time: 2022-06-10 15:48:24
   * @return: java.lang.String 状态信息
   */
  String getMessage();

  /**
   * @author: Ares
   * @description: 获取应用码
   * @time: 2024-08-16 14:49:23
   * @return: int 应用码
   */
  default int getAppCode() {
    return extractAppCode(getCode());
  }

  /**
   * @author: Ares
   * @description: 从错误码中提取应用码
   * @time: 2024-08-16 14:49:46
   * @params: [code] 错误码
   * @return: int 应用码
   */
  static int extractAppCode(int code) {
    String codeStr = String.valueOf(code);
    if (codeStr.length() != 9) {
      throw new IllegalStateException("status code must be 9 digits");
    }
    return Integer.parseInt(codeStr.substring(0, 3));
  }

  /**
   * @author: Ares
   * @description: 获取详情码
   * @time: 2024-08-16 14:50:09
   * @return: int 详情码
   */
  default int getDetailCode() {
    return extractDetailCode(getCode());
  }

  /**
   * @author: Ares
   * @description: 从错误码中提取详情码
   * @time: 2024-08-16 14:50:57
   * @params: [code] 错误码
   * @return: int 详情码
   */
  static int extractDetailCode(int code) {
    String codeStr = String.valueOf(code);
    if (codeStr.length() != 9) {
      throw new IllegalStateException("status code must be 9 digits");
    }
    return Integer.parseInt(codeStr.substring(7));
  }

}
