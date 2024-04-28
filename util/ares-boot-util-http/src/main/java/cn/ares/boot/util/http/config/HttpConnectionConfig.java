package cn.ares.boot.util.http.config;

import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_CONNECTION_REQUEST_TIMEOUT;
import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_CONNECT_TIMEOUT;
import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_POOL_EVICTABLE_IDLE_TIME_MILLIS;
import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_POOL_MONITOR_THREAD_NUM;
import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_POOL_MAX_PER_ROUTE;
import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_POOL_MAX_ROUTE;
import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_POOL_MAX_TOTAL;
import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_POOL_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_RETRY_TIMES;
import static cn.ares.boot.util.http.constant.HttpConstant.DEFAULT_SOCKET_TIMEOUT;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author: Ares
 * @time: 2019-08-16 17:25:00
 * @description: Http请求配置
 * @description: Http request connection configuration
 * @version: JDK 1.8
 */
@Configuration
@ConfigurationProperties(prefix = "ares.http.connection")
@Role(value = ROLE_INFRASTRUCTURE)
public class HttpConnectionConfig {

  /**
   * 套接字读数据超时时间，即从服务器获取响应数据的超时时间，单位毫秒
   * Socket read data timeout, that is, the timeout for obtaining response data from the server, in milliseconds
   */
  @Value("${socket-timeout:30000}")
  private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;

  /**
   * 与服务器连接超时时间，http客户端会创建一个异步线程用以创建套接字连接，此处设置该套接字的连接超时时间，单位毫秒
   * The connection timeout time to the server, the http client will create an asynchronous thread to create a socket connection, and the connection timeout time of the socket is set here, in milliseconds
   */
  @Value("${connect-timeout:15000}")
  private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

  /**
   * 从连接池获取连接超时时间，单位毫秒
   * Get connection timeout from connect Manager, in milliseconds
   */
  @Value("${connection-request-timeout:60000}")
  private int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;

  /**
   * 请求重试次数
   * Number of request retries
   */
  @Value("${retry-times:3}")
  private int retryTimes = DEFAULT_RETRY_TIMES;

  /**
   * 连接池
   */
  private Pool pool = new Pool();

  /**
   * 代理
   */
  private Proxy proxy = new Proxy();

  private HttpsConfig https = new HttpsConfig();

  public int getSocketTimeout() {
    return socketTimeout;
  }

