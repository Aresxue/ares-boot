package cn.ares.boot.util.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: Ares
 * @time: 2024-07-19 13:11:04
 * @description: 框架自定义的LocalDateTime序列化
 * @description: Custom LocalDateTime serialization of the framework
 * @version: JDK 1.8
 */
public class BootLocalDateTimeSerializer extends LocalDateTimeSerializer {

  private static final long serialVersionUID = -4746328519998980203L;

  public BootLocalDateTimeSerializer() {
  }

  public BootLocalDateTimeSerializer(DateTimeFormatter dateTimeFormatter) {
    super(dateTimeFormatter);
  }

  @Override
  public void serialize(LocalDateTime localDateTime, JsonGenerator generator,
      SerializerProvider provider) throws IOException {
    if (useTimestamp(provider)) {
      super.serialize(localDateTime, generator, provider);
    } else {
      DateTimeFormatter dateTimeFormatter = _formatter;
      if (dateTimeFormatter == null) {
        dateTimeFormatter = _defaultFormatter();
      }
      String result = localDateTime.format(dateTimeFormatter);
      generator.writeString(result);
    }
  }

}
