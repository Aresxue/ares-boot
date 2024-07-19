package cn.ares.boot.util.json.serializer;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;

/**
 * @author: Ares
 * @time: 2022-06-10 15:18:18
 * @description: 指定双精度浮点型字段序列化为双精度浮点型
 * @description: Specifies that double-precision floating-point fields are serialized as double-precision floating-point types
 * @version: JDK 1.8
 */
public class DoubleSerializer extends NumberSerializers.DoubleSerializer {

  private static final long serialVersionUID = -3641974694635872985L;

  public DoubleSerializer(Class<?> cls) {
    super(Double.class);
  }

}
