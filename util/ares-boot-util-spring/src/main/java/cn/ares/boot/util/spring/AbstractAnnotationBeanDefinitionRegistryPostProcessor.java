package cn.ares.boot.util.spring;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * @author: Ares
 * @time: 2022-05-25 23:15:29
 * @description: 按照想要的注解扫描出相应的class
 * @description: Scan out the corresponding class according to the desired annotation
 * @version: JDK 1.8
 */
public abstract class AbstractAnnotationBeanDefinitionRegistryPostProcessor implements
    BeanDefinitionRegistryPostProcessor, EnvironmentAware, ApplicationContextAware {

  private ApplicationContext applicationContext;
  private Environment environment;

  private final Class<? extends Annotation> annotationType;

  public AbstractAnnotationBeanDefinitionRegistryPostProcessor(
      Class<? extends Annotation> annotationType) {
    Assert.notNull(annotationType, "The argument of annotation' type must not empty");
    this.annotationType = annotationType;
  }

  @Override
  public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory)
      throws BeansException {

  }

  @Override
  public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry)
      throws BeansException {
    BeanNameGenerator beanNameGenerator = SpringUtil.resolveBeanNameGenerator(registry);

    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
    scanner.setBeanNameGenerator(beanNameGenerator);

    // Get all scan package
    Set<String> scanPackageSet = SpringUtil.getComponentScanningPackageSet(registry);

    scanPackageSet.forEach(scanPackage -> {
      Set<BeanDefinitionHolder> beanDefinitionHolders = SpringUtil.findServiceBeanDefinitionHolders(
          scanner, scanPackage, registry, beanNameGenerator);

      for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
        Class<?> clazz = SpringUtil.resolveClass(beanDefinitionHolder, null);
        if (null == clazz) {
          continue;
        }

        Annotation annotation = AnnotationUtils.findAnnotation(clazz, annotationType);
        if (null != annotation) {
          registry(registry, clazz, annotation);
        }
      }
    });
  }

  /**
   * @author: Ares
   * @description: 注册bean定义
   * @description: Registry BeanDefinition
   * @time: 2022-05-25 23:23:40
   * @params: [registry, clazz, annotation] bean定义注册器，类，注解
   * @return: void
   */
  public abstract void registry(BeanDefinitionRegistry registry, Class<?> clazz,
      Annotation annotation);


  @Override
  public void setApplicationContext(@NonNull ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void setEnvironment(@Nullable Environment environment) {
    this.environment = environment;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public Environment getEnvironment() {
    return environment;
  }

}
