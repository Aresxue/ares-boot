package cn.ares.boot.starter.datasource.config;

import static cn.ares.boot.util.common.constant.MiddlewareType.DATABASE;
import static cn.ares.boot.util.common.constant.StringConstant.FALSE;

import cn.ares.boot.base.log.util.LoggerUtil;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * @author: Ares
 * @time: 2024-07-02 19:23:33
 * @description: 数据库配置加载
 * @description: Database configuration loading
 * @version: JDK 1.8
 */
public class DataSourceEnvironmentPostProcessor implements EnvironmentPostProcessor {

  private static final AtomicBoolean PROCESSED = new AtomicBoolean(false);

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment,
      SpringApplication application) {
    if (PROCESSED.compareAndSet(false, true)) {
      Properties datasourceConfig = new Properties();
      datasourceConfig.put("mybatis-plus.globalConfig.banner", FALSE);
      environment.getPropertySources()
          .addLast(new PropertiesPropertySource(DATABASE.getName(), datasourceConfig));
      LoggerUtil.infoDeferred("datasource config load success");
    }
  }

}
