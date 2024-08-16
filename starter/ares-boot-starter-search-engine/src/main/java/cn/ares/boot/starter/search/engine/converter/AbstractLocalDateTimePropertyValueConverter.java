package cn.ares.boot.starter.search.engine.converter;

import cn.ares.boot.util.common.StringUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.core.mapping.PropertyValueConverter;
import org.springframework.lang.NonNull;

/**
 * @author: Ares
 * @time: 2024-08-16 16:18:22
 * @description: LocalDateTime属性值转换器
 * @description: LocalDateTime property value converter
 * @version: JDK 1.8
 */
public abstract class AbstractLocalDateTimePropertyValueConverter implements
    PropertyValueConverter {

  private final DateTimeFormatter formatter;

  public AbstractLocalDateTimePropertyValueConverter(String pattern) {
    this(pattern, "GMT+8");
  }

  public AbstractLocalDateTimePropertyValueConverter(String pattern, String timezone) {
    ZoneId zoneId;
    if (timezone.contains("+")) {
      List<String> stringList = StringUtil.listSplit(timezone, "+");
      zoneId = ZoneId.ofOffset(stringList.get(0),
          ZoneOffset.ofHours(Integer.parseInt(stringList.get(1))));
    } else {
      zoneId = ZoneId.of(timezone);
    }
    this.formatter = DateTimeFormatter.ofPattern(pattern).withZone(zoneId);
  }

  public AbstractLocalDateTimePropertyValueConverter(String pattern, ZoneId zoneId) {
    this.formatter = DateTimeFormatter.ofPattern(pattern).withZone(zoneId);
  }

  @NotNull
  @Override
  public Object write(@NonNull Object value) {
    if (value instanceof LocalDateTime) {
      LocalDateTime localDateTime = (LocalDateTime) value;
      return formatter.format(localDateTime);
    }
    throw new IllegalStateException(
        "Current convert only supported LocalDateTime, value class: " + value.getClass().getName());
  }

  @NotNull
  @Override
  public Object read(@NonNull Object value) {
    if (value instanceof LocalDateTime) {
      return value;
    }
    return formatter.parse(value.toString());
  }

}
