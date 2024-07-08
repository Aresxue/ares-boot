package cn.ares.boot.starter.datasource.config.typeHandler;

import cn.ares.boot.util.common.StringUtil;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * @author: Ares
 * @time: 2024-07-04 19:48:23
 * @description: 抽象的时间类型处理器
 * @description: Abstract time type handler
 * @version: JDK 1.8
 */
@MappedJdbcTypes(value = JdbcType.DATE, includeNullJdbcType = true)
public abstract class AbstractDateBaseTypeHandler<T extends TemporalAccessor> extends
    BaseTypeHandler<T> {

  private final DateTimeFormatter formatter;

  public AbstractDateBaseTypeHandler(DateTimeFormatter formatter) {
    this.formatter = formatter;
  }

  @Override
  public void setNonNullParameter(PreparedStatement statement, int columnIndex, T columnValue,
      JdbcType jdbcType) throws SQLException {
    if (null != columnValue) {
      statement.setString(columnIndex, formatter.format(columnValue));
    }
  }

  @Override
  public T getNullableResult(ResultSet resultSet, String columnName)
      throws SQLException {
    String columnValue = resultSet.getString(columnName);
    if (StringUtil.isEmpty(columnValue)) {
      return null;
    }
    return parseColumnValue(columnValue, formatter);
  }

  @Override
  public T getNullableResult(ResultSet resultSet, int columnIndex)
      throws SQLException {
    String columnValue = resultSet.getString(columnIndex);
    if (StringUtil.isEmpty(columnValue)) {
      return null;
    }
    return parseColumnValue(columnValue, formatter);
  }

  @Override
  public T getNullableResult(CallableStatement statement, int columnIndex)
      throws SQLException {
    String columnValue = statement.getString(columnIndex);
    if (StringUtil.isEmpty(columnValue)) {
      return null;
    }
    return parseColumnValue(columnValue, formatter);
  }

  public abstract T parseColumnValue(String columnValue, DateTimeFormatter formatter);

}
