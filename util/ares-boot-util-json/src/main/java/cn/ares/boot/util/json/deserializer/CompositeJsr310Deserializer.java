package cn.ares.boot.util.json.deserializer;

import static cn.ares.boot.util.common.constant.SymbolConstant.DOUBLE_HYPHEN;

import cn.ares.boot.util.common.CollectionUtil;
import cn.ares.boot.util.common.StringUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Ares
 * @time: 2022-10-13 10:13:19
 * @description: Composite LocalDateTime deserializer
 * @version: JDK 1.8
 */
public class CompositeJsr310Deserializer extends StdDeserializer<LocalDateTime> implements
    ContextualDeserializer {

  private static final long serialVersionUID = 8186443244781045193L;
  /**
   * 反序列化器缓存
   */
  private static final Map<String, List<JSR310DateTimeDeserializerBase<? extends TemporalAccessor>>> DESERIALIZER_CACHE = new ConcurrentHashMap<>();

  private final Set<String> formatSet = new HashSet<>();

  protected CompositeJsr310Deserializer() {
    super(LocalDateTime.class);
  }

  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    for (String format : formatSet) {
      try {
        List<JSR310DateTimeDeserializerBase<? extends TemporalAccessor>> deserializerList =
            DESERIALIZER_CACHE.get(format);
        if (CollectionUtil.isNotEmpty(deserializerList)) {
          // 先使用LocalDateTimeDeserializer然后再使用LocalDateDeserializer
          // Use a LocalDateTimeDeserializer then use LocalDateDeserializer first
          for (JSR310DateTimeDeserializerBase<? extends TemporalAccessor> deserializer : deserializerList) {
            try {
              Object result = deserializer.deserialize(parser, context);
              if (result instanceof LocalDateTime) {
                return (LocalDateTime) result;
              } else if (result instanceof LocalDate) {
                return LocalDateTime.of((LocalDate) result, LocalTime.MIN);
              }
            } catch (Exception ignore) {
            }
          }
        }
      } catch (Exception ignored) {
      }
    }
    // formatList为空或指定的格式都匹配不上的时候使用默认反序列化
    // The default deserialization is used when the formatList is empty or neither of the specified formats matches
    return LocalDateTimeDeserializer.INSTANCE.deserialize(parser, context);
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    JsonFormat.Value jsonFormat = findFormatOverrides(ctxt, property, handledType());
    if (null != jsonFormat && jsonFormat.hasPattern()) {

      String pattern = jsonFormat.getPattern();
      if (StringUtil.isNotEmpty(pattern)) {
        for (String format : StringUtil.split(pattern, DOUBLE_HYPHEN)) {
          formatSet.add(format);
          DESERIALIZER_CACHE.computeIfAbsent(format, value -> {
            List<JSR310DateTimeDeserializerBase<? extends TemporalAccessor>> deserializerList = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
            // 使用LocalDateTimeDeserializer和LocalDateDeserializer两种反序列化化器
            deserializerList.add(new LocalDateTimeDeserializer(dateTimeFormatter));
            deserializerList.add(new LocalDateDeserializer(dateTimeFormatter));
            return deserializerList;
          });
        }
      }
    }

    return this;
  }

}
