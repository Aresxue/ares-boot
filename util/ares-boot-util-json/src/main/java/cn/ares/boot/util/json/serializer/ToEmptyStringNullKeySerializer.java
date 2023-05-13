package cn.ares.boot.util.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * @author: Ares
 * @time: 2023-02-15 16:07:46
 * @description: 将key为null的数组转为key为空字符串
 * @description: Converts an array with key null to an empty string with key
 * @version: JDK 1.8
 */
public class ToEmptyStringNullKeySerializer extends StdSerializer<Object> {

  private static final long serialVersionUID = 4459839812026058301L;

  public ToEmptyStringNullKeySerializer() {
    this(null);
  }

  public ToEmptyStringNullKeySerializer(Class<Object> t) {
    super(t);
  }

  @Override
  public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused)
      throws IOException, JsonProcessingException {
    jsonGenerator.writeFieldName("");
  }

}


