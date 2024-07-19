package cn.ares.boot.util.json.deserializer;

import cn.ares.boot.util.common.StringUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author: Ares
 * @time: 2024-07-19 14:09:46
 * @description: 组合LocalDateTime反序列化器
 * @description: Composite LocalDateTime DeSerializer
 * @version: JDK 1.8
 */
public class CompositeLocalDateTimeDeSerializer extends JsonDeserializer<LocalDateTime> {

  private final LocalDateTimeDeserializer localDateTimeDeserializer;
  private LocalDateTimeDeserializer removeZeroLocalDateTimeDeserializer;

  public CompositeLocalDateTimeDeSerializer(String localDateTimeFormat) {
    this.localDateTimeDeserializer = new LocalDateTimeDeserializer(
        DateTimeFormatter.ofPattern(localDateTimeFormat));
    // 如果包含.例如yyyy-MM-dd HH:mm:ss.SSS则额外构造一个不包含毫秒的序列化器
    List<String> formatList = StringUtil.listSplit(localDateTimeFormat, ".S");
    if (formatList.size() > 1) {
      removeZeroLocalDateTimeDeserializer = new LocalDateTimeDeserializer(
          DateTimeFormatter.ofPattern(formatList.get(0)));
    }
  }

  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    if (null == removeZeroLocalDateTimeDeserializer || parser.getText().contains(".")) {
      return localDateTimeDeserializer.deserialize(parser, context);
    } else {
      return removeZeroLocalDateTimeDeserializer.deserialize(parser, context);
    }
  }

}
