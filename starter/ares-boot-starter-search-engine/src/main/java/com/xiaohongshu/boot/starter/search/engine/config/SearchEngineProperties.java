package com.xiaohongshu.boot.starter.search.engine.config;

import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static com.xiaohongshu.boot.starter.search.engine.config.SearchEngineEnvironmentPostProcessor.SEARCH_ENGINE_ENABLED;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author: Ares
 * @time: 2021-03-27 19:45:00
 * @description: 搜索引擎配置属性
 * @description: Search engine properties
 * @version: JDK 1.8
 */
@Configuration
@ConditionalOnProperty(name = SEARCH_ENGINE_ENABLED, havingValue = TRUE, matchIfMissing = true)
@Role(value = ROLE_INFRASTRUCTURE)
public class SearchEngineProperties {

  /**
   * 集群名称
   * Cluster name
   */
  @Value("${red.search-engine.cluster-name:}")
  private String clusterName;
  /**
   * 集群节点（这里可以是多个也可以是一个）
   * Cluster node
   */
  @Value("${red.search-engine.cluster-nodes}")
  private List<String> clusterNodes;
  /**
   * 请求超时时间
   * Request timeout
   */
  @Value("${red.search-engine.request-timeout:30000}")
  private Integer requestTimeout;
  /**
   * 连接超时时间
   * Connect timeout
   */
  @Value("${red.search-engine.connect-timeout:30000}")
  private Integer connectTimeout;
  /**
   * 套接字超时时间
   * Socket timeout
   */
  @Value("${red.search-engine.socket-timeout:30000}")
  private Integer socketTimeout;
  /**
   * 链接保持时间
   * Keep alive
   */
  @Value("${red.search-engine.keep-alive:60000}")
  private Long keepAlive;
  /**
   * 默认查询分页大小（不分页情况下覆盖默认的10条）
   * Default query page size
   */
  @Value("${red.search-engine.default-page-size:10}")
  private Integer defaultPageSize;
  /**
   * 用户名
   */
  @Value("${red.search-engine.username:elastic}")
  private String username;
  /**
   * 密码
   */
  @Value("${red.search-engine.password}")
  private String password;
  /**
   * exception: entity content is too long [*] for the configured buffer limit [*]
   * 104857600 = 100 * 1024 * 1024
   */
  @Value("${red.search-engine.buffer-limit:104857600}")
  private Integer bufferLimit;
  /**
   * The maximum number of connections in the connection pool, the default is 30, framework set to 200
   * 连接池最大连接数，默认30，框架设置为200
   */
  @Value("${red.search-engine.max-conn-total:200}")
  private Integer maxConnTotal;
  /**
   * The default maximum number of connections per route, default 10, framework set to 40
   * 每个路由默认的最大连接数, 默认10，框架设置为40
   */
  @Value("${red.search-engine.max-conn-per-route:40}")
  private Integer maxConnPerRoute;

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public List<String> getClusterNodes() {
    return clusterNodes;
  }

  public void setClusterNodes(List<String> clusterNodes) {
    this.clusterNodes = clusterNodes;
  }

  public Integer getRequestTimeout() {
    return requestTimeout;
  }

  public void setRequestTimeout(Integer requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  public Integer getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(Integer connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public Integer getSocketTimeout() {
    return socketTimeout;
  }

  public void setSocketTimeout(Integer socketTimeout) {
    this.socketTimeout = socketTimeout;
  }

  public Long getKeepAlive() {
    return keepAlive;
  }

  public void setKeepAlive(Long keepAlive) {
    this.keepAlive = keepAlive;
  }

  public Integer getDefaultPageSize() {
    return defaultPageSize;
  }

  public void setDefaultPageSize(Integer defaultPageSize) {
    this.defaultPageSize = defaultPageSize;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getBufferLimit() {
    return bufferLimit;
  }

  public void setBufferLimit(Integer bufferLimit) {
    this.bufferLimit = bufferLimit;
  }

  public Integer getMaxConnTotal() {
    return maxConnTotal;
  }

  public void setMaxConnTotal(Integer maxConnTotal) {
    this.maxConnTotal = maxConnTotal;
  }

  public Integer getMaxConnPerRoute() {
    return maxConnPerRoute;
  }

  public void setMaxConnPerRoute(Integer maxConnPerRoute) {
    this.maxConnPerRoute = maxConnPerRoute;
  }

}
