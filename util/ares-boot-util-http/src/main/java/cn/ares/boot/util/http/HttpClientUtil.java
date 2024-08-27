package cn.ares.boot.util.http;

import static cn.ares.boot.util.common.constant.SymbolConstant.AND;
import static cn.ares.boot.util.common.constant.SymbolConstant.COLON;
import static cn.ares.boot.util.common.constant.SymbolConstant.EQUALS;
import static cn.ares.boot.util.common.constant.SymbolConstant.LEFT_SQ_BRACKET;
import static cn.ares.boot.util.common.constant.SymbolConstant.QUESTION_MARK;
import static cn.ares.boot.util.common.constant.SymbolConstant.RIGHT_SQ_BRACKET;
import static cn.ares.boot.util.common.constant.SymbolConstant.SPOT;
import static cn.ares.boot.util.http.constant.HttpConstant.HTTP_POOL_MONITOR_THREAD_FACTORY_NAME;
import static cn.ares.boot.util.http.constant.HttpConstant.HTTP_REQUEST_HEADERS;
import static cn.ares.boot.util.http.constant.HttpConstant.HTTP_RESPONSE_HEADERS;
import static org.apache.hc.core5.http.ContentType.APPLICATION_FORM_URLENCODED;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_SUPPORT;

import cn.ares.boot.util.common.ArrayUtil;
import cn.ares.boot.util.common.ExceptionUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.common.file.FileUtil;
import cn.ares.boot.util.common.network.NetworkUtil;
import cn.ares.boot.util.common.thread.NameThreadFactory;
import cn.ares.boot.util.common.thread.ThreadLocalMapUtil;
import cn.ares.boot.util.http.config.HttpConnectionConfig;
import cn.ares.boot.util.http.config.HttpConnectionConfig.HttpsConfig;
import cn.ares.boot.util.http.config.HttpConnectionConfig.Proxy;
import cn.ares.boot.util.json.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.ssl.HostnameVerifier;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.psl.PublicSuffixMatcherLoader;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;

/**
 * @author: Ares
 * @time: 2019-02-26 01:31:00
 * @description: Http请求工具类，请求由连接池维护
 * @description: Http request tool class, the request is maintained by the connection pool
 * @version: JDK 1.8
 */
