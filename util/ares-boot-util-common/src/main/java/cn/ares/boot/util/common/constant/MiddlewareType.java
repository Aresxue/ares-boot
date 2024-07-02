package cn.ares.boot.util.common.constant;

/**
 * @author: Ares
 * @time: 2024-07-02 19:32:17
 * @description: 中间件类型
 * @description: Middleware type
 * @version: JDK 1.8
 */
public enum MiddlewareType {
  /*
  detail
   */
  HTTP_SERVER("http-server"),
  HTTP_CLIENT("http-client"),
  RPC_PROVIDER("rpc-provider"),
  RPC_CONSUMER("rpc-consumer"),
  MQ_CONSUMER("mq-consumer"),
  MQ_PRODUCER("mq-producer"),
  TASK("task"),
  DATABASE("database"),
  CACHE("cache"),
  ;

  private final String name;

  MiddlewareType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
