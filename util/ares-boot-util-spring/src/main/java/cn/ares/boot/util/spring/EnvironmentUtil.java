package cn.ares.boot.util.spring;

import static cn.ares.boot.util.common.constant.StringConstant.EMPTY;
import static cn.ares.boot.util.common.constant.SymbolConstant.COMMA;
import static cn.ares.boot.util.common.constant.SymbolConstant.MINUS;
import static cn.ares.boot.util.common.constant.SymbolConstant.SPOT;

import cn.ares.boot.util.common.ArrayUtil;
import cn.ares.boot.util.common.MapUtil;
import cn.ares.boot.util.common.StringUtil;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * @author: Ares
 * @time: 2021-06-04 15:13:00
 * @description: 环境工具
 * @description: Environment util
 * @version: JDK 1.8
 */
public class EnvironmentUtil {

  public static final String SPRING_AUTOCONFIGURE_EXCLUDE = "spring.autoconfigure.exclude";
  private static final String STANDARD_SERVLET_ENVIRONMENT = "org.springframework.web.context.support.StandardServletEnvironment";

  private static final String BOOTSTRAP_YML = "applicationConfig: [classpath:/bootstrap.yml]";
  private static final String BOOTSTRAP_YAML = "applicationConfig: [classpath:/bootstrap.yaml]";
  private static final String BOOTSTRAP_PROPERTIES = "applicationConfig: [classpath:/bootstrap.properties]";


  /**
   * @author: Ares
   * @description: 根据前缀获取排除的属性源以外的spring的配置信息（不保留前缀）
   * @description: Get the configuration information of spring other than the excluded property
   * source according to the prefix (the prefix is not preserved)
   * @time: 2020-03-19 00:15:00
   * @params: [environment, prefix, excludePropertySourceNames] 环境信息实体, 排除的属性源名称
   * @return: java.util.Properties
   */
  public static Properties getPropertiesByPrefix(Environment environment, String prefix,
      String... excludePropertySourceNames) {
    return getPropertiesByPrefix(environment, prefix, false, excludePropertySourceNames);
  }

  /**
   * @author: Ares
   * @description: 根据前缀获取spring的配置信息（不保留前缀）
   * @description: Get spring configuration information based on prefix (prefix is not preserved)
   * @time: 2022-06-08 17:43:57
   * @params: [environment, prefix] 环境信息实体，前缀
   * @return: java.util.Properties
   */
  public static Properties getPropertiesByPrefix(Environment environment, String prefix) {
    return getPropertiesByPrefix(environment, prefix, EMPTY);
  }

  /**
   * @author: Ares
   * @description: 根据前缀获取spring的配置信息（是否保留前缀）
   * @description: Get spring configuration information based on the prefix (whether to keep the
   * prefix or not)
   * @time: 2022-06-08 17:43:57
   * @params: [environment, prefix, retainPrefix] 环境信息实体，前缀，是否保留前缀
   * @return: java.util.Properties
   */
  public static Properties getPropertiesByPrefix(Environment environment, String prefix,
      boolean retainPrefix) {
    return getPropertiesByPrefix(environment, prefix, retainPrefix, EMPTY);
  }

  /**
   * @author: Ares
   * @description: 根据前缀获取排除的属性源以外的spring的配置信息（是否保留前缀）
   * @description: Get the configuration information of spring other than the excluded property
   * source according to the prefix (whether to keep the prefix or not)
   * @time: 2020-03-19 00:15:00
   * @params: [environment, prefix, retainPrefix] 环境信息实体, 前缀, 是否保留前缀
   * @return: java.util.Properties
   */
  public static Properties getPropertiesByPrefix(Environment environment, String prefix,
      boolean retainPrefix, String... excludePropertySourceNames) {
    Properties properties = new Properties();
    ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
    configurableEnvironment.getPropertySources().forEach(propertySource -> {
      if (propertySource instanceof MapPropertySource && !ArrayUtil.contains(
          excludePropertySourceNames, propertySource.getName())) {
        MapPropertySource mapPropertySource = (MapPropertySource) propertySource;
        mapPropertySource.getSource().forEach((key, value) -> {
          if (key.startsWith(prefix)) {
            String propertyKey = retainPrefix ? key : StringUtil.replace(key, prefix + SPOT, EMPTY);
            if (!properties.containsKey(propertyKey)) {
              properties.put(propertyKey, value);
              properties.setProperty(propertyKey, String.valueOf(value));
            }
          }
        });
      }
    });
    return properties;
  }

  /**
   * @author: Ares
   * @description: 增加spring需要排除的类, 如一些AutoConfigure
   * @description: Add classes that spring needs to exclude, such as some AutoConfigure
   * @time: 2021-07-06 11:14:00
   * @params: [environment, config, excludeClassNames] 环境信息实体，配置，排除类名称
   * @return: void
   */
  public static void addSpringExclude(Environment environment, Properties config,
      String excludeClassNames) {
    String exclude = environment.getProperty(SPRING_AUTOCONFIGURE_EXCLUDE);
    if (StringUtil.isEmpty(exclude)) {
      config.put(SPRING_AUTOCONFIGURE_EXCLUDE, excludeClassNames);
    } else {
      System.setProperty(SPRING_AUTOCONFIGURE_EXCLUDE, exclude + COMMA + excludeClassNames);
    }
  }

