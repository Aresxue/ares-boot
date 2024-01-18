package cn.ares.boot.util.common.log;

import static cn.ares.boot.util.common.DateUtil.DATE_FORMAT_WHIFFLETREE_MILLIS;

import cn.ares.boot.util.common.DateUtil;
import cn.ares.boot.util.common.ExceptionUtil;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author: Ares
 * @time: 2024-01-18 19:13:46
 * @description: 简单的用于控制台的日志格式化器
 * @description: Simple console formatter
 * @version: JDK 1.8
 */
class SimpleConsoleFormatter extends Formatter {

  @Override
  public String format(LogRecord record) {
    StringBuilder builder = new StringBuilder();

    Date date = new Date(record.getMillis());
    builder.append(DateUtil.getFormat(DATE_FORMAT_WHIFFLETREE_MILLIS).format(date))
        .append(" ").append("[").append(record.getThreadID()).append("]")
        .append(" ").append(record.getLevel())
        .append(" ").append(record.getSourceClassName())
        .append("#").append(record.getSourceMethodName())
        .append("#").append(record.getSequenceNumber())
        .append(": ").append(record.getMessage());
    Throwable throwable = record.getThrown();
    if (null != throwable) {
      builder.append(ExceptionUtil.toString(throwable));
    }

    builder.append(System.lineSeparator());

    return builder.toString();
  }

}
