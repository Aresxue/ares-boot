package cn.ares.boot.util.json.serializer;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;

/**
 * @author: Ares
 * @time: 2022-06-10 15:18:18
 * @description: 指定整形字段序列化为整形
 * @description: Specifies that the integer field is serialized as an integer
 * @version: JDK 1.8
 */
@JacksonStdImpl
public class IntegerSerializer extends NumberSerializers.IntegerSerializer {

  private static final long serialVersionUID = -4607397096311604751L;

  public IntegerSerializer(Class<?> cls) {
    super(Integer.class);
  }

}
