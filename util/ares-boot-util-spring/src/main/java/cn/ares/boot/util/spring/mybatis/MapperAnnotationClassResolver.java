package cn.ares.boot.util.spring.mybatis;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;

/**
 * @author: Ares
 * @time: 2023-05-05 21:49:52
 * @description: Mapper上的自定义注解解析
 * @description: Custom annotation parsing on Mapper
 * @version: JDK 1.8
 */
public class MapperAnnotationClassResolver {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(MapperAnnotationClassResolver.class);

  private static boolean mpEnabled = false;

  private static Field mapperInterfaceField;

  static {
    Class<?> proxyClass = null;
    try {
      proxyClass = Class.forName("com.baomidou.mybatisplus.core.override.MybatisMapperProxy");
    } catch (ClassNotFoundException e1) {
      try {
        proxyClass = Class.forName("com.baomidou.mybatisplus.core.override.PageMapperProxy");
      } catch (ClassNotFoundException e2) {
        try {
          proxyClass = Class.forName("org.apache.ibatis.binding.MapperProxy");
        } catch (ClassNotFoundException ignored) {
        }
      }
    }
    if (proxyClass != null) {
      try {
        mapperInterfaceField = proxyClass.getDeclaredField("mapperInterface");
        mapperInterfaceField.setAccessible(true);
        mpEnabled = true;
      } catch (NoSuchFieldException exception) {
        JdkLoggerUtil.warn(LOGGER, "get mapper interface field fail: ", exception);
      }
    }
  }

  /**
   * 缓存对应的属性值
   */
  private final Map<Object, AnnotationAttributes> attributeCache = new ConcurrentHashMap<>();
  private final boolean allowedPublicOnly;
  private final Class<? extends Annotation> annotationType;

  public MapperAnnotationClassResolver(Class<? extends Annotation> annotationType) {
    this(true, annotationType);
  }

  /**
   * 加入扩展, 给外部一个修改aop条件的机会
   *
   * @param allowedPublicOnly 只允许公共的方法, 默认为true
   */
  public MapperAnnotationClassResolver(boolean allowedPublicOnly,
      Class<? extends Annotation> annotationType) {
    this.allowedPublicOnly = allowedPublicOnly;
    this.annotationType = annotationType;
  }

  public String findKey(Method method, Object targetObject) {
    return findKey(method, targetObject, "value");
  }

  public String findKey(Method method, Object targetObject, String attributeName) {
    return Optional.ofNullable(findAttributes(method, targetObject))
        .map(attributes -> attributes.getString(attributeName)).orElse(null);
  }

  /**
   * 从缓存获取数据 Get data from the cache
   *
   * @param method       方法
   * @param targetObject 目标对象
   * @return attributes 属性
   */
  public AnnotationAttributes findAttributes(Method method, Object targetObject) {
    if (method.getDeclaringClass() == Object.class) {
      return null;
    }
    Object cacheKey = new MethodClassKey(method, targetObject.getClass());
    AnnotationAttributes attributes = this.attributeCache.get(cacheKey);
    if (attributes == null) {
      attributes = computeAttributes(method, targetObject);

      this.attributeCache.put(cacheKey, attributes);
    }
    return attributes;
  }

  /**
   * 查找注解的顺序 1. 当前方法 2. 桥接方法 3. 当前类开始一直找到Object 4. 支持mybatis-plus, mybatis-spring Order to find
   * annotations 1. Current method 2. Bridge method 3. Current class starts to find all the way to
   * Object 4. mybatis-plus and mybatis-spring are supported
   *
   * @param method       方法
   * @param targetObject 目标对象
   * @return attributes 属性
   */
  private AnnotationAttributes computeAttributes(Method method, Object targetObject) {
    if (allowedPublicOnly && !Modifier.isPublic(method.getModifiers())) {
      return null;
    }
    // 1. 从当前方法接口中获取
    // 1. Obtained from the current method interface
    AnnotationAttributes attributes = findAttributes(method);
    if (attributes != null) {
      return attributes;
    }
    Class<?> targetClass = targetObject.getClass();
    Class<?> userClass = ClassUtils.getUserClass(targetClass);
    // JDK代理时, 获取实现类的方法声明.  method: 接口的方法, specificMethod: 实现类方法
    // When the JDK proxy is used, the method declaration of the implementation class is obtained. method: The method of the interface, specificMethod: The implementation class method
    Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);

    specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
    // 2. 从桥接方法查找
    // 2. Look up from the bridge method
    attributes = findAttributes(specificMethod);
    if (attributes != null) {
      return attributes;
    }
    // 从当前方法声明的类查找
    // Looks from the class declared by the current method
    attributes = findAttributes(userClass);
    if (attributes != null && ClassUtils.isUserLevelMethod(method)) {
      return attributes;
    }
    // 从接口查找，只取第一个找到的
    // Search from the interface, only the first one found
    for (Class<?> interfaceClazz : ClassUtils.getAllInterfacesForClassAsSet(userClass)) {
      attributes = findAttributes(interfaceClazz);
      if (attributes != null) {
        return attributes;
      }
    }
    // 如果存在桥接方法
    // If there is a bridging method
    if (specificMethod != method) {
      // 从桥接方法查找
      // Look up from the bridge method
      attributes = findAttributes(method);
      if (attributes != null) {
        return attributes;
      }
      // 从桥接方法声明的类查找
      // Lookup from the class declared by the bridge method
      attributes = findAttributes(method.getDeclaringClass());
      if (attributes != null && ClassUtils.isUserLevelMethod(method)) {
        return attributes;
      }
    }
    return getDefaultDataSourceAttr(targetObject);
  }

  /**
   * 默认的获取方式 Default obtaining method
   *
   * @param targetObject 目标对象
   * @return attributes 属性
   */
  private AnnotationAttributes getDefaultDataSourceAttr(Object targetObject) {
    Class<?> targetClass = targetObject.getClass();
    // 如果不是代理类, 从当前类开始, 不断的找父类的声明
    // If it is not a proxy class, start with the current class and continue to find the parent class declaration
    if (!Proxy.isProxyClass(targetClass)) {
      Class<?> currentClass = targetClass;
      while (currentClass != Object.class) {
        AnnotationAttributes attributes = findAttributes(currentClass);
        if (attributes != null) {
          return attributes;
        }
        currentClass = currentClass.getSuperclass();
      }
    }
    // mybatis-plus, mybatis-spring 的获取方式
    if (mpEnabled) {
      final Class<?> clazz = getMapperInterfaceClass(targetObject);
      if (clazz != null) {
        AnnotationAttributes attributes = findAttributes(clazz);
        if (attributes != null) {
          return attributes;
        }
        // 尝试从其父接口获取
        // Attempts to get from its parent interface
        return findAttributes(clazz.getSuperclass());
      }
    }
    return null;
  }

  /**
   * 用于处理嵌套代理 Used to handle nested agents
   *
   * @param target JDK 代理类对象
   * @return InvocationHandler 的 Class
   */
  private Class<?> getMapperInterfaceClass(Object target) {
    Object current = target;
    while (Proxy.isProxyClass(current.getClass())) {
      Object currentRefObject = AopProxyUtils.getSingletonTarget(current);
      if (currentRefObject == null) {
        break;
      }
      current = currentRefObject;
    }
    try {
      if (Proxy.isProxyClass(current.getClass())) {
        return (Class<?>) mapperInterfaceField.get(Proxy.getInvocationHandler(current));
      }
    } catch (IllegalAccessException ignore) {
    }
    return null;
  }

  /**
   * 通过AnnotatedElement 查找标记的注解 Find annotations for markup by AnnotatedElement
   *
   * @param annotatedElement AnnotatedElement
   * @return attributes 属性
   */
  private AnnotationAttributes findAttributes(AnnotatedElement annotatedElement) {
    return AnnotatedElementUtils.getMergedAnnotationAttributes(
        annotatedElement, annotationType);
  }

}
