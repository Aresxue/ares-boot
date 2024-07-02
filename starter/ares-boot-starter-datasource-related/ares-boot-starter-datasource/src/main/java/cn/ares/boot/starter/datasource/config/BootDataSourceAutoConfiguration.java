package cn.ares.boot.starter.datasource.config;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.starter.datasource.extension.ExtensionSqlInjector;
import cn.ares.boot.starter.datasource.extension.LogicDeleteCommonFieldMetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
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
public class BootDataSourceAutoConfiguration {

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

}