  public void setSocketTimeout(int socketTimeout) {
    this.socketTimeout = socketTimeout;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public int getConnectionRequestTimeout() {
    return connectionRequestTimeout;
  }

  public void setConnectionRequestTimeout(int connectionRequestTimeout) {
    this.connectionRequestTimeout = connectionRequestTimeout;
  }

  public int getRetryTimes() {
    return retryTimes;
  }

  public void setRetryTimes(int retryTimes) {
    this.retryTimes = retryTimes;
  }

  public Pool getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

  public Proxy getProxy() {
    return proxy;
  }

  public void setProxy(Proxy proxy) {
    this.proxy = proxy;
  }

  public HttpsConfig getHttps() {
    return https;
  }

  public void setHttps(HttpsConfig https) {
    this.https = https;
  }

  public static class Pool {

    /**
     * 连接池最大连接数，默认20，框架设置为200
     * The maximum number of connections in the connection pool, the default is 20, framework set to 200
     */
    @Value("${max-total:200}")
    private int maxTotal = DEFAULT_POOL_MAX_TOTAL;
    /**
     * 每个路由默认的最大连接数，默认2，框架设置为40
     * The default maximum number of connections per route, default 2, framework set to 40
     */
    @Value("${max-per-route:40}")
    private int maxPerRoute = DEFAULT_POOL_MAX_PER_ROUTE;
    /**
     * 每个路由的最大连接数,优先于maxPerRoute
     * The maximum number of connections per route, which takes precedence over maxPerRoute
     */
    @Value("${max-route:100}")
    private int maxRoute = DEFAULT_POOL_MAX_ROUTE;
    /**
     * 监控线程池的线程数量
     * Monitor the number of threads in the thread pool
     */
    @Value("${monitor-thread-num:1}")
    private int monitorThreadNum = DEFAULT_POOL_MONITOR_THREAD_NUM;
    /**
     * 把空闲时间超过minEvictableIdleTimeMillis毫秒的连接断开，代码中会向前滚动2s保证在超时之前可以关闭空闲连接 很多服务端默认超时为60s，所以这里默认超时时间为60s
     * Disconnect connections that have been idle for more than minEvictableIdleTimeMillis milliseconds The code will scroll forward 2s to ensure that idle connections can be closed before the timeout. Many servers have a default timeout of 60s, so the default timeout here is 60s.
     */
    @Value("${evictable-idle-time-millis:60000}")
    private int evictableIdleTimeMillis = DEFAULT_POOL_EVICTABLE_IDLE_TIME_MILLIS;
    /**
     * 毫秒检查一次连接池中空闲的连接 需小于evictable-idle-time-millis / 2, 否则会导致连接关闭不及时 默认为-1时会取值evictable-idle-time-millis / 2 - 500
     * Check for idle connections in the connection pool once in milliseconds It needs to be less than evictable-idle-time-millis/2, otherwise the connection will not be closed in time When the default is -1, it will take the value evictable-idle-time-millis/2 - 1000*
     */
    @Value("${time-between-eviction-runs-millis:-1}")
    private int timeBetweenEvictionRunsMillis = DEFAULT_POOL_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    public int getMaxTotal() {
      return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
      this.maxTotal = maxTotal;
    }

    public int getMaxPerRoute() {
      return maxPerRoute;
    }

    public void setMaxPerRoute(int maxPerRoute) {
      this.maxPerRoute = maxPerRoute;
    }

    public int getMaxRoute() {
      return maxRoute;
    }

    public void setMaxRoute(int maxRoute) {
      this.maxRoute = maxRoute;
    }

    public int getMonitorThreadNum() {
      return monitorThreadNum;
    }

    public void setMonitorThreadNum(int monitorThreadNum) {
      this.monitorThreadNum = monitorThreadNum;
    }

    public int getEvictableIdleTimeMillis() {
      return evictableIdleTimeMillis;
    }

    public void setEvictableIdleTimeMillis(int evictableIdleTimeMillis) {
      this.evictableIdleTimeMillis = evictableIdleTimeMillis;
    }

    public int getTimeBetweenEvictionRunsMillis() {
      return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
      this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }
  }

  public static class Proxy {

    private String hostname;
    private Integer port;
    @Value("${scheme-name:}")
    private String schemeName;

    public String getHostname() {
      return hostname;
    }

    public void setHostname(String hostname) {
      this.hostname = hostname;
    }

    public Integer getPort() {
      return port;
    }

    public void setPort(Integer port) {
      this.port = port;
    }

    public String getSchemeName() {
      return schemeName;
    }

    public void setSchemeName(String schemeName) {
      this.schemeName = schemeName;
    }
  }

  public static class HttpsConfig {

    /**
     * 支持的协议如TLSv1.2
     */
    @Value("${supported-protocols:}")
    private String[] supportedProtocols;
    /**
     * 忽略SSL证书校验
     * Ignore SSL certificate verification
     */
    @Value("${noop-hostname-verifier:false}")
    private boolean noopHostnameVerifier;

    public String[] getSupportedProtocols() {
      return supportedProtocols;
    }

    public void setSupportedProtocols(String[] supportedProtocols) {
      this.supportedProtocols = supportedProtocols;
    }

    public boolean isNoopHostnameVerifier() {
      return noopHostnameVerifier;
    }

    public void setNoopHostnameVerifier(boolean noopHostnameVerifier) {
      this.noopHostnameVerifier = noopHostnameVerifier;
    }

  }

}
