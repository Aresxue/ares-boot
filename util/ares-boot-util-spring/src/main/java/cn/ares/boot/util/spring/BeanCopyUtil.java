package cn.ares.boot.util.spring;

import cn.ares.boot.util.common.CollectionUtil;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

/**
 * @author: Ares
 * @time: 2021-03-03 15:02:00
 * @description: 对象属性拷贝工具
 * @description: Bean Attribute copy util
 * @version: JDK 1.8
 */
public class BeanCopyUtil {

  /**
   * BeanCopier cache
   */
  private static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();

  /**
   * @author: Ares
   * @description: 生成目标类对象并拷贝源对象属性给它（属性不一致拷贝失败）
   * @description: Generate the target class object and copy the source object properties to
   * it(Attribute inconsistent copy failed)
   * @time: 2021-03-03 15:43:00
   * @params: [sourceObject, targetClass] 源对象，目标类
   * @return: TARGET java object
   */
  public static <SOURCE, TARGET> TARGET copy(SOURCE sourceObject, Class<TARGET> targetClass) {
    TARGET targetObject = BeanUtils.instantiateClass(targetClass);
    return copy(sourceObject, targetObject);
  }

  /**
   * @author: Ares
   * @description: 拷贝源对象属性给目标类对象（属性不一致拷贝失败）
   * @description: Copy the source object properties to the target class object(Attribute
   * inconsistent copy failed)
   * @time: 2021-03-03 15:43:00
   * @params: [sourceObject, targetClass] 源对象，目标对象
   * @return: TARGET 拷贝后目标对象
   */
  public static <SOURCE, TARGET> TARGET copy(SOURCE sourceObject, TARGET targetObject) {
    return copy(sourceObject, targetObject, false, null);
  }

  /**
   * @author: Ares
   * @description: 生成目标类对象并拷贝源对象属性给它（属性不一致拷贝失败）之后执行其他操作
   * @description: Perform other operations after generating the target class object and copying the
   * source object attributes to it (Attribute inconsistent copy failed)
   * @time: 2021-03-03 15:43:00
   * @params: [sourceObject, targetClass, biConsumer] 源对象，目标类，拷贝后操作
   * @return: TARGET java object
   */
  public static <SOURCE, TARGET> TARGET copy(SOURCE sourceObject, Class<TARGET> targetClass,
      BiConsumer<SOURCE, TARGET> biConsumer) {
    TARGET targetObject = copy(sourceObject, targetClass);
    biConsumer.accept(sourceObject, targetObject);
    return targetObject;
  }

  /**
   * @author: Ares
   * @description: 使用转换器将源对象的属性拷贝给目标对象
   * @description: Copy the properties of the source object to the target object using the
   * converter
   * @time: 2023-05-08 17:06:57
   * @params: [sourceObject, targetObject, converter] 源对象，目标对象，转换器
   * @return: TARGET out 出参
   */
  public static <SOURCE, TARGET> TARGET copy(SOURCE sourceObject, TARGET targetObject,
      Converter converter) {
    return copy(sourceObject, targetObject, true, converter);
  }

  /**
   * @author: Ares
   * @description: 是否使用转换器从源对象拷贝属性到目标对象
   * @description: Whether to use a converter to copy properties from the source object to the
   * target object
   * @time: 2022-06-08 17:15:01
   * @params: [sourceObject, targetObject, useConverter, converter] 源对象，目标对象，是否使用转换器，转换器
   * @return: TARGET 拷贝后目标对象
   */
  public static <SOURCE, TARGET> TARGET copy(SOURCE sourceObject, TARGET targetObject,
      boolean useConverter, Converter converter) {
    if (null == sourceObject || null == targetObject) {
      return null;
    }
    String key = sourceObject.getClass().getCanonicalName() + ":" + targetObject.getClass()
        .getCanonicalName();
    BeanCopier copier = BEAN_COPIER_CACHE.get(key);
    if (null == copier) {
      copier = createBeanCopier(sourceObject.getClass(), targetObject.getClass(), useConverter,
          key);
    }
    copier.copy(sourceObject, targetObject, converter);

    return targetObject;
  }

