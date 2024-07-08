package cn.ares.boot.starter.datasource.config.typeHandler;

import cn.ares.boot.util.common.DateUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author: Ares
 * @time: 2024-07-04 19:39:38
 * @description: 可配置LocalDateTime格式的类型处理器
 * @description: Type handler that can configure LocalDateTime format
 * @version: JDK 1.8
 */
@MappedTypes(LocalDateTime.class)
public class ConfigurableLocalDateTimeTypeHandler extends
    AbstractDateBaseTypeHandler<LocalDateTime> {

  public ConfigurableLocalDateTimeTypeHandler(DateTimeFormatter formatter) {
    super(formatter);
  }

  @Override
  public LocalDateTime parseColumnValue(String columnValue, DateTimeFormatter formatter) {
    try {
      return LocalDateTime.parse(columnValue, formatter);
    } catch (Exception exception) {
      return DateUtil.getDefaultLocalDateTime(columnValue);
    }
  }

}