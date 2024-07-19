package cn.ares.boot.starter.web.config;


import static cn.ares.boot.util.common.constant.StringConstant.TRUE;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: Ares
 * @time: 2021-11-13 17:13:00
 * @description: 跨域配置
 * @description: cors config
 * @version: JDK 1.8
 */
@Configuration
@ConditionalOnProperty(name = "ares.web.cors.enable", havingValue = TRUE)
public class BootCorsConfiguration implements WebMvcConfigurer {

  @Value("${ares.web.cors.mapping:/**}")
  private String mapping;

  @Value("${ares.web.cors.allowed-origins:*}")
  private String allowedOrigins;

  @Value("${c2f.web.cors.allowed-methods:*}")
  private String allowedMethods;

  @Value("${ares.web.cors.allowed-headers:*}")
  private String[] allowedHeaders;

  @Value("${ares.web.cors.exposed-headers:}")
  private String[] exposedHeaders;

  @Value("${ares.web.cors.max-age:3600}")
  private Integer maxAge;

  @Value("${ares.web.cors.allow-credentials:true}")
  private Boolean allowCredentials;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // 指定哪些接口支持跨域
    // Specify which interfaces support cross-domain
    registry.addMapping(mapping)
        // 开放哪些ip、端口、域名的访问权限, 星号表示开放所有域
        // Open the access rights of which IP, port, and domain name, and the asterisk indicates opening all domains
        .allowedOrigins(allowedOrigins)
        .allowCredentials(allowCredentials)
        // 开放哪些Http方法，允许跨域访问
        // Open which Http methods, allowing cross-domain access
        .allowedMethods(allowedMethods)
        .allowedHeaders(allowedHeaders)
        // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        // Expose which header information (because cross-domain access cannot obtain all header information by default)
        .exposedHeaders(exposedHeaders)
        // 跨域允许时间
        // Cross-domain allowed time
        .maxAge(maxAge);
  }

}