  /**
   * @author: Ares
   * @description: 拷贝源对象属性给目标类对象（仅拷贝一致的属性且忽略null值）
   * @description: Copy source object properties to target class objects (copy only consistent
   * properties and ignore null values)
   * @time: 2021-03-05 13:34:00
   * @params: [sourceObject, targetObject] 源对象，目标对象
   * @return: void
   */
  public static <SOURCE, T> void copyPropertiesIgnoreNull(SOURCE sourceObject, T targetObject) {
    BeanUtils.copyProperties(sourceObject, targetObject, getNullPropertyNames(sourceObject));
  }


  /**
   * @author: Ares
   * @description: 拷贝源对象属性给目标类对象（仅拷贝一致的属性）
   * @description: Copy source object properties to target class objects (copy only consistent
   * properties)
   * @time: 2021-03-05 13:34:00
   * @params: [sourceObject, targetObject] 源对象，目标对象
   * @return: void
   */
  public static <SOURCE, TARGET> void copyProperties(SOURCE sourceObject, TARGET targetObject,
      String... ignoreProperties) {
    BeanUtils.copyProperties(sourceObject, targetObject, ignoreProperties);
  }

  /**
   * @author: Ares
   * @description: 生成目标类对象并拷贝源对象属性（仅拷贝一致的属性）给它
   * @description: Generate the target class object and copy the source object properties (copy only
   * consistent properties) to it
   * @time: 2022-06-08 17:21:10
   * @params: [sourceObject, targetClass] 源对象，目标类
   * @return: TARGET
   */
  public static <SOURCE, TARGET> TARGET copyProperties(SOURCE sourceObject,
      Class<TARGET> targetClass) {
    TARGET targetObject = BeanUtils.instantiateClass(targetClass);
    copyProperties(sourceObject, targetObject);
    return targetObject;
  }

