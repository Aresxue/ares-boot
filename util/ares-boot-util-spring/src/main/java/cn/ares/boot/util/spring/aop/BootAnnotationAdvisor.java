package cn.ares.boot.util.spring.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * @author: Ares
 * @time: 2023-05-31 23:52:25
 * @description: 更强大的注解切面
 * @description: More powerful annotation advisor
 * @version: JDK 1.8
 */
public class BootAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

  private static final long serialVersionUID = 8857290990179021140L;

  private final Advice advice;

  private final Pointcut pointcut;

  private final Class<? extends Annotation> annotation;

  public BootAnnotationAdvisor(@NonNull MethodInterceptor advice,
      @NonNull Class<? extends Annotation> annotation) {
    this.advice = advice;
    this.annotation = annotation;
    this.pointcut = buildPointcut();
  }

  @Override
  public Pointcut getPointcut() {
    return this.pointcut;
  }

  @Override
  public Advice getAdvice() {
    return this.advice;
  }

  @Override
  public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
    if (this.advice instanceof BeanFactoryAware) {
      ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
    }
  }

  private Pointcut buildPointcut() {
    Pointcut matchingPointcut = new AnnotationMatchingPointcut(annotation, true);
    Pointcut methodPoint = new AnnotationMethodPoint(annotation);
    return new ComposablePointcut(matchingPointcut).union(methodPoint);
  }

  /**
   * In order to be compatible with the spring lower than 5.0
   */
  private static class AnnotationMethodPoint implements Pointcut {

    private final Class<? extends Annotation> annotationType;

    public AnnotationMethodPoint(Class<? extends Annotation> annotationType) {
      Assert.notNull(annotationType, "Annotation type must not be null");
      this.annotationType = annotationType;
    }

    @Override
    @NonNull
    public ClassFilter getClassFilter() {
      return ClassFilter.TRUE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
      return new AnnotationMethodMatcher(annotationType);
    }

    private static class AnnotationMethodMatcher extends StaticMethodMatcher {

      private final Class<? extends Annotation> annotationType;

      public AnnotationMethodMatcher(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
      }

      @Override
      public boolean matches(Method method, Class<?> targetClass) {
        if (matchesMethod(method)) {
          return true;
        }
        // Proxy classes never have annotations on their redeclared methods.
        if (Proxy.isProxyClass(targetClass)) {
          return false;
        }
        // The method may be on an interface, so let's check on the target class as well.
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        return (specificMethod != method && matchesMethod(specificMethod));
      }

      private boolean matchesMethod(Method method) {
        return AnnotatedElementUtils.hasAnnotation(method, this.annotationType);
      }
    }
  }

}