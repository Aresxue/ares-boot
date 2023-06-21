package cn.ares.boot.util.spring;


import static cn.ares.boot.util.common.StringUtil.isEmpty;
import static cn.ares.boot.util.common.constant.SymbolConstant.SPOT;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_SUPPORT;
import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;

import cn.ares.boot.util.common.MapUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.common.primitive.BooleanUtil;
import cn.ares.boot.util.common.primitive.IntegerUtil;
import cn.ares.boot.util.common.primitive.LongUtil;
import cn.ares.boot.util.spring.annotation.SpringBooleanValue;
import cn.ares.boot.util.spring.annotation.SpringIntegerValue;
import cn.ares.boot.util.spring.annotation.SpringLongValue;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Role;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * @author: Ares
 * @time: 2021-05-31 16:18:00
 * @description: Spring相关处理的工具，部分功能需要在bean初始化之后使用
 * @description: Spring related processing tools, some functions need to be used after bean
 * initialization
 * @version: JDK 1.8
 */
@Component(value = "aresSpringUtil")
@Role(value = ROLE_SUPPORT)
public class SpringUtil implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringUtil.class);

  private static ApplicationContext applicationContext;
  private static ApplicationContext firstApplicationContext;
  private static SingletonBeanRegistry beanFactory;
  private static ClassLoader classLoader;

  /**
   * @author: Ares
   * @description: 获取扫描的包名
   * @description: Get component scanning packages
   * @time: 2021-11-24 18:14:00
   * @params: [registry, allowEmpty] bean definition registry, allow package name empty
   * @params: [registry, allowEmpty] bean定义注册器, 是否允许包名为空
   * @return: java.util.Set<java.lang.String> package set
   */
  public static Set<String> getComponentScanningPackages(BeanDefinitionRegistry registry,
      boolean allowEmpty) {
    Set<String> packageSet = new LinkedHashSet<>();
    String[] names = registry.getBeanDefinitionNames();
    for (String name : names) {
      BeanDefinition definition = registry.getBeanDefinition(name);
      if (definition instanceof AnnotatedBeanDefinition) {
        AnnotatedBeanDefinition annotatedDefinition = (AnnotatedBeanDefinition) definition;
        SpringUtil.addComponentScanningPackageSet(packageSet, annotatedDefinition.getMetadata());
      }
    }
    if (!allowEmpty) {
      packageSet = packageSet.stream().filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
    }
    return packageSet;
  }

  /**
   * @author: Ares
   * @description: 获取要扫描的包的集合且如果为空且剔除
   * @description: Get component scanning packages with don't allow empty
   * @time: 2021-12-21 19:48:35
   * @params: [registry] bean definition registry bean定义注册器
   * @return: java.util.Set<java.lang.String> scan package set
   */
  public static Set<String> getComponentScanningPackageSet(BeanDefinitionRegistry registry) {
    return getComponentScanningPackages(registry, false);
  }


  /**
   * @author: Ares
   * @description: 通过名称获取bean
   * @description: Get bean by name
   * @time: 2021-05-31 16:21:00
   * @params: [beanName] Bean的名称
   * @return: java.lang.Object Bean
   */
  public static Object getBean(String beanName) {
    return getApplicationContext().getBean(beanName);
  }

  /**
   * @author: Ares
   * @description: 通过class获取Bean
   * @description: Get Bean by class
   * @time: 2021-05-31 16:22:00
   * @params: [clazz] class
   * @return: T Bean
   */
  public static <T> T getBean(Class<T> clazz) {
    return getApplicationContext().getBean(clazz);
  }

  /**
   * @author: Ares
   * @description: 通过bean名称和class获取Bean
   * @description: Get bean by bean name and class
   * @time: 2021-05-31 16:23:00
   * @params: [beanName, clazz] Bean名称, class
   * @return: T Bean
   */
  public static <T> T getBean(String beanName, Class<T> clazz) {
    return getApplicationContext().getBean(beanName, clazz);
  }

  /**
   * @author: Ares
   * @description: 判断是否包含Bean
   * @description: Determine if Bean is included
   * @time: 2021-05-31 16:26:00
   * @params: [beanName] Bean的名称
   * @return: boolean 是否包含
   */
  public static boolean containsBean(String beanName) {
    return getApplicationContext().containsBean(beanName);
  }

  /**
   * @author: Ares
   * @description: 判断Bean是否是单例
   * @description: Determine if a bean is a singleton
   * @time: 2021-05-31 16:26:00
   * @params: [beanName] Bean的名称
   * @return: boolean 是否单例
   */
  public static boolean isSingleton(String beanName) {
    return getApplicationContext().isSingleton(beanName);
  }

  /**
   * @author: Ares
   * @description: 获取Bean的Class
   * @description: Get the Class of the Bean
   * @time: 2021-05-31 16:28:00
   * @params: [beanName] Bean的名称
   * @return: java.lang.Class Class
   */
  public static Class<?> getType(String beanName) {
    return getApplicationContext().getType(beanName);
  }

  /**
   * @author: Ares
   * @description: 通过类型获取所有bean
   * @description: Gets all beans by type
   * @time: 2022-06-08 18:06:48
   * @params: [clazz] 类
   * @return: java.util.Map<java.lang.String, T> bean map
   */
  public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
    return getApplicationContext().getBeansOfType(clazz);
  }

  /**
   * @author: Ares
   * @description: 通过类型获取所有bean(包括父应用上下文)
   * @description: Get all beans by type (including parent application context)
   * @time: 2022-12-30 14:36:04
   * @params: [clazz] 类
   * @return: java.util.Map<java.lang.String, T> bean map
   */
  public static <T> Map<String, T> getAllBeansOfType(Class<T> clazz) {
    Map<String, T> beanMap = MapUtil.newHashMap();
    getAllBeansOfType(clazz, getApplicationContext(), beanMap);
    return beanMap;
  }

  private static <T> void getAllBeansOfType(Class<T> clazz,
      ApplicationContext applicationContext, Map<String, T> beanMap) {
    beanMap.putAll(applicationContext.getBeansOfType(clazz));
    Optional.ofNullable(applicationContext.getParent()).ifPresent(
        parentApplicationContext -> getAllBeansOfType(clazz, parentApplicationContext, beanMap));
  }

  /**
   * @author: Ares
   * @description: 根据注解获取名称数组
   * @description: Gets an array of names from the annotationType
   * @time: 2023-05-11 11:32:34
   * @params: [annotationType] 注解类型
   * @return: java.lang.String[] bean名称数组
   */
  public static String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
    return getApplicationContext().getBeanNamesForAnnotation(annotationType);
  }


  /**
   * @author: Ares
   * @description: 根据注解获取对象映射
   * @description: Gets the object map from the annotation
   * @time: 2023-05-11 11:39:49
   * @params: [annotationType] 注解类型
   * @return: java.util.Map<java.lang.String, java.lang.Object>
   */
  public static Map<String, Object> getBeansWithAnnotation(
      Class<? extends Annotation> annotationType) {
    return getApplicationContext().getBeansWithAnnotation(annotationType);
  }

  /**
   * @author: Ares
   * @description: 根据类获取名称数组
   * @description: Gets an array of names from the class
   * @time: 2023-05-11 11:32:34
   * @params: [clazz] 类
   * @return: java.lang.String[] bean名称数组
   */
  public static String[] getBeanNamesForType(Class<?> type) {
    return getApplicationContext().getBeanNamesForType(type);
  }

  /**
   * @author: Ares
   * @description: 根据类获取名称数组（可指定是否包括非单例以及早期初始化）
   * @description: Gets an array of names by class (you can specify whether to include
   * non-singletons and early initialization)
   * @time: 2023-05-11 11:42:34
   * @params: [clazz, includeNonSingletons, allowEagerInit] 类，
   * @return: java.lang.String[] bean名称数组
   */
  public static String[] getBeanNamesForType(Class<?> clazz, boolean includeNonSingletons,
      boolean allowEagerInit) {
    return getApplicationContext().getBeanNamesForType(clazz, includeNonSingletons, allowEagerInit);
  }

  /**
   * @author: Ares
   * @description: 获取对象定义名称数组
   * @description: Gets an array of object definition names
   * @time: 2023-05-11 12:14:21
   * @params: []
   * @return: java.lang.String[] 对象定义名称数组
   */
  public static String[] getBeanDefinitionNames() {
    return getApplicationContext().getBeanDefinitionNames();
  }

  /**
   * @author: Ares
   * @description: 注册单例对象到spring容器
   * @description: Register singleton objects into the spring container
   * @time: 2023-05-11 12:15:00
   * @params: [beanName, singletonObject] 对象名称，单例对象
   * @return: void
   */
  public static void registerSingleton(String beanName, Object singletonObject) {
    beanFactory.registerSingleton(beanName, singletonObject);
  }

  /**
   * @author: Ares
   * @description: 注册对象到spring容器（指定构造逻辑和对象定义自定义处理）
   * @description: Register objects into the spring container (specifying construction logic and
   * object definition custom handling)
   * @time: 2023-05-11 12:18:16
   * @params: [beanName, beanClass, supplier, customizers] 对象名称，对象类，构造bean的逻辑，对象定义自定义处理数组
   * @return: void
   */
  public static <T> void registerBean(String beanName, Class<T> beanClass,
      Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {
    GenericApplicationContext genericApplicationContext = (GenericApplicationContext) getApplicationContext();
    genericApplicationContext.registerBean(beanName, beanClass, supplier, customizers);
  }

  /**
   * @author: Ares
   * @description: 获取环境
   * @description: Get Environment
   * @time: 2023-05-11 12:19:43
   * @params: []
   * @return: org.springframework.core.env.Environment 环境
   */
  public static Environment getEnvironment() {
    return getApplicationContext().getEnvironment();
  }


  /**
   * @author: Ares
   * @description: 获取所有的Bean的类
   * @description: Gets all the Bean classes
   * @time: 2019-06-11 15:32:00
   * @params: []
   * @return: java.util.List<java.lang.Class ?>
   */
  public static List<Class<?>> getAllBeans() {
    List<Class<?>> result = new LinkedList<>();
    String[] beans = getApplicationContext().getBeanDefinitionNames();
    for (String beanName : beans) {
      Class<?> clazz = getType(beanName);
      result.add(clazz);
    }
    return result;
  }

  /**
   * @author: Ares
   * @description: 容器启动完成后在运行时根据前缀获取spring的配置信息
   * @description: After the container is started, the configuration information of spring is
   * obtained according to the prefix at runtime
   * @time: 2020-03-19 00:15:00
   * @params: [prefix, retainPrefix] 前缀, 是否保留前缀
   * @return: java.util.Properties
   */
  public static Properties getPropertiesByPrefix(String prefix, boolean retainPrefix) {
    Properties properties = new Properties();
    StandardEnvironment standardEnvironment = (StandardEnvironment) getApplicationContext().getEnvironment();
    standardEnvironment.getPropertySources().forEach(propertySource -> {
      if (propertySource instanceof MapPropertySource) {
        MapPropertySource mapPropertySource = (MapPropertySource) propertySource;
        mapPropertySource.getSource().forEach((key, value) -> {
          if (key.startsWith(prefix)) {
            String propertyKey = retainPrefix ? key : StringUtil.replace(key, prefix + SPOT, "");
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
   * @description: 根据解析的文本从环境中获取值
   * @description: Gets value from the environment based on parsed text
   * @time: 2023-05-11 12:20:33
   * @params: [text] 解析的文本
   * @return: java.lang.String 值
   */
  public static String resolvePlaceholders(String text) {
    return getEnvironment().resolvePlaceholders(text);
  }

  /**
   * @author: Ares
   * @description: 从环境中根据键获取值
   * @description: Get value from the environment by key
   * @time: 2023-05-11 12:21:46
   * @params: [key] 键
   * @return: java.lang.String 值
   */
  public static String getProperty(String key) {
    return getEnvironment().getProperty(key);
  }

  /**
   * @author: Ares
   * @description: 根据键获取值（不存在时取默认值）
   * @description: Get value by key (default if none exists)
   * @time: 2023-05-11 12:22:14
   * @params: [key, defaultValue] 键 ，默认值
   * @return: java.lang.String 值
   */
  public static String getProperty(String key, String defaultValue) {
    return getEnvironment().getProperty(key, defaultValue);
  }

  /**
   * @author: Ares
   * @description: 容器启动完成后在运行时根据前缀获取spring的配置信息(不保留前缀)
   * @description: After the container is started, the configuration information of spring is
   * obtained at runtime according to the prefix (the prefix is not reserved)
   * @time: 2020-03-19 00:15:00
   * @params: [prefix] 前缀
   * @return: java.util.Properties
   */
  public static Properties getPropertiesByPrefix(String prefix) {
    return getPropertiesByPrefix(prefix, false);
  }

  /**
   * @author: Ares
   * @description: add componentScanning packages
   * @time: 2021-11-24 16:49:00
   * @params: [packages, metadata] 包，元数据
   * @return: void
   */
  public static void addComponentScanningPackageSet(Set<String> packageSet,
      AnnotationMetadata metadata) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(
        metadata.getAnnotationAttributes(ComponentScan.class.getName(), true));
    if (attributes != null) {
      addPackages(packageSet, attributes.getStringArray("value"));
      addPackages(packageSet, attributes.getStringArray("basePackages"));
      addClasses(packageSet, attributes.getStringArray("basePackageClasses"));
      if (packageSet.isEmpty()) {
        packageSet.add(ClassUtils.getPackageName(metadata.getClassName()));
      }
    }
  }

  public static void addPackages(Set<String> packages, String[] values) {
    if (values != null) {
      Collections.addAll(packages, values);
    }
  }

  public static void addClasses(Set<String> packages, String[] values) {
    if (values != null) {
      for (String value : values) {
        packages.add(ClassUtils.getPackageName(value));
      }
    }
  }

  /**
   * @author: Ares
   * @description: 从注册器中获取对象名称生成器
   * @description: Gets the bean name generator from the registry
   * @time: 2023-05-11 12:25:00
   * @params: [registry] 对象定义注册器
   * @return: org.springframework.beans.factory.support.BeanNameGenerator 对象名称生成器
   */
  public static BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {
    BeanNameGenerator beanNameGenerator = null;

    if (registry instanceof SingletonBeanRegistry) {
      SingletonBeanRegistry singletonBeanRegistry = (SingletonBeanRegistry) registry;
      beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(
          CONFIGURATION_BEAN_NAME_GENERATOR);
    }

    if (beanNameGenerator == null) {
      LOGGER.info("BeanNameGenerator bean can't be found in BeanFactory with name ["
          + CONFIGURATION_BEAN_NAME_GENERATOR + "]");
      LOGGER.info(
          "BeanNameGenerator will be a instance of " + AnnotationBeanNameGenerator.class.getName()
              + " , it maybe a potential problem on bean name generation.");
      beanNameGenerator = new AnnotationBeanNameGenerator();
    }

    return beanNameGenerator;

  }

  /**
   * @author: Ares
   * @description: 根据指定的扫描器、扫描报名、对象定义注册器、对象名称生成器搜索对象定义持有者
   * @description: Find for object definitions according to the specified scanner, scan register,
   * object definition registry, object name generator
   * @time: 2023-05-11 12:26:04
   * @params: [scanner, packageToScan, registry, beanNameGenerator] 扫描器，要扫描的包，对象定义注册器，对象名称生成器
   * @return: java.util.Set<org.springframework.beans.factory.config.BeanDefinitionHolder>
   * 对象定义持有者不可重复集合
   */
  public static Set<BeanDefinitionHolder> findServiceBeanDefinitionHolders(
      ClassPathBeanDefinitionScanner scanner, String packageToScan, BeanDefinitionRegistry registry,
      BeanNameGenerator beanNameGenerator) {
    Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);

    Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<>(beanDefinitions.size());

    for (BeanDefinition beanDefinition : beanDefinitions) {

      String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
      BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition,
          beanName);
      beanDefinitionHolders.add(beanDefinitionHolder);
    }

    return beanDefinitionHolders;
  }

  /**
   * @author: Ares
   * @description: 根据对象定义持有者和类加载器解析出类
   * @description: The classes are resolved according to the object definition holder and the class
   * loader
   * @time: 2023-05-11 12:28:30
   * @params: [beanDefinitionHolder, classLoader] 对象定义持有者，类加载器
   * @return: java.lang.Class<?> 类
   */
  public static Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder,
      ClassLoader classLoader) {
    BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
    return resolveClass(beanDefinition, classLoader);
  }

  /**
   * @author: Ares
   * @description: 使用默认的类加载器从对象定义持有者中解析出类
   * @description: The class is resolved from the object definition holder using the default class
   * loader
   * @time: 2023-05-11 12:29:50
   * @params: [beanDefinitionHolder] 对象定义持有者
   * @return: java.lang.Class<?> 类
   */
  public static Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {
    BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
    return resolveClass(beanDefinition, classLoader);
  }

  /**
   * @author: Ares
   * @description: 根据对象定义和类加载器解析出类
   * @description: Parse out the classes based on the object definition and the class loader
   * @time: 2023-05-11 12:30:28
   * @params: [beanDefinition, classLoader] 对象定义，类加载器
   * @return: java.lang.Class<?> 类
   */
  public static Class<?> resolveClass(BeanDefinition beanDefinition, ClassLoader classLoader) {
    String beanClassName = beanDefinition.getBeanClassName();
    if (isEmpty(beanClassName)) {
      return null;
    }
    return ClassUtils.resolveClassName(beanClassName, classLoader);
  }

  /**
   * @author: Ares
   * @description: 使用默认的类加载器从对象定义中解析出类
   * @description: The class is resolved from the object definition using the default class loader
   * @time: 2023-05-11 12:31:33
   * @params: [beanDefinition] 对象定义
   * @return: java.lang.Class<?> 类
   */
  public static Class<?> resolveClass(BeanDefinition beanDefinition) {
    String beanClassName = beanDefinition.getBeanClassName();
    if (isEmpty(beanClassName)) {
      return null;
    }
    return ClassUtils.resolveClassName(beanClassName, classLoader);
  }

  /**
   * @author: Ares
   * @description: 是否为主应用上下文
   * @description: Is main ApplicationContext
   * @time: 2022-07-25 18:00:16
   * @params: [context] 应用上下文
   * @return: boolean 是否为主应用上下文
   */
  public static boolean isMainApplicationContext(ApplicationContext context) {
    return firstApplicationContext == context;
  }

  /**
   * @author: Ares
   * @description: 获取SpringIntegerValue对应的值
   * @time: 2022-07-27 15:55:58
   * @params: [springIntegerValue] spring整形注解
   * @return: int
   */
  public static int resolvePlaceholders(SpringIntegerValue springIntegerValue) {
    int value = springIntegerValue.value();
    String valueExpression = springIntegerValue.valueExpression();
    if (StringUtil.isNotEmpty(valueExpression)) {
      value = IntegerUtil.parseInteger(resolvePlaceholders(valueExpression));
    }
    return value;
  }

  /**
   * @author: Ares
   * @description: 获取SpringBooleanValue对应的值
   * @time: 2022-07-27 15:55:58
   * @params: [springBooleanValue] spring布尔形注解
   * @return: boolean
   */
  public static boolean resolvePlaceholders(SpringBooleanValue springBooleanValue) {
    boolean value = springBooleanValue.value();
    String valueExpression = springBooleanValue.valueExpression();
    if (StringUtil.isNotEmpty(valueExpression)) {
      value = BooleanUtil.parseBoolean(resolvePlaceholders(valueExpression));
    }
    return value;
  }

  /**
   * @author: Ares
   * @description: 获取SpringLongValue对应的值
   * @time: 2022-07-27 15:55:58
   * @params: [springLongValue] spring长整形形注解
   * @return: long
   */
  public static long resolvePlaceholders(SpringLongValue springLongValue) {
    long value = springLongValue.value();
    String valueExpression = springLongValue.valueExpression();
    if (StringUtil.isNotEmpty(valueExpression)) {
      value = LongUtil.parseLong(resolvePlaceholders(valueExpression));
    }
    return value;
  }

  private static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  @Override
  public void setApplicationContext(@NonNull ApplicationContext applicationContext)
      throws BeansException {
    SpringUtil.applicationContext = applicationContext;
    if (null == firstApplicationContext) {
      SpringUtil.firstApplicationContext = applicationContext;
    }
  }

  @Override
  public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
    SpringUtil.classLoader = classLoader;
  }

  @Override
  public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
    SpringUtil.beanFactory = (SingletonBeanRegistry) beanFactory;
  }

}
