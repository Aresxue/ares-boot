package cn.ares.boot.base.dynamic.route.constant;

/**
 * @author: Ares
 * @time: 2023-05-31 20:25:02
 * @description: 动态路由类型
 * @description: Dynamic route Type
 * @version: JDK 1.8
 */
public enum DynamicRouteType {
  /*
   远程缓存、数据源、消息队列、搜索引擎
   */
  REMOTE_CACHE("remote cache"),
  DATASOURCE("datasource"),
  MQ("message queue"),
  SEARCH_ENGINE("search engine"),
  ;

  DynamicRouteType(String description) {
    this.description = description;
  }

  private final String description;

  public String getDescription() {
    return description;
  }

}
