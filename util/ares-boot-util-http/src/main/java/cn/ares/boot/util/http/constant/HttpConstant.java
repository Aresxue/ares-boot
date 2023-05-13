package cn.ares.boot.util.http.constant;

/**
 * @author: Ares
 * @time: 2021-08-19 11:33:00
 * @description: Http constant
 * @version: JDK 1.8
 */
public interface HttpConstant {

  String HTTP_REQUEST_HEADERS = "httpRequestHeaders";
  String HTTP_RESPONSE_HEADERS = "httpResponseHeaders";

  /**
   * http连接池监控线程工厂命名格式
   */
  String HTTP_POOL_MONITOR_THREAD_FACTORY_NAME = "Http-Connection-Pool-Monitor-Thread-%d";

}