  /**
   * @author: Ares
   * @description: 增加spring需要排除的类, 如一些AutoConfigure
   * @description: Add classes that spring needs to exclude, such as some AutoConfigure
   * @time: 2022-06-08 17:46:45
   * @params: [environment, config, excludeClass] 环境信息实体，配置，排除类
   * @return: void
   */
  public static void addSpringExclude(Environment environment, Properties config,
      Class<?>... excludeClass) {
    if (excludeClass.length > 0) {
      String excludeClasses = Arrays.stream(excludeClass).map(Class::getCanonicalName).collect(
          Collectors.joining(COMMA));
      addSpringExclude(environment, config, excludeClasses);
    }
  }


  /**
   * @author: Ares
   * @description: 替换属性前缀 做rpc->dubbo, log->log4j2等的转义
   * @description: Replace attribute prefixes Do rpc->dubbo, log->log4j2, etc.
   * @time: 2021-07-08 19:57:00
   * @params: [properties, prefix, newPrefix] 属性, 前缀, 新前缀
   * @return: java.util.Properties
   */
  public static Properties replacePropertiesPrefix(Properties properties, String prefix,
      String newPrefix) {
    Properties newProperties = new Properties();
    properties.forEach((key, value) -> {
      String keyStr = String.valueOf(key);
      if (keyStr.startsWith(prefix)) {
        key = newPrefix + keyStr.substring(prefix.length());
      }
      newProperties.put(key, value);
    });
    return newProperties;
  }

  /**
   * @author: Ares
   * @description: 替换属性前缀返回Map
   * @description: Replacing the property prefix returns a Map
   * @time: 2022-06-08 17:47:45
   * @params: [properties, prefix, newPrefix] 属性, 前缀, 新前缀
   * @return: java.util.Map<java.lang.String, java.lang.String>
   */
  public static Map<String, String> replacePropertiesPrefixToMap(Properties properties,
      String prefix, String newPrefix) {
    Map<String, String> map = MapUtil.newHashMap();
    properties.forEach((key, value) -> {
      String keyStr = String.valueOf(key);
      if (keyStr.startsWith(prefix)) {
        key = newPrefix + keyStr.substring(prefix.length());
      }
      map.put(String.valueOf(key), String.valueOf(value));
    });
    return map;
  }

  /**
   * @author: Ares
   * @description: 将属性添加前缀后返回
   * @description: Returns after prefixing the property
   * @time: 2021-06-03 15:55:00
   * @params: [properties, prefix] 属性，前缀
   * @return: java.util.Properties
   */
  public static Properties getPrefixProperties(Properties properties, String prefix) {
    Properties newProperties = new Properties();
    properties.stringPropertyNames().forEach(propertyName -> {
      // 将属性的key中的中划线-转为小驼峰式, 如druid.test-while-idle转为如druid.testWhileIdle
      // Convert the underline in the key of the property to a small camel case,
      // such as druid.test-while-idle, such as druid.testWhileIdle
      String newPropertyName;
      if (propertyName.contains(MINUS)) {
        newPropertyName = StringUtil.strikeToLittleCamelCase(propertyName);
      } else {
        newPropertyName = propertyName;
      }
      newProperties.put(prefix + newPropertyName, properties.get(propertyName));
    });
    return newProperties;
  }

  /**
   * @author: Ares
   * @description: 是否是Servlet环境
   * @description: Servlet environment or not
   * @time: 2023-05-08 17:12:16
   * @params: [environment] 环境
   * @return: boolean 是否是Servlet环境
   */
  public static boolean isServletEnvironment(ConfigurableEnvironment environment) {
    return STANDARD_SERVLET_ENVIRONMENT.equals(environment.getClass().getCanonicalName());
  }

  /**
   * @author: Ares
   * @description: 添加属性源，如果存在bootstrap属性源添加在其后否则添加到最后
   * @description: Add the property source, if there is a bootstrap property source add it after it
   * else add it at the end
   * @time: 2022-06-08 17:49:24
   * @params: [propertySources, propertiesPropertySource] 属性源聚合，属性源
   * @return: void
   */
  public static void addBefore(MutablePropertySources propertySources,
      PropertiesPropertySource propertiesPropertySource) {
    if (propertySources.contains(BOOTSTRAP_PROPERTIES)) {
      propertySources.addBefore(BOOTSTRAP_PROPERTIES, propertiesPropertySource);
    } else if (propertySources.contains(BOOTSTRAP_YAML)) {
      propertySources.addBefore(BOOTSTRAP_YAML, propertiesPropertySource);
    } else if (propertySources.contains(BOOTSTRAP_YML)) {
      propertySources.addBefore(BOOTSTRAP_YML, propertiesPropertySource);
    } else {
      propertySources.addLast(propertiesPropertySource);
    }
  }

  /**
   * @author: Ares
   * @description: 如果属性不存在则设置属性
   * @description: Set the property if it does not exist
   * @time: 2024-07-19 12:07:10
   * @params: [environment, properties, key, value] 环境, 属性组, 键, 值
   * @return: void
   */
  public static void setPropertyIfAbsent(ConfigurableEnvironment environment, Properties properties,
      String key, String value) {
    if (!environment.containsProperty(key)) {
      properties.put(key, value);
    }
  }

}
