package cn.ares.boot.starter.spring.validation.util;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_SUPPORT;

import cn.ares.boot.util.common.CollectionUtil;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;

/**
 * @author: Ares
 * @time: 2024-12-02 11:46:50
 * @description: 对象校验工具类
 * @description: Object validator util
 * @version: JDK 1.8
 */
@Component
@Role(value = ROLE_SUPPORT)
public class ValidatorUtil {

  /**
   * 校验器
   * validator
   */
  private static Validator validator;

  /**
   * @author: Ares
   * @description: 对对象执行默认分组的校验
   * @description: Validate object with default group
   * @time: 2024-12-02 11:48:52
   * @params: [object] 对象
   */
  public static <T> void validate(T object) {
    validate(object, Default.class);
  }

  /**
   * @author: Ares
   * @description: 对对象执行指定分组的校验
   * @description: Validate object with specified group
   * @time: 2024-12-02 11:49:18
   * @params: [object, groups] 对象, 校验分组数组
   */
  public static <T> void validate(T object, Class<?>... groups) {
    Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(object, groups);
    if (CollectionUtil.isNotEmpty(constraintViolationSet)) {
      throw new ConstraintViolationException(constraintViolationSet);
    }
  }

  /**
   * @author: Ares
   * @description: 对对象的指定属性执行指定分组的校验
   * @description: Validate the specified property of the object with the specified group
   * @time: 2024-12-02 11:50:00
   * @params: [object, propertyName, groups] 对象, 属性名, 校验分组数组
   */
  public static <T> void validateProperty(T object, String propertyName, Class<?>... groups) {
    Set<ConstraintViolation<T>> constraintViolationSet = validator.validateProperty(object,
        propertyName, groups);
    if (CollectionUtil.isNotEmpty(constraintViolationSet)) {
      throw new ConstraintViolationException(constraintViolationSet);
    }
  }


  /**
   * @author: Ares
   * @description: 对指定类型的对象的指定属性值执行指定分组的校验
   * @description: Validate the specified property value of the specified type of object with the specified group
   * @time: 2024-12-02 11:50:00
   * @params: [beanType, propertyName, value, groups] 对象类型, 属性名, 属性值, 校验分组数组
   */
  public static <T> void validateValue(Class<T> beanType, String propertyName, Object value,
      Class<?>... groups) {
    Set<ConstraintViolation<T>> constraintViolationSet = validator.validateValue(beanType,
        propertyName, value, groups);
    if (CollectionUtil.isNotEmpty(constraintViolationSet)) {
      throw new ConstraintViolationException(constraintViolationSet);
    }
  }

  @Autowired
  public void setValidator(Validator validator) {
    ValidatorUtil.validator = validator;
  }

}
