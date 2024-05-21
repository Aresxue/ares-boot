package cn.ares.boot.util.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import java.io.IOException;

/**
 * @author: Ares
 * @time: 2024-05-21 11:57:33
 * @description: 将长整形转为字符串类型
 * @description: Convert Long o String
 * @version: JDK 1.8
 */
@JacksonStdImpl
public class LongToStringSerializer extends NumberSerializers.Base<Object> {

  private static final long serialVersionUID = -7912222815768080738L;

  public LongToStringSerializer() {
    super(Long.class, NumberType.LONG, "number");
  }

  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeString(String.valueOf(value));
  }

}
