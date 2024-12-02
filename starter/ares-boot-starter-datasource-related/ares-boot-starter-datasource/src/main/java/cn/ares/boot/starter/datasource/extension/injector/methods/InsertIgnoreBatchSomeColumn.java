package cn.ares.boot.starter.datasource.extension.injector.methods;

import cn.ares.boot.util.common.StringUtil;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import java.util.function.Predicate;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * @author: Ares
 * @time: 2024-12-02 16:59:39
 * @description: 批量新增数据（忽略报错）
 * @description: Batch insert data (ignore error)
 * @version: JDK 1.8
 */
public class InsertIgnoreBatchSomeColumn extends InsertBatchSomeColumn {

  private static final long serialVersionUID = 7945498968463756217L;

  public InsertIgnoreBatchSomeColumn(
      Predicate<TableFieldInfo> predicate) {
    super(predicate);
  }

  @Override
  public SqlSource createSqlSource(Configuration configuration, String script,
      Class<?> parameterType) {
    if (StringUtil.isNotBlank(script)) {
      // 使用 (?i) 来忽略大小写
      script = script.replaceFirst("(?i)INSERT INTO", "INSERT IGNORE INTO");
    }
    return super.createSqlSource(configuration, script, parameterType);
  }

}
