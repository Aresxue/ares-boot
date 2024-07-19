package cn.ares.boot.util.json.serializer;

import cn.ares.boot.util.common.StringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author: Ares
 * @time: 2024-07-19 13:39:36
 * @description: 组合LocalTime序列化器
 * @description: Composite LocalTime Serializer
 * @version: JDK 1.8
 */
public class CompositeLocalTimeSerializer extends JsonSerializer<LocalTime> {

  private final LocalTimeSerializer localDateTimeSerializer;
  private LocalTimeSerializer removeZeroLocalTimeSerializer;

  public CompositeLocalTimeSerializer(String localTimeFormat) {
    this.localDateTimeSerializer = new LocalTimeSerializer(
        DateTimeFormatter.ofPattern(localTimeFormat));
    // 如果包含.例如HH:mm:ss.SSS则额外构造一个不包含毫秒的序列化器
    List<String> formatList = StringUtil.listSplit(localTimeFormat, ".S");
    if (formatList.size() > 1) {
      removeZeroLocalTimeSerializer = new LocalTimeSerializer(
          DateTimeFormatter.ofPattern(formatList.get(0)));
    }
  }

  @Override
  public void serialize(LocalTime value, JsonGenerator generator,
      SerializerProvider serializers) throws IOException {
    // 发现是0毫秒的时间则使用不包含毫秒的序列化器
    if (null != removeZeroLocalTimeSerializer && 0 == value.getNano()) {
      removeZeroLocalTimeSerializer.serialize(value, generator, serializers);
    } else {
      localDateTimeSerializer.serialize(value, generator, serializers);
    }
  }

}
