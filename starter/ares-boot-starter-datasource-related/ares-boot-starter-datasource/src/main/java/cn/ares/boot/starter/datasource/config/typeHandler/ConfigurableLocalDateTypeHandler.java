package cn.ares.boot.starter.datasource.config.typeHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author: Ares
 * @time: 2024-07-04 19:43:52
 * @description: 可配置LocalDate格式的类型处理器
 * @description: Type handler that can configure LocalDate format
 * @version: JDK 1.8
 */
@MappedTypes(LocalDate.class)
public class ConfigurableLocalDateTypeHandler extends AbstractDateBaseTypeHandler<LocalDate> {

  public ConfigurableLocalDateTypeHandler(DateTimeFormatter formatter) {
    super(formatter);
  }

  @Override
  public LocalDate parseColumnValue(String columnValue, DateTimeFormatter formatter) {
    return LocalDate.parse(columnValue, formatter);
  }

}