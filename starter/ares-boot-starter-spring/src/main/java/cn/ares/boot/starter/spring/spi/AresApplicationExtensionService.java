package cn.ares.boot.starter.spring.spi;

/**
 * @author: Ares
 * @time: 2022-05-25 12:15:29
 * @description: Boot application extension service
 * @description: 框架应用扩展服务
 * @version: JDK 1.8
 */
public interface AresApplicationExtensionService {

  /**
   * @author: Ares
   * @description: Handle before run
   * @description: 运行之前做一些处理
   * @time: 2022-05-25 12:22:22
   * @params: [primarySource, args] 主类，参数数组
   */
  void handleBeforeRun(Class<?> primarySource, String[] args);

  /**
   * @author: Ares
   * @description: Handle after run
   * @description: 运行之后做一些处理
   * @time: 2022-05-25 12:22:34
   * @params: [primarySource, args] 主类，参数数组
   */
  void handleAfterRun(Class<?> primarySource, String[] args);

  /**
   * @author: Ares
   * @description: Execution order, the smaller, the earlier
   * @description: 执行顺序，越小越早
   * @time: 2022-06-09 14:51:55
   * @return: int 执行排序
   */
  default int getOrder(){
    return 0;
  }

}
