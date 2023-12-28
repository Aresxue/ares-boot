package cn.ares.boot.util.spring;

import cn.ares.boot.util.common.entity.InvokeMethod;
import cn.ares.boot.util.spring.entity.ReflectionEntity;
import cn.ares.boot.util.spring.entity.User;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author: Ares
 * @time: 2022-11-20 00:16:31
 * @description: ReflectionUtil test
 * @version: JDK 1.8
 */
public class ReflectionUtilTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtilTest.class);

  @Test
  public void testField() {
    User user = new User();
    user.setId(1L);

    // test when field is not exist
    String noExist = ReflectionUtil.getFieldValue(user, "noExist");

    // findField test
    Field fieldFromClass = ReflectionUtil.findField(User.class, "id");
    Field fieldFromObject = ReflectionUtil.findField(user, "id");
    LOGGER.info("{}, {}", fieldFromClass, fieldFromObject);
    Assert.isTrue(fieldFromClass.equals(fieldFromObject), "字段不一致");

    // getFieldValue test
    Long id = ReflectionUtil.getFieldValue(user, "id");
    Object idObject = ReflectionUtil.getFieldValue(user, "id");
    Assert.isTrue(id.equals(idObject), "字段值不一致");
    // java.lang.ClassCastException
//    User tempUser = ReflectionUtil.getFieldValue(user, "id");
    Integer level = ReflectionUtil.getFieldValue(User.class, "level");
    Object levelObject = ReflectionUtil.getFieldValue(user, "level");
    Assert.isTrue(level.equals(levelObject), "静态字段值不一致");
  }


  @Test
  public void testMethod() {
    // test when method is not exist
    Method noExistMethod = ReflectionUtil.findMethod(ReflectionEntity.class, "noExist");

    // findMethod test
    Method method = ReflectionUtil.findMethod(ReflectionEntity.class, "sayHello");
    Method accessibleMethod = ReflectionUtil.findMethod(ReflectionEntity.class, "sayHello", true);
    Assert.isTrue(method.equals(accessibleMethod), "方法不一致");
    Method sayMethod = ReflectionUtil.findMethod(ReflectionEntity.class, "say", String.class);
    Method sayAccessibleMethod = ReflectionUtil.findMethod(ReflectionEntity.class, "say", true,
        String.class);
    Assert.isTrue(sayMethod.equals(sayAccessibleMethod), "方法不一致");

    // invokeMethod test
    ReflectionEntity reflectionEntity = new ReflectionEntity();
    Object result = ReflectionUtil.invokeMethod(reflectionEntity, "sayHello");
    LOGGER.info("sayHello: {}", result);
    Assert.isTrue("hello".equals(result), "反射结果错误");
    result = ReflectionUtil.invokeMethod(reflectionEntity, "say", "ares");
    LOGGER.info("say: {}", result);
    Assert.isTrue("ares".equals(result), "反射结果错误");
    String str = ReflectionUtil.invokeMethod(reflectionEntity, "sayHello");
    LOGGER.info("sayHello: {}", str);
    Assert.isTrue("hello".equals(str), "反射结果错误");
    str = ReflectionUtil.invokeMethod(reflectionEntity, "say", "ares");
    LOGGER.info("say: {}", str);
    Assert.isTrue("ares".equals(str), "反射结果错误");
    String word = null;
    str = ReflectionUtil.invokeMethod(reflectionEntity, "say", word);
    LOGGER.info("say: {}", str);
    Assert.isTrue(null == str, "反射结果错误");
    str = ReflectionUtil.invokeMethod(reflectionEntity, "notExist", "ares");
    LOGGER.info("say: {}", str);
    Assert.isTrue(null == str, "反射结果错误");
    str = ReflectionUtil.invokeMethod(ReflectionEntity.class, "sayStaticHello");
    LOGGER.info("sayStaticHello: {}", str);
    Assert.isTrue("static hello".equals(str), "反射结果错误");
    str = ReflectionUtil.invokeMethod(ReflectionEntity.class, "sayStatic", "static ares");
    LOGGER.info("sayStatic: {}", str);
    Assert.isTrue("static ares".equals(str), "反射结果错误");

    InvokeMethod invokeMethod = ReflectionUtil.buildInvokeMethod(method, reflectionEntity);
    str = ReflectionUtil.invoke(invokeMethod);
    Assert.isTrue("hello".equals(str), "反射结果错误");
    invokeMethod = ReflectionUtil.buildInvokeMethod(reflectionEntity, "sayHello");
    str = ReflectionUtil.invoke(invokeMethod);
    Assert.isTrue("hello".equals(str), "反射结果错误");
    invokeMethod = ReflectionUtil.buildInvokeMethod(reflectionEntity, "say", String.class);
    str = ReflectionUtil.invoke(invokeMethod, "ares");
    Assert.isTrue("ares".equals(str), "反射结果错误");
    invokeMethod = ReflectionUtil.buildInvokeMethod(ReflectionEntity.class, "sayStatic",
        String.class);
    str = ReflectionUtil.invoke(invokeMethod, "ares");
    Assert.isTrue("ares".equals(str), "反射结果错误");
    invokeMethod = ReflectionUtil.buildInvokeMethod(ReflectionEntity.class, "sayStaticHello");
    str = ReflectionUtil.invoke(invokeMethod);
    Assert.isTrue("static hello".equals(str), "反射结果错误");

    LOGGER.info("{}", ReflectionUtil.invokeMethodReturnOption(reflectionEntity, "sayHello"));
    LOGGER.info("{}", ReflectionUtil.invokeMethodReturnOption(reflectionEntity, "say", "ares"));
  }

  @Test
  public void testConstructor() throws ClassNotFoundException {
    Constructor<User> constructor = ReflectionUtil.findConstructor(User.class, true);
    Constructor<User> constructorFromObject = ReflectionUtil.findConstructor(new User(), true);
    LOGGER.info("{}, {}", constructor, constructorFromObject);
    Assert.isTrue(constructor.equals(constructorFromObject), "构造器不一致");

    constructorFromObject = ReflectionUtil.findConstructor(new User(), true, String.class,
        Long.class, String.class);
    LOGGER.info("{}", constructorFromObject);

    User user = ReflectionUtil.invokeConstructor(User.class, true);
    LOGGER.info("{}", user);
    User userFromName = ReflectionUtil.invokeConstructor("cn.ares.boot.util.spring.entity.User");
    LOGGER.info("{}", userFromName);
    user = ReflectionUtil.invokeConstructor(User.class, "ares", 1L, "");
    LOGGER.info("{}", user);
  }

}
