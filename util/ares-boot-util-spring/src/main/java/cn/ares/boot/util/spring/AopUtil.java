package cn.ares.boot.util.spring;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author: Ares
 * @time: 2021-06-29 10:26:00
 * @description: Aop util
 * @version: JDK 1.8
 */
public class AopUtil {

  /**
   * @author: Ares
   * @description: 对象是否被aop代理
   * @description: Whether the object is proxied by aop
   * @time: 2022-06-08 16:58:41
   * @params: [object] 对象
   * @return: boolean 是否被代理
   */
  public static boolean isAopProxy(@Nullable Object object) {
    return object instanceof SpringProxy && (Proxy.isProxyClass(object.getClass()) || object
        .getClass().getName().contains("$$"));
  }

  /**
   * @author: Ares
   * @description: 对象是否被jdk代理
   * @description: Whether the object is proxied by jdk
   * @time: 2022-06-08 16:59:10
   * @params: [object] 对象
   * @return: boolean 是否jdk代理
   */
  public static boolean isJdkDynamicProxy(@Nullable Object object) {
    return object instanceof SpringProxy && Proxy.isProxyClass(object.getClass());
  }

  /**
   * @author: Ares
   * @description: 对象是否被cglib代理
   * @description: Whether the object is proxied by cglib
   * @time: 2022-06-08 16:59:10
   * @params: [object] 对象
   * @return: boolean 是否cglib代理
   */
  public static boolean isCglibProxy(@Nullable Object object) {
    return object instanceof SpringProxy && object.getClass().getName().contains("$$");
  }

  /**
   * @author: Ares
   * @description: 获取对象的真正的类
   * @description: Get the real class of the object
   * @time: 2022-06-08 17:00:18
   * @params: [candidate] 对象
   * @return: java.lang.Class<?>
   */
  public static Class<?> getTargetClass(Object candidate) {
    Assert.notNull(candidate, "Candidate object must not be null");
    Class<?> result = null;
    if (candidate instanceof TargetClassAware) {
      result = ((TargetClassAware) candidate).getTargetClass();
    }

    if (result == null) {
      result =
          isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass();
    }

    return result;
  }

  /**
   * @author: Ares
   * @description: 选择可调用的方法
   * @description: Select invocable method
   * @time: 2022-06-08 17:03:01
   * @params: [method, targetType] 方法，类
   * @return: java.lang.reflect.Method
   */
  public static Method selectInvocableMethod(Method method, @Nullable Class<?> targetType) {
    if (targetType == null) {
      return method;
    } else {
      Method methodToUse = MethodIntrospector.selectInvocableMethod(method, targetType);
      if (Modifier.isPrivate(methodToUse.getModifiers()) && !Modifier
          .isStatic(methodToUse.getModifiers()) && SpringProxy.class.isAssignableFrom(targetType)) {
        throw new IllegalStateException(String.format(
            "Need to invoke method '%s' found on proxy for target class '%s' but cannot be delegated to target bean. Switch its visibility to package or protected.",
            method.getName(), method.getDeclaringClass().getSimpleName()));
      } else {
        return methodToUse;
      }
    }
  }

  public static boolean isEqualsMethod(@Nullable Method method) {
    return ReflectionUtils.isEqualsMethod(method);
  }

  public static boolean isHashCodeMethod(@Nullable Method method) {
    return ReflectionUtils.isHashCodeMethod(method);
  }

  public static boolean isToStringMethod(@Nullable Method method) {
    return ReflectionUtils.isToStringMethod(method);
  }

  public static boolean isFinalizeMethod(@Nullable Method method) {
    return method != null && "finalize".equals(method.getName()) && method.getParameterCount() == 0;
  }

