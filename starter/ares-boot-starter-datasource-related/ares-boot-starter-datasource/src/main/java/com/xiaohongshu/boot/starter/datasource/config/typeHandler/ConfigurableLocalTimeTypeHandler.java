package com.xiaohongshu.boot.starter.datasource.config.typeHandler;

import cn.ares.boot.util.common.DateUtil;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author: Ares
 * @time: 2024-07-04 19:45:23
 * @description: 可配置LocalTime格式的类型处理器
 * @description: Type handler that can configure LocalTime format
 * @version: JDK 1.8
 */
@MappedTypes(LocalTime.class)
public class ConfigurableLocalTimeTypeHandler extends AbstractDateBaseTypeHandler<LocalTime> {

  public ConfigurableLocalTimeTypeHandler(DateTimeFormatter formatter) {
    super(formatter);
  }

  @Override
  public LocalTime parseColumnValue(String columnValue, DateTimeFormatter formatter) {
    try {
      return LocalTime.parse(columnValue, formatter);
    } catch (Exception exception) {
      return DateUtil.getDefaultLocalTime(columnValue);
    }
  }

}


