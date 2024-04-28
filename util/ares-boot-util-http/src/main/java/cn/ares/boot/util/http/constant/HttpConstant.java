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

  /**
   * 默认套接字读数据超时时间
   */
  int DEFAULT_SOCKET_TIMEOUT = 30_000;
  /**
   * 默认与服务器连接超时时间
   */
  int DEFAULT_CONNECT_TIMEOUT = 15_000;
  /**
   * 从连接池获取连接默认超时时间
   */
  int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 60_000;
  /**
   * 默认请求重试次数
   */
  int DEFAULT_RETRY_TIMES = 3;
  /**
   * 连接池默认最大连接数
   */
  int DEFAULT_POOL_MAX_TOTAL = 200;
  /**
   * 连接池每个路由默认的最大连接数
   */
  int DEFAULT_POOL_MAX_PER_ROUTE = 40;
  /**
   * 连接池每个路由的默认最大连接数
   */
  int DEFAULT_POOL_MAX_ROUTE = 100;
  /**
   * 监控线程池的默认线程数量
   */
  int DEFAULT_POOL_MONITOR_THREAD_NUM = 1;
  /**
   * 连接池空闲默认驱逐时间
   */
  int DEFAULT_POOL_EVICTABLE_IDLE_TIME_MILLIS = 60_000;
  /**
   * 连接池驱逐间隔默认检查时间
   */
  int DEFAULT_POOL_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1;

}