  public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
    Class<?> specificTargetClass =
        targetClass != null ? ClassUtils.getUserClass(targetClass) : null;
    Method resolvedMethod = ClassUtils.getMostSpecificMethod(method, specificTargetClass);
    return BridgeMethodResolver.findBridgedMethod(resolvedMethod);
  }

  public static boolean canApply(Pointcut pc, Class<?> targetClass) {
    return canApply(pc, targetClass, false);
  }

  public static boolean canApply(Pointcut pointcut, Class<?> targetClass,
      boolean hasIntroductions) {
    Assert.notNull(pointcut, "Pointcut must not be null");
    if (!pointcut.getClassFilter().matches(targetClass)) {
      return false;
    } else {
      MethodMatcher methodMatcher = pointcut.getMethodMatcher();
      if (methodMatcher == MethodMatcher.TRUE) {
        return true;
      } else {
        IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
        if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
          introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher) methodMatcher;
        }

        Set<Class<?>> classes = new LinkedHashSet<>();
        if (!Proxy.isProxyClass(targetClass)) {
          classes.add(ClassUtils.getUserClass(targetClass));
        }

        classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));

        for (Class<?> clazz : classes) {
          Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);

          for (Method method : methods) {
            if (introductionAwareMethodMatcher != null) {
              if (introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions)) {
                return true;
              }
            } else if (methodMatcher.matches(method, targetClass)) {
              return true;
            }
          }
        }

        return false;
      }
    }
  }

  public static boolean canApply(Advisor advisor, Class<?> targetClass) {
    return canApply(advisor, targetClass, false);
  }

  public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
    if (advisor instanceof IntroductionAdvisor) {
      return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
    } else if (advisor instanceof PointcutAdvisor) {
      PointcutAdvisor pca = (PointcutAdvisor) advisor;
      return canApply(pca.getPointcut(), targetClass, hasIntroductions);
    } else {
      return true;
    }
  }

  /**
   * @author: Ares
   * @description: 为目标类挑选合适的Advisor
   * @description: Pick the appropriate Advisor for the target class
   * @time: 2023-05-08 17:01:52
   * @params: [candidateAdvisorList, clazz] 切面列表，类
   * @return: java.util.List<org.springframework.aop.Advisor> 合适的切面
   */
  public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisorList,
      Class<?> clazz) {
    if (candidateAdvisorList.isEmpty()) {
      return candidateAdvisorList;
    } else {
      List<Advisor> eligibleAdvisorList = new ArrayList<>();

      for (Advisor candidate : candidateAdvisorList) {
        if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
          eligibleAdvisorList.add(candidate);
        }
      }

      boolean hasIntroductions = !eligibleAdvisorList.isEmpty();

      for (Advisor candidate : candidateAdvisorList) {
        if (!(candidate instanceof IntroductionAdvisor) && canApply(candidate, clazz,
            hasIntroductions)) {
          eligibleAdvisorList.add(candidate);
        }
      }

      return eligibleAdvisorList;
    }
  }

  /**
   * @author: Ares
   * @description: 使用参数反射调用指定对象的方法
   * @description: Invoke a method of the specified object using parameter reflection
   * @time: 2022-06-08 17:05:54
   * @params: [target, method, args] 对象，方法，参数数组
   * @return: java.lang.Object 调用结果
   */
  @Nullable
  public static Object invokeJoinPointUsingReflection(@Nullable Object target, Method method,
      Object[] args) throws Throwable {
    try {
      method.setAccessible(true);
      return method.invoke(target, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new AopInvocationException(
          "AOP configuration seems to be invalid: tried calling method [" + method + "] on target ["
              + target + "]", illegalArgumentException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new AopInvocationException("Could not access method [" + method + "]",
          illegalAccessException);
    } finally {
      method.setAccessible(false);
    }
  }


  /**
   * @author: Ares
   * @description: 获取代理对象
   * @description: Get proxy object
   * @time: 2021-06-29 10:36:00
   * @params: [proxy] 代理对象
   * @return: java.lang.Object 真实对象
   */
  public static Object getTarget(Object proxy) throws Exception {
    if (!isAopProxy(proxy)) {
      return proxy;
    } else {
      return isJdkDynamicProxy(proxy) ? getJdkDynamicProxyTargetObject(proxy)
          : getCglibProxyTargetObject(proxy);
    }
  }

  /**
   * @author: Ares
   * @description: 获取代理对象的类名
   * @description: Get the class name of the proxy object
   * @time: 2022-06-08 17:07:31
   * @params: [proxy] 代理对象
   * @return: java.lang.String 类名
   */
  public static String getTargetName(Object proxy) {
    Object bean = proxy;
    try {
      bean = getTarget(proxy);
    } catch (Exception ignored) {
    }
    return bean.getClass().getCanonicalName();
  }


  /**
   * @author: Ares
   * @description: 获取cglib代理的对象
   * @description: Get cglib proxy object
   * @time: 2021-06-29 10:36:00
   * @params: [proxy] 代理对象
   * @return: java.lang.Object 真实对象
   */
  public static Object getCglibProxyTargetObject(Object proxy) throws Exception {
    Field field = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
    field.setAccessible(true);
    Object dynamicAdvisedInterceptor = field.get(proxy);
    field.setAccessible(false);
    Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
    field.setAccessible(false);
    advised.setAccessible(true);
    return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource()
        .getTarget();
  }

  /**
   * @author: Ares
   * @description: 获取jdk代理的对象
   * @description: Get jdk proxy object
   * @time: 2021-06-29 10:37:00
   * @params: [proxy] 代理对象
   * @return: java.lang.Object 真实对象
   */
  public static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
    Field field = proxy.getClass().getSuperclass().getDeclaredField("h");
    field.setAccessible(true);
    AopProxy aopProxy = (AopProxy) field.get(proxy);
    field.setAccessible(false);
    Field advised = aopProxy.getClass().getDeclaredField("advised");
    field.setAccessible(false);
    advised.setAccessible(true);
    return ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
  }

}