  /**
   * @author: Ares
   * @description: 生成目标类对象并拷贝源对象属性（仅拷贝一致的属性）给它之后执行其他操作
   * @description: Generate the target class object and copy the source object attributes (only copy
   * consistent attributes) to it and then perform other operations
   * @time: 2022-06-08 17:21:10
   * @params: [sourceObject, targetClass, biConsumer] 源对象，目标类，拷贝后操作
   * @return: TARGET
   */
  public static <SOURCE, TARGET> TARGET copyProperties(SOURCE source, Class<TARGET> targetClass,
      BiConsumer<SOURCE, TARGET> biConsumer) {
    try {
      TARGET target = copyProperties(source, targetClass);
      biConsumer.accept(source, target);
      return target;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * @author: Ares
   * @description: 从一个List<SOURCE>到另一个List<TARGET> 因为泛型擦除所以target class作为单独一个参数
   * @description: From one List<SOURCE> to another List<TARGET> target class as a single parameter
   * due to generic erasure
   * @time: 2021-03-03 15:21:00
   * @params: [sourceObjectList, targetObjectList, targetClass] 源对象列表，目标对象列表，目标类
   * @return: java.util.List<T> 拷贝后目标对象列表
   */
  public static <SOURCE, TARGET> List<TARGET> copyList(List<SOURCE> sourceObjectList,
      List<TARGET> targetObjectList, Class<TARGET> targetClass) {
    if (null == sourceObjectList || null == targetClass) {
      return Collections.emptyList();
    }

    if (null == targetObjectList) {
      targetObjectList = new ArrayList<>();
    }

    for (SOURCE sourceObject : sourceObjectList) {
      TARGET targetObject = BeanUtils.instantiateClass(targetClass);
      copy(sourceObject, targetObject);
      targetObjectList.add(targetObject);
    }
    return targetObjectList;
  }

  /**
   * @author: Ares
   * @description: 为源对象列表中的对象生成目标类对象并拷贝属性给它
   * @description: Generate a target class object for the objects in the source object list and copy
   * properties to it
   * @time: 2022-06-08 17:25:55
   * @params: [sourceObjectList, targetClass] 源对象列表，目标类
   * @return: java.util.List<T>
   */
  public static <SOURCE, TARGET> List<TARGET> copyList(List<SOURCE> sourceObjectList,
      Class<TARGET> targetClass) {
    return copyList(sourceObjectList, new ArrayList<>(), targetClass);
  }

  /**
   * @author: Ares
   * @description: 为源对象列表中的对象生成目标类对象并拷贝属性给它之后执行其他操作
   * @description: Generate the target class object for the object in the source object list and
   * copy the properties to it (only copy the consistent properties) and then perform other
   * operations
   * @time: 2022-06-08 17:25:55
   * @params: [sourceObjectList, targetClass, biConsumer] 源对象列表，目标类，拷贝后操作
   * @return: java.util.List<T>
   */
  public static <SOURCE, TARGET> List<TARGET> copyList(List<SOURCE> sourceObjectList,
      Class<TARGET> targetClass, BiConsumer<SOURCE, TARGET> biConsumer) {
    return copyList(sourceObjectList, new ArrayList<>(), targetClass, biConsumer);
  }

  private static <SOURCE, TARGET> List<TARGET> copyList(List<SOURCE> sourceObjectList,
      ArrayList<TARGET> targetObjectList, Class<TARGET> targetClass,
      BiConsumer<SOURCE, TARGET> biConsumer) {
    if (null == sourceObjectList || null == targetClass) {
      return Collections.emptyList();
    }

    if (null == targetObjectList) {
      targetObjectList = new ArrayList<>();
    }

    for (SOURCE sourceObject : sourceObjectList) {
      TARGET targetObject = BeanUtils.instantiateClass(targetClass);
      copy(sourceObject, targetObject);
      biConsumer.accept(sourceObject, targetObject);
      targetObjectList.add(targetObject);
    }
    return targetObjectList;
  }

  private static <SOURCE, T> BeanCopier createBeanCopier(Class<SOURCE> sourceClass,
      Class<T> targetClass,
      boolean useConverter, String cacheKey) {
    BeanCopier copier = BeanCopier.create(sourceClass, targetClass, useConverter);
    BEAN_COPIER_CACHE.putIfAbsent(cacheKey, copier);
    return copier;
  }

  /**
   * @author: Ares
   * @description: 为源对象列表中的对象生成目标类对象并拷贝属性给它（仅拷贝一致的属性）
   * @description: Generate a target class object for the objects in the source object list and copy
   * properties to it(Copy only consistent properties)
   * @time: 2022-06-08 17:25:55
   * @params: [sourceObjectList, targetClass] 源对象列表，目标类
   * @return: java.util.List<T>
   */
  public static <SOURCE, T> List<T> copyListBeanPropertiesToList(List<SOURCE> sourceList,
      Class<T> targetClass) {
    List<T> result = new ArrayList<>();
    if (CollectionUtil.isNotEmpty(sourceList)) {
      for (SOURCE source : sourceList) {
        T targetObject = BeanUtils.instantiateClass(targetClass);
        BeanUtils.copyProperties(source, targetObject);
        result.add(targetObject);
      }
    }
    return result;
  }

  /**
   * @author: Ares
   * @description: 为源对象列表中的对象生成目标类对象并拷贝属性给它（仅拷贝一致的属性）之后执行其他操作
   * @description: Generate a target class object for the objects in the source object list and copy
   * properties to it(Copy only consistent properties)
   * @time: 2022-06-08 17:25:55
   * @params: [sourceObjectList, targetClass, biConsumer] 源对象列表，目标类，拷贝后操作
   * @return: java.util.List<T>
   */
  public static <SOURCE, TARGET> List<TARGET> copyListBeanPropertiesToList(List<SOURCE> sourceList,
      Class<TARGET> targetClass, BiConsumer<SOURCE, TARGET> biConsumer) {
    List<TARGET> result = new ArrayList<>();
    if (CollectionUtil.isNotEmpty(sourceList)) {
      for (SOURCE source : sourceList) {
        TARGET targetObject = BeanUtils.instantiateClass(targetClass);
        BeanUtils.copyProperties(source, targetObject);
        biConsumer.accept(source, targetObject);
        result.add(targetObject);
      }
    }
    return result;
  }


  /**
   * @author: Ares
   * @description: 获取值为null的属性名数组
   * @description: Gets an array of property names with a value of null
   * @time: 2023-05-08 17:10:25
   * @params: [source] 源对象
   * @return: java.lang.String[] 值为null的属性名数组
   */
  public static String[] getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    PropertyDescriptor[] propertyDescriptors = src.getPropertyDescriptors();
    Set<String> emptyNames = new HashSet<>();
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      try {
        Object srcValue = src.getPropertyValue(descriptor.getName());
        if (null == srcValue) {
          emptyNames.add(descriptor.getName());
        }
      } catch (BeansException ignored) {
        // BeansException不阻断整个执行过程
        // BeansException does not block the entire execution
      }
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }

}
