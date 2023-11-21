package cn.ares.boot.base.model.status;

/**
 * @author: Ares
 * @time: 2022-06-10 15:46:46
 * @description: 状态
 * @version: JDK 1.8
 */
public interface Status {

  /**
   * @author: Ares
   * @description: Get status code
   * @description: 获取状态码
   * @time: 2022-06-10 15:47:46
   * @params: []
   * @return: int 状态码
   */
  int getCode();

  /**
   * @author: Ares
   * @description: Get status message
   * @description: 获取状态信息
   * @time: 2022-06-10 15:48:24
   * @params: []
   * @return: java.lang.String 状态信息
   */
  String getMessage();

}
