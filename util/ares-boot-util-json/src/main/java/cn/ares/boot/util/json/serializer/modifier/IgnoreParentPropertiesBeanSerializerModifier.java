package cn.ares.boot.util.json.serializer.modifier;

import cn.ares.boot.util.common.ArrayUtil;
import cn.ares.boot.util.common.ClassUtil;
import cn.ares.boot.util.json.annotation.JsonIgnoreParentProperties;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.util.Iterator;
import java.util.List;

/**
 * @author: Ares
 * @time: 2022-06-16 10:58:32
 * @description: 忽略父类字段的序列化
 * @description: Ignore serialization of superclass fields
 * @version: JDK 1.8
 */
public class IgnoreParentPropertiesBeanSerializerModifier extends BeanSerializerModifier {

  @Override
  public List<BeanPropertyWriter> changeProperties(
      SerializationConfig config, BeanDescription beanDesc,
      List<BeanPropertyWriter> beanProperties) {
    JsonIgnoreParentProperties ignoreParentProperties = beanDesc.getClassAnnotations()
        .get(JsonIgnoreParentProperties.class);
    if (null != ignoreParentProperties) {
      String[] excludeProperties = ignoreParentProperties.exclude();
      Class<?> beanClass = beanDesc.getBeanClass();
      Iterator<BeanPropertyWriter> iterator = beanProperties.iterator();
      while (iterator.hasNext()) {
        BeanPropertyWriter beanPropertyWriter = iterator.next();
        if (ArrayUtil.contains(excludeProperties, beanPropertyWriter.getName())) {
          continue;
        }
        Class<?> clazz = beanPropertyWriter.getMember().getDeclaringClass();
        // 父类字段都移除
        // The parent class fields are removed
        if (!ClassUtil.isSameClass(beanClass, clazz)) {
          iterator.remove();
        }
      }
    }
    return beanProperties;
  }

}
