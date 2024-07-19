package cn.ares.boot.util.json.serializer;

import cn.ares.boot.util.common.StringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author: Ares
 * @time: 2024-07-19 13:39:36
 * @description: 组合LocalDateTime序列化器
 * @description: Composite LocalDateTime Serializer
 * @version: JDK 1.8
 */
public class CompositeLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

  private final LocalDateTimeSerializer localDateTimeSerializer;
  private LocalDateTimeSerializer removeZeroLocalDateTimeSerializer;

  public CompositeLocalDateTimeSerializer(String localDateTimeFormat) {
    this.localDateTimeSerializer = new LocalDateTimeSerializer(
        DateTimeFormatter.ofPattern(localDateTimeFormat));
    // 如果包含.例如yyyy-MM-dd HH:mm:ss.SSS则额外构造一个不包含毫秒的序列化器
    List<String> formatList = StringUtil.listSplit(localDateTimeFormat, ".S");
    if (formatList.size() > 1) {
      removeZeroLocalDateTimeSerializer = new LocalDateTimeSerializer(
          DateTimeFormatter.ofPattern(formatList.get(0)));
    }
  }

  @Override
  public void serialize(LocalDateTime value, JsonGenerator generator,
      SerializerProvider serializers) throws IOException {
    // 发现是0毫秒的时间则使用不包含毫秒的序列化器
    if (null != removeZeroLocalDateTimeSerializer && 0 == value.getNano()) {
      removeZeroLocalDateTimeSerializer.serialize(value, generator, serializers);
    } else {
      localDateTimeSerializer.serialize(value, generator, serializers);
    }
  }

}
