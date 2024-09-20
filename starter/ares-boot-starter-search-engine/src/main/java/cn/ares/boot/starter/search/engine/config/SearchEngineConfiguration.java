package cn.ares.boot.starter.search.engine.config;

import static cn.ares.boot.starter.search.engine.config.SearchEngineEnvironmentPostProcessor.SEARCH_ENGINE_ENABLED;
import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.starter.search.engine.extension.ExtensionElasticsearchRestTemplate;
import cn.ares.boot.util.spring.ReflectionUtil;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.elasticsearch.client.HeapBufferedAsyncResponseConsumer;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;

/**
 * @author: Ares
 * @time: 2021-11-08 18:41
 * @description: 搜索引擎配置
 * @description: search engine configuration
 * @version: JDK 1.8
 */
@Configuration
@ConditionalOnProperty(name = SEARCH_ENGINE_ENABLED, havingValue = TRUE, matchIfMissing = true)
@Role(value = ROLE_INFRASTRUCTURE)
public class SearchEngineConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngineConfiguration.class);

  @Resource
  private SearchEngineProperties searchEngineProperties;

  @PostConstruct
  public void init() {
    int bufferLimit = searchEngineProperties.getBufferLimit();
    int defaultBufferLimit = 100 * 1024 * 1024;
    // 非默认值才做修改
    // If it is not the default value, modify it
    if (defaultBufferLimit != bufferLimit) {
      // 使用反射强行设置es查询buffer大小
      // Use reflection to force the setting of es query buffer size
      ReflectionUtil.setFinalFieldValue(RequestOptions.DEFAULT, "httpAsyncResponseConsumerFactory",
          (HttpAsyncResponseConsumerFactory) () -> new HeapBufferedAsyncResponseConsumer(
              bufferLimit));
    }
  }


  /**
   * @author: Ares
   * @description: 初始化Elasticsearch官方提供的RestHighLevelClient（高级客户端）
   * @description: Initialize the official provided RestHighLevelClient (high-level client)
   * @time: 2021-03-27 19:54:00 请求参数
   * @return: org.elasticsearch.client.RestHighLevelClient 高级客户端
   */
  @Bean
  @ConditionalOnMissingBean
  @Role(value = ROLE_INFRASTRUCTURE)
  public RestHighLevelClient restHighLevelClient() {
    LOGGER.info("search engine client init start");
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    // es账号密码
    // es username/password
    credentialsProvider.setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials(searchEngineProperties.getUsername(),
            searchEngineProperties.getPassword()));
    // 从配置信息中创建集群节点
    // Create cluster nodes from configuration information
    RestClientBuilder builder = RestClient.builder(buildHttpHost())
        .setHttpClientConfigCallback(httpClientBuilder ->
            httpClientBuilder.disableAuthCaching()
                .setMaxConnTotal(searchEngineProperties.getMaxConnTotal())
                .setMaxConnPerRoute(searchEngineProperties.getMaxConnPerRoute())
                .setKeepAliveStrategy((response, context) -> {
                  Args.notNull(response, "http response");
                  final HeaderElementIterator headerElementIterator = new BasicHeaderElementIterator(
                      response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                  while (headerElementIterator.hasNext()) {
                    final HeaderElement headerElement = headerElementIterator.nextElement();
                    final String param = headerElement.getName();
                    final String value = headerElement.getValue();
                    if (value != null && "timeout".equalsIgnoreCase(param)) {
                      try {
                        return Long.parseLong(value) * 1_000;
                      } catch (final NumberFormatException ignored) {
                      }
                    }
                  }
                  return searchEngineProperties.getKeepAlive();
                })
                .setDefaultCredentialsProvider(credentialsProvider))
        .setRequestConfigCallback(requestConfigBuilder ->
            requestConfigBuilder
                .setConnectionRequestTimeout(searchEngineProperties.getRequestTimeout())
                .setConnectTimeout(searchEngineProperties.getConnectTimeout())
                .setSocketTimeout(searchEngineProperties.getSocketTimeout()));
    return new RestHighLevelClient(builder);
  }


  private HttpHost[] buildHttpHost() {
    if (null == searchEngineProperties) {
      throw new UnsupportedOperationException("search engine properties is null");
    }

    List<String> clusterNodes = searchEngineProperties.getClusterNodes();
    if (clusterNodes.isEmpty()) {
      throw new UnsupportedOperationException("search engine cluster nodes is empty");
    }
    HttpHost[] httpHosts = new HttpHost[clusterNodes.size()];
    try {
      for (int i = 0; i < clusterNodes.size(); i++) {
        String[] ipPort = clusterNodes.get(i).split(":");
        int port = 9200;
        if (ipPort.length == 2) {
          port = Integer.parseInt(ipPort[1]);
        }
        String ip = ipPort[0];
        HttpHost httpHost = new HttpHost(ip, port, "http");
        httpHosts[i] = httpHost;
        LOGGER.info("search engine server address build success, ip: {}, port: {} ", ip, port);
      }
    } catch (Exception exception) {
      LOGGER.error("search engine server address build fail: ", exception);
      throw new UnsupportedOperationException("search engine server config load fail");
    }
    return httpHosts;
  }

  @Bean
  @ConditionalOnMissingBean(value = ElasticsearchOperations.class, name = "elasticsearchTemplate")
  @ConditionalOnBean(RestHighLevelClient.class)
  ElasticsearchRestTemplate elasticsearchTemplate(RestHighLevelClient client, ElasticsearchConverter converter) {
    return new ExtensionElasticsearchRestTemplate(client, converter);
  }

}
