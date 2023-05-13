package cn.ares.boot.util.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * @author: Ares
 * @time: 2022-06-16 12:55:31
 * @description: null值序列化
 * @description: null json serializer
 * @version: JDK 1.8
 */
public class NullJsonSerializer {

  /**
   * Array(write empty array)
   * 数组集合类（写空数组）
   */
  public static class NullArrayJsonSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeStartArray();
      gen.writeEndArray();
    }
  }

  /**
   * String(write empty string)
   * 字符串（写空串）
   */
  public static class NullStringJsonSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeString("");
    }
  }

  /**
   * Number(write zero)
   * 数字（写0）
   */
  public static class NullNumberJsonSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeNumber(0);
    }
  }


  /**
   * Boolean(write false)
   * 写否
   */
  public static class NullBooleanJsonSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeBoolean(false);
    }
  }

}
