package cn.ares.boot.starter.datasource.jdbc.interceptor;

import cn.ares.boot.base.config.BootEnvironment;
import cn.ares.boot.base.log.util.LoggerUtil;
import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import java.util.Properties;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2022-09-15 09:56:43
 * @description: Abstract QueryInterceptor
 * @version: JDK 1.8
 */
public abstract class AbstractQueryInterceptor implements QueryInterceptor {

  public static final Logger SQL_LOGGER = LoggerFactory.getLogger("sqlInterceptor");

  @Override
  public QueryInterceptor init(MysqlConnection mysqlConnection, Properties properties, Log log) {
    return this;
  }

  @Override
  public <T extends Resultset> T preProcess(Supplier<String> sqlSupplier, Query query) {
    if (null != query && BootEnvironment.isApplicationReady()) {
      // The alarm does not affect the business when the failure occurs
      try {
        handle(sqlSupplier.get());
      } catch (Exception e) {
        LoggerUtil.error("jdbc query interceptor handle sql exception: ", e);
      }
    }
    return null;
  }

  public abstract void handle(String sql);

  @Override
  public <T extends Resultset> T postProcess(Supplier<String> sqlSupplier, Query query, T t,
      ServerSession serverSession) {
    return null;
  }

  @Override
  public boolean executeTopLevelOnly() {
    return false;
  }

  @Override
  public void destroy() {
  }

}
