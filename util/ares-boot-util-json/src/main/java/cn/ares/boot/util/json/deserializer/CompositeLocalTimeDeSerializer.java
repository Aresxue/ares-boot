package cn.ares.boot.util.json.deserializer;

import cn.ares.boot.util.common.StringUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author: Ares
 * @time: 2024-07-19 14:09:46
 * @description: 组合LocalTime反序列化器
 * @description: Composite LocalTime DeSerializer
 * @version: JDK 1.8
 */
public class CompositeLocalTimeDeSerializer extends JsonDeserializer<LocalTime> {

  private final LocalTimeDeserializer localDateTimeDeserializer;
  private LocalTimeDeserializer removeZeroLocalTimeDeserializer;

  public CompositeLocalTimeDeSerializer(String localTimeFormat) {
    this.localDateTimeDeserializer = new LocalTimeDeserializer(
        DateTimeFormatter.ofPattern(localTimeFormat));
    // 如果包含.例如yyyy-MM-dd HH:mm:ss.SSS则额外构造一个不包含毫秒的序列化器
    List<String> formatList = StringUtil.listSplit(localTimeFormat, ".S");
    if (formatList.size() > 1) {
      removeZeroLocalTimeDeserializer = new LocalTimeDeserializer(
          DateTimeFormatter.ofPattern(formatList.get(0)));
    }
  }

  @Override
  public LocalTime deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    if (null == removeZeroLocalTimeDeserializer || parser.getText().contains(".")) {
      return localDateTimeDeserializer.deserialize(parser, context);
    } else {
      return removeZeroLocalTimeDeserializer.deserialize(parser, context);
    }
  }

}
