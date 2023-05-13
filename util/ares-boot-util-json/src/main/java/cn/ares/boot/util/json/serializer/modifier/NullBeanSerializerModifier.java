package cn.ares.boot.util.json.serializer.modifier;

import cn.ares.boot.util.json.serializer.NullJsonSerializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Ares
 * @time: 2022-06-16 12:52:28
 * @description: 重写Null的序列化，在异构系统中可能不能把null转为相应的布尔值和数字
 * @description: Override the serialization of Null, which may not convert null to correct boolean
 * values and numbers in heterogeneous systems (especially for primitive types)
 * @version: JDK 1.8
 */
public class NullBeanSerializerModifier extends BeanSerializerModifier {

  private static final String ARRAY_TYPE = "array";
  private static final String STRING_TYPE = "string";
  private static final String BOOLEAN_TYPE = "boolean";
  private static final String NUMBER_TYPE = "number";

  private static final Map<String, JsonSerializer<Object>> JSON_SERIALIZER_MAP = new HashMap<String, JsonSerializer<Object>>(8) {
    private static final long serialVersionUID = 2979664114420208314L;

    {
    put(ARRAY_TYPE, new NullJsonSerializer.NullArrayJsonSerializer());
    put(STRING_TYPE, new NullJsonSerializer.NullStringJsonSerializer());
    put(BOOLEAN_TYPE, new NullJsonSerializer.NullBooleanJsonSerializer());
    put(NUMBER_TYPE, new NullJsonSerializer.NullNumberJsonSerializer());
  }};


  @Override
  public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
      BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
    for (BeanPropertyWriter beanPropertyWriter : beanProperties) {
      String type = getType(beanPropertyWriter);
      JsonSerializer<Object> objectJsonSerializer = JSON_SERIALIZER_MAP.get(type);
      if (null != objectJsonSerializer) {
        beanPropertyWriter.assignNullSerializer(objectJsonSerializer);
      }
    }
    return beanProperties;
  }

  private String getType(BeanPropertyWriter writer) {
    Class<?> clazz = writer.getType().getRawClass();

    if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
      return ARRAY_TYPE;
    }

    if (Character.class.isAssignableFrom(clazz) || CharSequence.class.isAssignableFrom(clazz)) {
      return STRING_TYPE;
    }

    if (Boolean.class.equals(clazz)) {
      return BOOLEAN_TYPE;
    }

    if (Number.class.isAssignableFrom(clazz)) {
      return NUMBER_TYPE;
    }

    return null;
  }


}
