package cn.ares.boot.starter.datasource.config;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.starter.datasource.config.typeHandler.ConfigurableLocalDateTimeTypeHandler;
import cn.ares.boot.starter.datasource.config.typeHandler.ConfigurableLocalDateTypeHandler;
import cn.ares.boot.starter.datasource.config.typeHandler.ConfigurableLocalTimeTypeHandler;
import cn.ares.boot.starter.datasource.extension.ExtensionSqlInjector;
import cn.ares.boot.starter.datasource.extension.LogicDeleteCommonFieldMetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author: Ares
 * @time: 2024-07-02 14:10:16
 * @description: 数据源配置
 * @version: JDK 1.8
 */
@Configuration
@Role(value = ROLE_INFRASTRUCTURE)
public class BootDataSourceConfiguration {

  @Bean
  @ConditionalOnMissingBean
  @Role(value = ROLE_INFRASTRUCTURE)
  public MetaObjectHandler metaObjectHandler() {
    return new LogicDeleteCommonFieldMetaObjectHandler();
  }

  @Bean
  @ConditionalOnMissingBean
  @Role(value = ROLE_INFRASTRUCTURE)
  public DefaultSqlInjector mySqlInjector() {
    return new ExtensionSqlInjector();
  }

  @Bean
  @ConditionalOnMissingBean
  @Role(value = ROLE_INFRASTRUCTURE)
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
    interceptor.addInnerInterceptor(new BootPaginationInnerInterceptor());
    return interceptor;
  }

  @Bean
  @ConditionalOnMissingBean
  @Role(value = ROLE_INFRASTRUCTURE)
  public ConfigurableLocalDateTimeTypeHandler configurableLocalDateTimeTypeHandler(
      @Value("${ares.application.datasource.local-date-time.format:yyyy-MM-dd HH:mm:ss.SSS}") String format) {
    return new ConfigurableLocalDateTimeTypeHandler(DateTimeFormatter.ofPattern(format));
  }

  @Bean
  @ConditionalOnMissingBean
  @Role(value = ROLE_INFRASTRUCTURE)
  public ConfigurableLocalDateTypeHandler configurableLocalDateTypeHandler(
      @Value("${ares.application.datasource.local-date.format:yyyy-MM-dd}") String format) {
    return new ConfigurableLocalDateTypeHandler(DateTimeFormatter.ofPattern(format));
  }

  @Bean
  @ConditionalOnMissingBean
  @Role(value = ROLE_INFRASTRUCTURE)
  public ConfigurableLocalTimeTypeHandler configurableLocalTimeTypeHandler(
      @Value("${ares.application.datasource.local-time.format:HH:mm:ss.SSS}") String format) {
    return new ConfigurableLocalTimeTypeHandler(DateTimeFormatter.ofPattern(format));
  }

}