@Component
@Import(HttpConnectionConfig.class)
@Role(value = ROLE_SUPPORT)
public class HttpClientUtil implements ApplicationContextAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

  private static HttpConnectionConfig config = new HttpConnectionConfig();

  /**
   * Http请求客户端，针对单个host或ip+port使用一个CloseableHttpClient Http request client, use a
   * CloseableHttpClient for a single host or ip+port
   */
  private static final Map<String, CloseableHttpClient> HTTP_CLIENT_MAP = new ConcurrentHashMap<>();
  /**
   * Http连接池管理对象 Http connection pool management object
   */
  private static PoolingHttpClientConnectionManager connectionManager;
  /**
   * 监控Http连接池中的空闲和异常连接 Monitor idle and abnormal connections in the Http connection pool
   */
  private static ScheduledExecutorService monitorExecutor = null;

  /**
   * 是否启动监控线程 Whether to start the monitoring thread
   */
  private static final AtomicBoolean IS_START_MONITOR = new AtomicBoolean(false);

  static {
    //  初始化Http连接池管理对象
    //  Init http connection pool management object
    initManager(config, null);
  }

  /**
   * @author: Ares
   * @description: 根据请求地址获取Http请求客户端
   * @description: Get the Http request client based on the request address
   * @time: 2019-05-08 15:31:00
   * @params: [host, port] host，端口
   * @return: org.apache.http.impl.client.CloseableHttpClient
   **/
  public static CloseableHttpClient getHttpClient(String host, int port) {
    // 保证第一次获取Http请求客户端启动监控线程, 且只启动一次
    // Ensure that the client starts the monitor thread the first time it gets a Http request, and only once
    if (IS_START_MONITOR.compareAndSet(false, true)) {
      startMonitor();
    }
    String uniqueKey = host + COLON + port;
    return HTTP_CLIENT_MAP.computeIfAbsent(uniqueKey, client -> createHttpClient(host, port));
  }

  /**
   * @author: Ares
   * @description: 根据主机名和端口号创建Http请求客户端
   * @description: Create Http request client based on hostname and port number
   * @time: 2019-05-08 15:32:00
   * @params: [hostOrIp, port] 主机名, 端口号
   * @return: org.apache.http.impl.client.CloseableHttpClient
   **/
  private static CloseableHttpClient createHttpClient(String hostOrIp, int port) {
    // 请求重试处理
    // Request retry processing
    HttpRequestRetryStrategy retryStrategy = new DefaultHttpRequestRetryStrategy(
        config.getRetryTimes(), TimeValue.ofSeconds(1L));

    HttpHost httpHost = new HttpHost(hostOrIp, port);
    // 设置路由的最大连接数, 优先于DefaultMaxPerRoute
    // Set the maximum number of connections for the route, which takes precedence over DefaultMaxPerRoute
    connectionManager.setMaxPerRoute(new HttpRoute(httpHost), config.getPool().getMaxRoute());

    ConnectionConfig.Builder connectionConfig = ConnectionConfig.custom();
    connectionConfig.setSocketTimeout(Timeout.ofMilliseconds(config.getSocketTimeout()));
    connectionConfig.setConnectTimeout(Timeout.ofMilliseconds(config.getConnectTimeout()));
    connectionManager.setDefaultConnectionConfig(connectionConfig.build());

    return HttpClients.custom().setConnectionManager(connectionManager)
        // 关闭自动重试
//        .disableAutomaticRetries()
        .setRetryStrategy(retryStrategy)
        // 下面两个选项会启动一个线程池和线程去清理连接(默认10s一次)但是空闲连接的hold时间小于服务端超时时间的一半，
        // 对资源的利用不充分，所以这里不开启，使用自定义监控线程去清理异常连接和空闲连接
//        .evictExpiredConnections()
        // 驱逐空闲连接, evictableIdleTimeMillis应小于服务端超时时间的一半
//        .evictIdleConnections(config.getPool().getEvictableIdleTimeMillis(), TimeUnit.MILLISECONDS)
        .setProxy(buildHttpProxy())
        .build();
  }

  /**
   * @author: Ares
   * @description: 使用请求对象发起Post请求地址获取结果
   * @description: Use the request object to initiate a Post request address to get the result
   * @time: 2022-12-28 11:40:07
   * @params: [url, request] 请求地址，请求对象
   * @return: java.lang.String 响应结果
   */
  public static <T> String post(String url, T request) throws Exception {
    return post(url, request, Collections.emptyMap());
  }


  /**
   * @author: Ares
   * @description: 使用请求对象发起Post请求地址获取结果/文件
   * @description: Use the request object to initiate a Post request address to get the result or
   * file
   * @time: 2022-12-28 11:40:07
   * @params: [url, request, fileSavePath] 请求地址，请求对象，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   */
  public static <T> String post(String url, T request, String fileSavePath) throws Exception {
    return post(url, request, Collections.emptyMap(), fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 使用请求对象发起Post请求地址获取结果（等待指定超时时间）
   * @description: Use the request object to initiate a Post request address to get the result (wait
   * for the specified timeout)
   * @time: 2022-12-28 11:40:07
   * @params: [url, request, socketTimeout] 请求地址，请求对象，超时时间）
   * @return: java.lang.String 响应结果
   */
  public static <T> String post(String url, T request, int socketTimeout)
      throws Exception {
    return post(url, request, Collections.emptyMap(), socketTimeout, null);
  }

  /**
   * @author: Ares
   * @description: 传入请求对象和消息头发起Post请求地址获取结果/文件
   * @description: Pass in the request object and message header to initiate the Post request
   * address to get the result or file
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers, fileSavePath] 请求地址，请求对象，消息头，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   */
  public static <T> String post(String url, T request, Map<String, String> headers,
      String fileSavePath) throws Exception {
    return post(url, request, headers, config.getSocketTimeout(), fileSavePath);
  }


  /**
   * @author: Ares
   * @description: 传入请求对象和消息头发起Post请求地址获取结果
   * @description: Pass in the request object and message header to initiate the Post request
   * address to get the result (wait for the specified timeout)
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers] 请求地址，请求对象，消息头
   * @return: java.lang.String 响应结果
   */
  public static <T> String post(String url, T request, Map<String, String> headers)
      throws Exception {
    return post(url, request, headers, config.getSocketTimeout());
  }

  /**
   * @author: Ares
   * @description: 传入请求对象和消息头发起Post请求地址获取结果（等待指定超时时间）
   * @description: Pass in the request object and message header to initiate the Post request
   * address to get the result (wait for the specified timeout)
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers, socketTimeout] 请求地址，请求对象，消息头，超时时间
   * @return: java.lang.String 响应结果
   */
  public static <T> String post(String url, T request, Map<String, String> headers,
      int socketTimeout) throws Exception {
    return post(url, request, headers, socketTimeout, config.getConnectTimeout(),
        config.getConnectionRequestTimeout(), null);
  }


  /**
   * @author: Ares
   * @description: 传入请求对象和消息头发起Post请求地址获取结果/文件（等待指定超时时间）
   * @description: Pass in the request object and message header to initiate the Post request
   * address to get the result or file (wait for the specified timeout)
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers, socketTimeout, fileSavePath] 请求地址，请求对象，消息头，超时时间，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   */
  public static <T> String post(String url, T request, Map<String, String> headers,
      int socketTimeout, String fileSavePath) throws Exception {
    return post(url, request, headers, socketTimeout, config.getConnectTimeout(),
        config.getConnectionRequestTimeout(), fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 传入请求对象和消息头发起Post请求地址获取结果/文件（等待指定超时时间、连接超时时间、连接获取超时时间）
   * @description: The incoming request object and message header initiate the Post request address
   * to get the result or file (waiting for the specified timeout, connection timeout, and
   * connection acquisition timeout)
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers, socketTimeout, connectTimeout, connectionRequestTimeout,
   * fileSavePath] 请求地址，请求对象，消息头，套接字超时时间，连接超时时间，连接获取超时时间，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   */
  public static <T> String post(String url, T request, Map<String, String> headers,
      int socketTimeout, int connectTimeout, int connectionRequestTimeout, String fileSavePath)
      throws Exception {
    return post(url, request, headers, socketTimeout, connectTimeout, connectionRequestTimeout,
        null, fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 传入请求对象和消息头使用代理发起Post请求地址获取结果/文件（等待指定超时时间、连接超时时间、连接获取超时时间）
   * @description: The incoming request object and message header use the proxy to initiate a Post
   * request address to get the result or file (waiting for the specified timeout, connection
   * timeout, and connection acquisition timeout)
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers, socketTimeout, connectTimeout, connectionRequestTimeout,
   * proxy] 请求地址，请求对象，消息头，套接字超时时间，连接超时时间，连接获取超时时间，http代理
   * @return: java.lang.String 响应结果
   */
  public static <T> String post(String url, T request, Map<String, String> headers,
      int socketTimeout, int connectTimeout, int connectionRequestTimeout, HttpHost proxy,
      String fileSavePath) throws Exception {
    HttpPost httpPost = new HttpPost(url);
    httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON.toString());
    configRequest(httpPost, headers, socketTimeout, connectTimeout, connectionRequestTimeout);
    return request(httpPost, request, fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 发起get请求地址获取结果
   * @description: Initiate a get request address to get the result
   * @time: 2019-05-08 15:39:00
   * @params: [url] 请求地址
   * @return: java.lang.String 响应结果
   **/
  public static String get(String url) throws Exception {
    return get(url, "");
  }


  /**
   * @author: Ares
   * @description: 发起get请求地址获取结果/文件
   * @description: Initiate a get request address to get the result or file
   * @time: 2019-05-08 15:39:00
   * @params: [url, fileSavePath] 请求地址，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   **/
  public static String get(String url, String fileSavePath) throws Exception {
    return get(url, Collections.emptyMap(), config.getSocketTimeout(), fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 发起get请求地址获取结果（等待超时时间）
   * @description: Initiate a get request address to get the result (wait for the timeout period)
   * @time: 2019-05-08 15:39:00
   * @params: [url， socketTimeout] 请求地址，超时时间
   * @return: java.lang.String 响应结果
   **/
  public static String get(String url, int socketTimeout) throws Exception {
    return get(url, Collections.emptyMap(), socketTimeout, null);
  }

  /**
   * @author: Ares
   * @description: 使用请求对象发起get请求地址获取结果/文件
   * @description: Use the request object to initiate a get request address to get the result or
   * file
   * @time: 2019-05-08 15:39:00
   * @params: [url, param, fileSavePath] 请求地址，请求参数，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   **/
  public static <T> String get(String url, T param, String fileSavePath) throws Exception {
    return get(url, param, Collections.emptyMap(), config.getSocketTimeout(), fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 使用消息头发起get请求地址获取结果
   * @description: Use the message header to initiate a get request address to obtain the result
   * @time: 2024-06-12 14:12:09
   * @params: [url, headers] 请求地址，消息头
   * @return: java.lang.String 响应结果
   */
  public static String get(String url, Map<String, String> headers) throws Exception {
    return get(url, headers, "");
  }

  /**
   * @author: Ares
   * @description: 使用消息头发起get请求地址获取结果
   * @description: Use the message header to initiate a get request address to obtain the result
   * @time: 2024-06-12 23:18:04
   * @params: [url, param, headers] 请求地址，入参，消息头
   * @return: java.lang.String 响应结果
   */
  public static <T> String get(String url, T param, Map<String, String> headers) throws Exception {
    return get(url, param, headers, config.getSocketTimeout(), null);
  }

  /**
   * @author: Ares
   * @description: 使用消息头发起get请求地址获取结果/文件
   * @description: Use the message header to initiate a get request address to get the result or
   * file
   * @time: 2019-05-08 15:39:00
   * @params: [url, param, fileSavePath] 请求地址，消息头，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   **/
  public static String get(String url, Map<String, String> headers, String fileSavePath)
      throws Exception {
    return get(url, headers, config.getSocketTimeout(), fileSavePath);
  }


  /**
   * @author: Ares
   * @description: 使用请求对象发起get请求地址获取结果（等待超时时间）
   * @description: Use the request object to initiate a get request address to get the result (wait
   * for the timeout period)
   * @time: 2019-05-08 15:39:00
   * @params: [url, param, socketTimeout] 请求地址，请求参数，超时时间
   * @return: java.lang.String 响应结果
   **/
  public static <T> String get(String url, T param, int socketTimeout) throws Exception {
    return get(url, param, Collections.emptyMap(), socketTimeout, null);
  }

  /**
   * @author: Ares
   * @description: 使用消息头发起get请求地址获取结果/文件（等待超时时间）
   * @description: Use the message header to initiate a get request address to get the result or
   * file (wait for the timeout period)
   * @time: 2019-05-08 15:39:00
   * @params: [url, param, socketTimeout, fileSavePath] 请求地址，消息头，超时时间，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   **/
  public static String get(String url, Map<String, String> headers, int socketTimeout,
      String fileSavePath) throws Exception {
    return get(url, headers, socketTimeout, config.getConnectTimeout(),
        config.getConnectionRequestTimeout(), fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 使用请求对象和消息头发起get请求地址获取结果/文件（等待超时时间）
   * @description: Use the request object and message header to initiate a get request address to
   * get the result or file (waiting for the timeout period)
   * @time: 2019-05-08 15:39:00
   * @params: [url, param, headers, socketTimeout, fileSavePath] 请求地址，请求对象，消息头，超时时间，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   **/
  public static <T> String get(String url, T param, Map<String, String> headers, int socketTimeout,
      String fileSavePath) throws Exception {
    if (null != param) {
      String query = HttpClientUtil.encodeGetRequest(param);
      if (StringUtil.isNotEmpty(query)) {
        url += QUESTION_MARK + query;
      }
    }
    return get(url, headers, socketTimeout, fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 使用消息头发起get请求地址获取结果/文件（等待超时时间，连接超时时间，连接获取超时时间）
   * @description: Use the message header to initiate a get request address to get the result or
   * file(waiting timeout, connection timeout, connection acquisition timeout)
   * @time: 2019-05-08 15:39:00
   * @params: [url, headers, socketTimeout, connectTimeout, connectionRequestTimeout, fileSavePath]
   * 请求地址，消息头，套接字超时时间，连接超时时间，连接获取超时时间，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   **/
  public static String get(String url, Map<String, String> headers, int socketTimeout,
      int connectTimeout, int connectionRequestTimeout, String fileSavePath) throws Exception {
    return get(url, headers, socketTimeout, connectTimeout, connectionRequestTimeout, null,
        fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 使用消息头使用代理发起get请求地址获取结果/文件（等待超时时间，连接超时时间，连接获取超时时间）
   * @description: Use the message header to use the proxy to initiate a get request address to get
   * the result or file (waiting timeout, connection timeout, connection acquisition timeout)
   * @time: 2019-05-08 15:39:00
   * @params: [url, headers, socketTimeout, connectTimeout, connectionRequestTimeout, proxy,
   * fileSavePath] 请求地址，消息头，套接字超时时间，连接超时时间，连接获取超时时间，http代理，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   **/
  public static String get(String url, Map<String, String> headers, int socketTimeout,
      int connectTimeout, int connectionRequestTimeout, HttpHost proxy, String fileSavePath)
      throws Exception {
    HttpGet httpGet = new HttpGet(url);
    httpGet.setHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString());
    configRequest(httpGet, headers, socketTimeout, connectTimeout, connectionRequestTimeout);
    return request(httpGet, null, fileSavePath);
  }

  /**
   * @author: Ares
   * @description: 把get请求对象转为?id=1&name=ares这种形式
   * @description: Convert the get request object to the form of ?id=1&name=ares
   * @time: 2021-10-21 20:13:00
   * @params: [request] 请求对象
   * @return: java.lang.String response
   */
  public static <T> String encodeGetRequest(T request) {
    JsonNode jsonNode = JsonUtil.parseObject(request);
    StringJoiner result = new StringJoiner(AND);
    ExceptionUtil.run(() -> handleObjectNode(jsonNode, result, ""));
    return result.toString();
  }

  /**
   * @author: Ares
   * @description: 使用请求对象发起Delete请求地址获取结果
   * @description: Use the request object to initiate a Delete request address to get the result
   * @time: 2022-12-28 11:40:07
   * @params: [url, request] 请求地址，请求对象
   * @return: java.lang.String 响应结果
   */
  public static <T> String delete(String url, T request) throws Exception {
    return delete(url, request, Collections.emptyMap());
  }

  /**
   * @author: Ares
   * @description: 使用请求对象发起Delete请求地址获取结果（等待指定超时时间）
   * @description: Use the request object to initiate a Delete request address to get the result
   * (wait for the specified timeout)
   * @time: 2022-12-28 11:40:07
   * @params: [url, request, socketTimeout] 请求地址，请求对象，超时时间
   * @return: java.lang.String 响应结果
   */
  public static <T> String delete(String url, T request, int socketTimeout) throws Exception {
    return delete(url, request, Collections.emptyMap(), socketTimeout);
  }

  /**
   * @author: Ares
   * @description: 传入请求对象和消息头发起Delete请求地址获取结果
   * @description: Pass in the request object and message header to initiate the Delete request
   * address to get the result
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers] 请求地址，请求对象，消息头
   * @return: java.lang.String 响应结果
   */
  public static <T> String delete(String url, T request, Map<String, String> headers)
      throws Exception {
    return delete(url, request, headers, config.getSocketTimeout());
  }

  /**
   * @author: Ares
   * @description: 传入请求对象和消息头发起Delete请求地址获取结果（等待指定超时时间）
   * @description: Pass in the request object and message header to initiate the Delete request
   * address to get the result (wait for the specified timeout)
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers, socketTimeout] 请求地址，请求对象，消息头，超时时间
   * @return: java.lang.String 响应结果
   */
  public static <T> String delete(String url, T request, Map<String, String> headers,
      int socketTimeout) throws Exception {
    return delete(url, request, headers, socketTimeout, config.getConnectTimeout(),
        config.getConnectionRequestTimeout());
  }

  /**
   * @author: Ares
   * @description: 传入请求对象和消息头发起Delete请求地址获取结果（等待指定超时时间、连接超时时间、连接获取超时时间）
   * @description: The incoming request object and message header initiate the Delete request
   * address to get the result (waiting for the specified timeout, connection timeout, and
   * connection acquisition timeout)
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers, socketTimeout, connectTimeout, connectionRequestTimeout]
   * 请求地址，请求对象，消息头，套接字超时时间，连接超时时间，连接获取超时时间
   * @return: java.lang.String 响应结果
   */
  public static <T> String delete(String url, T request, Map<String, String> headers,
      int socketTimeout, int connectTimeout, int connectionRequestTimeout) throws Exception {
    return delete(url, request, headers, socketTimeout, connectTimeout, connectionRequestTimeout,
        null);
  }

  /**
   * @author: Ares
   * @description: 传入请求对象和消息头使用代理发起Delete请求地址获取结果（等待指定超时时间、连接超时时间、连接获取超时时间）
   * @description: The incoming request object and message header use the proxy to initiate a Post
   * request address to get the result (waiting for the specified timeout, connection timeout, and
   * connection acquisition timeout)
   * @time: 2022-12-28 11:41:46
   * @params: [url, request, headers, socketTimeout, connectTimeout, connectionRequestTimeout,
   * proxy] 请求地址，请求对象，消息头，套接字超时时间，连接超时时间，连接获取超时时间，http代理
   * @return: java.lang.String 响应结果
   */
  public static <T> String delete(String url, T request, Map<String, String> headers,
      int socketTimeout, int connectTimeout, int connectionRequestTimeout, HttpHost proxy)
      throws Exception {
    HttpDelete httpDelete = new HttpDelete(url);
    httpDelete.setHeader(CONTENT_TYPE, APPLICATION_JSON.toString());
    configRequest(httpDelete, headers, socketTimeout, connectTimeout, connectionRequestTimeout);
    return request(httpDelete, request, null);
  }

  /**
   * @author: Ares
   * @description: 获取最后一次http请求的请求头
   * @description: Get the request headers of the last http request
   * @time: 2022-07-14 14:22:18
   * @params: []
   * @return: org.apache.http.Header[] 请求消息头
   */
  public static Header[] getLastHttpRequestHeaders() {
    return ThreadLocalMapUtil.get(HTTP_REQUEST_HEADERS);
  }

  /**
   * @author: Ares
   * @description: 获取最后一次http请求的响应头
   * @description: Get the response headers of the last http request
   * @time: 2022-07-14 14:33:08
   * @params: []
   * @return: org.apache.http.Header[] 响应消息头
   */
  public static Header[] getLastHttpResponseHeaders() {
    return ThreadLocalMapUtil.get(HTTP_RESPONSE_HEADERS);
  }

  private static void handleObjectNode(JsonNode jsonNode, StringJoiner result, String prefix)
      throws UnsupportedEncodingException {
    if (null != jsonNode) {
      Iterator<String> iterator = jsonNode.fieldNames();
      while (iterator.hasNext()) {
        String key = iterator.next();
        JsonNode currentNode = jsonNode.get(key);
        if (currentNode instanceof NullNode) {
          continue;
        }
        if (currentNode.isObject()) {
          handleObjectNode(currentNode, result, key + SPOT);
        } else if (currentNode.isArray()) {
          handleArrayNode(currentNode, result, key);
        } else {
          String encodeKey = URLEncoder.encode(prefix + key, Charset.defaultCharset().name());
          String encodeValue = URLEncoder.encode(currentNode.asText(),
              Charset.defaultCharset().name());
          result.add(encodeKey + EQUALS + encodeValue);
        }
      }
    }
  }

  private static void handleArrayNode(JsonNode jsonNode, StringJoiner result, String prefix)
      throws UnsupportedEncodingException {
    if (null != jsonNode) {
      Iterator<JsonNode> arrayIterator = jsonNode.elements();
      int index = 0;
      while (arrayIterator.hasNext()) {
        JsonNode currentNode = arrayIterator.next();
        String key = prefix + LEFT_SQ_BRACKET + index + RIGHT_SQ_BRACKET;
        if (currentNode.isObject()) {
          handleObjectNode(currentNode, result, key + SPOT);
        } else if (currentNode.isArray()) {
          handleArrayNode(currentNode, result, key);
        } else {
          String encodeKey = URLEncoder.encode(key, Charset.defaultCharset().name());
          String encodeValue = URLEncoder.encode(currentNode.asText(),
              Charset.defaultCharset().name());
          result.add(encodeKey + EQUALS + encodeValue);
        }
        index++;
      }
    }
  }

  /**
   * @author: Ares
   * @description: 请求通用代码
   * @description: Request generic code
   * @time: 2019-05-08 15:46:00
   * @params: [requestBase, url, request, fileSavePath] 请求基类，请求地址，请求对象，文件保存地址（可选）
   * @return: java.lang.String 响应结果
   **/
  private static <T> String request(HttpUriRequestBase requestBase, T request, String fileSavePath)
      throws Exception {
    setBody(requestBase, request);
    URI uri = requestBase.getUri();
    int port = NetworkUtil.extractPort(uri);
    return getHttpClient(uri.getHost(), port).execute(requestBase, HttpClientContext.create(),
        new BasicHttpClientResponseHandler() {
          @Override
          public String handleResponse(ClassicHttpResponse response) throws IOException {
            ThreadLocalMapUtil.set(HTTP_RESPONSE_HEADERS, response.getHeaders());

            if (StringUtil.isBlank(fileSavePath)) {
              String result = super.handleResponse(response);
              int statusCode = response.getCode();
              if (statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                throw new RuntimeException(
                    "Status code is " + statusCode + ", response is " + result);
              }
              return result;
            } else {
              // 读取数据
              // read data
              byte[] bytes = EntityUtils.toByteArray(response.getEntity());
              int statusCode = response.getCode();
              if (statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                throw new RuntimeException(
                    "Status code is " + statusCode + ", response is " + new String(bytes));
              }
              FileUtil.writeByteArrayToFile(new File(fileSavePath), bytes);
            }
            return null;
          }

          @SuppressWarnings("see")
          /**
           * @see EntityUtils#DEFAULT_CHARSET
           */
          @Override
          public String handleEntity(final HttpEntity entity) throws IOException {
            try {
              String contentEncoding = entity.getContentEncoding();
              if (StringUtil.isEmpty(contentEncoding)) {
                // EntityUtils默认是ISO-8859-1编码，这里如果发现不存在编码则使用应用的默认编码
                // EntityUtils defaults to ISO-8859-1 encoding, where the application's default encoding is used if no encoding is found
                return EntityUtils.toString(entity, Charset.defaultCharset());
              } else {
                return EntityUtils.toString(entity);
              }
            } catch (final ParseException ex) {
              throw new ClientProtocolException(ex);
            }
          }
        });
  }

  /**
   * @author: Ares
   * @description: 请求需要设置消息体
   * @description: The request needs to set the message body
   * @time: 2019-08-16 16:56:00
   * @params: [httpRequestBase, request] 请求对象, 请求消息体
   */
  private static <T> void setBody(HttpUriRequestBase httpRequestBase, T request) {
    if (null == request) {
      return;
    }
    if (!(httpRequestBase instanceof HttpPost)) {
      return;
    }
    HttpPost httpPost = (HttpPost) httpRequestBase;
    Header header = httpPost.getFirstHeader(CONTENT_TYPE);
    if (null == header) {
      return;
    }
    String headerValue = header.getValue();
    if (headerValue.contains(APPLICATION_FORM_URLENCODED.getMimeType())) {
      httpPost.setEntity(new StringEntity(encodeGetRequest(request), Charset.defaultCharset()));
    } else {
      httpPost.setEntity(new StringEntity(JsonUtil.toJsonString(request), APPLICATION_JSON));
    }
  }

  /**
   * @author: Ares
   * @description: 配置http请求对象的消息头和超时时间
   * @description: Configure the header and timeout of the http request object
   * @time: 2019-11-01 16:21:00
   * @params: [requestBase, headers, socketTimeout, connectTimeout, connectionRequestTimeout]
   * 请求基类，自定义消息头，套接字超时时间，连接超时时间，获取连接超时时间
   */
  private static void configRequest(HttpUriRequestBase requestBase, Map<String, String> headers,
      int socketTimeout, int connectTimeout, int connectionRequestTimeout) {
    // 设置公共Header等
    // requestBase.setHeader("User-Agent", "Mozilla/5.0");
    // requestBase.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    // requestBase.setHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
    // requestBase.setHeader("Accept-Charset","ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");
    // 根据外部传入参数设置消息头
    // Set message headers based on external incoming parameters
    headers.forEach(requestBase::setHeader);
    ThreadLocalMapUtil.set(HTTP_REQUEST_HEADERS, requestBase.getHeaders());

    // setConnectionManagerShared谨慎使用, 误用会导致关闭一个host或ip和port下的client会关闭公用的manager
    // Use setConnectionManagerShared with caution, misuse will lead to closing a host or client under ip and port will close the public manager
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeout))
        .setConnectTimeout(Timeout.ofMilliseconds(connectTimeout))
        .setResponseTimeout(Timeout.ofMilliseconds(socketTimeout)).build();
    requestBase.setConfig(requestConfig);
  }

  private static HttpHost buildHttpProxy() {
    HttpHost httpProxy = null;
    Proxy proxy = config.getProxy();
    String hostname = proxy.getHostname();
    String schemeName = proxy.getSchemeName();
    Integer port = proxy.getPort();
    if (StringUtil.allIsNotEmpty(hostname, schemeName) && null != port) {
      httpProxy = new HttpHost(schemeName, hostname, port);
    }
    return httpProxy;
  }

  /**
   * @author: Ares
   * @description: 初始化Http连接池管理对象
   * @description: Init http connection pool management object
   * @time: 2023-03-09 15:23:49
   * @params: [httpConnectionConfig] http连接配置
   */
  private static void initManager(HttpConnectionConfig httpConnectionConfig,
      HostnameVerifier hostnameVerifier) {
    ConnectionSocketFactory connectionSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
    HttpsConfig httpsConfig = httpConnectionConfig.getHttps();
    // 为空时构造一个hostnameVerifier不为空时使用传入的以支持用户自定义
    if (null == hostnameVerifier) {
      if (httpsConfig.isNoopHostnameVerifier()) {
        hostnameVerifier = NoopHostnameVerifier.INSTANCE;
      } else {
        hostnameVerifier = new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
      }
    }
    String[] supportedProtocols = httpsConfig.getSupportedProtocols();
    if (ArrayUtil.isEmpty(supportedProtocols)) {
      supportedProtocols = null;
    }
    LayeredConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
        SSLContexts.createDefault(), supportedProtocols, null, hostnameVerifier);
    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", connectionSocketFactory).register("https", socketFactory).build();
    connectionManager = new PoolingHttpClientConnectionManager(registry);
    // 设置最大连接数, 默认20，框架设置为200
    // Set the maximum number of connections, the default is 20, framework set to 200
    connectionManager.setMaxTotal(httpConnectionConfig.getPool().getMaxTotal());
    // 设置每个路由默认的最大连接数，默认2，框架设置为40
    // Set the default maximum number of connections per route, default 2, framework set to 40
    connectionManager.setDefaultMaxPerRoute(httpConnectionConfig.getPool().getMaxPerRoute());
  }

  /**
   * @author: Ares
   * @description: 开启监控线程, 对异常和空闲线程进行关闭
   * @description: Start monitoring threads, close exceptions and idle threads
   * @time: 2019-08-17 09:49:00
   * @params: []
   */
  private static void startMonitor() {
    // 使用命名的线程工厂，在排查问题有标识性
    // Use named thread factories to identify issues when troubleshooting
    ThreadFactory monitorHttpConnectPoolFactory = new NameThreadFactory().setNameFormat(
        HTTP_POOL_MONITOR_THREAD_FACTORY_NAME).setDaemon(true).build();
    int monitorThreadNum = config.getPool().getMonitorThreadNum();
    monitorExecutor = new ScheduledThreadPoolExecutor(monitorThreadNum,
        monitorHttpConnectPoolFactory);

    long period = config.getPool().getTimeBetweenEvictionRunsMillis();
    // 如果为-1则按照evictableIdleTimeMillis自动计算线程执行周期
    if (-1 == period) {
      // 防止连接超时发生在清理线程未工作时，需小于evictableIdleTimeMillis/2, 这里的500ms可以视作留给连接处理的时间
      period = getEvictableIdleTimeMillis() / 2 - 500;
    }
    for (int i = 0; i < monitorThreadNum; i++) {
      MonitorHttpWorker worker = new MonitorHttpWorker(connectionManager);

      monitorExecutor.scheduleAtFixedRate(worker, period, period, TimeUnit.MILLISECONDS);
    }
  }

  public static void closeHttpConnectionPool() {
    try {
      for (CloseableHttpClient httpClient : HTTP_CLIENT_MAP.values()) {
        httpClient.close();
      }
      connectionManager.close();
      monitorExecutor.shutdown();
    } catch (IOException e) {
      LOGGER.error("close http connection pool exception: ", e);
    }
  }

  public static void closeHttpConnectionPool(String singleHostPort) {
    try {
      CloseableHttpClient httpClient = HTTP_CLIENT_MAP.get(singleHostPort);
      if (null != httpClient) {
        httpClient.close();
      }
    } catch (IOException e) {
      LOGGER.error("close http connection pool exception: ", e);
    }
  }

  private static int getEvictableIdleTimeMillis() {
    int evictableIdleTimeMillis = config.getPool().getEvictableIdleTimeMillis();
    // 低于10s时取10s，在这个基础上再减去1s, 这1s防止刚好超时还没来得及被清理的极端情况出现
    // When it is lower than 10s, take 10s, and subtract 1s from this basis
    // This 1s prevents the extreme situation that just timed out and has not had time to be cleaned up
    return Math.max(evictableIdleTimeMillis, 10_000) - 1_000;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    config = applicationContext.getBean(HttpConnectionConfig.class);
    HostnameVerifier hostnameVerifier = ExceptionUtil.get(
        () -> applicationContext.getBean(HostnameVerifier.class), true);
    // 重新初始化Http连接池管理对象（第一次初始化在静态块中取不到spring的配置）
    // Reinitializing the Http connection pool management object (the first time initializing a configuration that does not fetch spring in a static block)
    initManager(config, hostnameVerifier);
  }

  private static class MonitorHttpWorker implements Runnable {

    private final PoolingHttpClientConnectionManager manager;

    MonitorHttpWorker(PoolingHttpClientConnectionManager manager) {
      super();
      this.manager = manager;
    }

    /**
     * @author: Ares
     * @description: Http连接监控，关闭异常和空闲连接
     * @description: Http connection monitoring, closing exceptions and idle connections
     * @time: 2019-08-17 09:39:00
     * @params: []
     * @return: void
     */
    @Override
    public void run() {
      // 关闭异常连接
      manager.closeExpired();
      int evictableIdleTimeMillis = getEvictableIdleTimeMillis();
      // 关闭空闲的连接
      manager.closeIdle(TimeValue.ofMilliseconds(evictableIdleTimeMillis));
      LOGGER.debug("close expired connections and over {} ms idle connections",
          evictableIdleTimeMillis);
    }
  }

}


