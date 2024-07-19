package cn.ares.boot.util.json.serializer;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;

/**
 * @author: Ares
 * @time: 2022-06-10 15:18:18
 * @description: 指定长整形字段序列化为长整形
 * @description: Specifies that the long integer field is serialized as a long integer
 * @version: JDK 1.8
 */
public class LongSerializer extends NumberSerializers.LongSerializer {

  private static final long serialVersionUID = -752662085827129328L;

  public LongSerializer(Class<?> cls) {
    super(Long.class);
  }

}
