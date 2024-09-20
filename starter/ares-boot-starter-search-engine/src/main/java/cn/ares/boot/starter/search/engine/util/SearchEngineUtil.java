package cn.ares.boot.starter.search.engine.util;

import static org.springframework.data.elasticsearch.core.RefreshPolicy.WAIT_UNTIL;

import java.util.function.Supplier;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.query.BulkOptions;
import org.springframework.data.elasticsearch.core.query.BulkOptions.BulkOptionsBuilder;

/**
 * @author: Ares
 * @time: 2024-09-10 15:18:22
 * @description: 搜索引擎工具类
 * @description: Search engine util
 * @version: JDK 1.8
 */
public class SearchEngineUtil {

  /**
   * @author: Ares
   * @description: 执行操作获取结果直到其刷盘完成
   * @description: Get the result of the operation until it is flushed
   * @time: 2024-09-10 15:29:38
   * @params: [supplier] 执行操作
   * @return: T 执行操作结果
   */
  public static <T> T getWaitUntil(Supplier<T> supplier) {
    return getWithRefreshPolicy(WAIT_UNTIL, supplier);
  }

  /**
   * @author: Ares
   * @description: 执行操作直到其刷盘完成
   * @description: Perform the operation until it is flushed
   * @time: 2024-09-10 15:32:18
   * @params: [runnable] 执行操作
   */
  public static void runWaitUntil(Runnable runnable) {
    runWithRefreshPolicy(WAIT_UNTIL, runnable);
  }

  /**
   * @author: Ares
   * @description: 根据刷新策略执行操作
   * @description: Perform operations according to the refresh policy
   * @time: 2024-09-10 15:29:17
   * @params: [refreshPolicy, runnable] 刷新策略, 执行操作
   */
  public static void runWithRefreshPolicy(RefreshPolicy refreshPolicy, Runnable runnable) {
    BulkOptions bulkOptions = buildBulkOptions(refreshPolicy);
    runWithBulkOptions(bulkOptions, runnable);
  }

  /**
   * @author: Ares
   * @description: 根据刷新策略执行操作获取结果
   * @description: Get the result according to the refresh policy
   * @time: 2024-09-10 15:29:38
   * @params: [refreshPolicy, supplier] 刷新策略, 执行操作
   * @return: T 执行操作结果
   */
  public static <T> T getWithRefreshPolicy(RefreshPolicy refreshPolicy, Supplier<T> supplier) {
    BulkOptions bulkOptions = buildBulkOptions(refreshPolicy);
    return getWithBulkOptions(bulkOptions, supplier);
  }

  /**
   * @author: Ares
   * @description: 根据批量选项执行操作获取结果
   * @description: Get the result according to the bulk options
   * @time: 2024-09-10 15:22:02
   * @params: [options, supplier] 批量选项, 执行操作
   * @return: T 操作结果
   */
  public static <T> T getWithBulkOptions(BulkOptions options, Supplier<T> supplier) {
    BulkOptions oldOptions = BulkOptionsHolder.getBulkOptions();
    BulkOptionsHolder.setBulkOptions(options);
    try {
      return supplier.get();
    } finally {
      BulkOptionsHolder.setBulkOptions(oldOptions);
    }
  }

  /**
   * @author: Ares
   * @description: 根据批量选项执行操作
   * @description: Perform operations according to bulk options
   * @time: 2024-09-10 15:22:23
   * @params: [options, runnable] 批量选项, 执行操作
   */
  public static void runWithBulkOptions(BulkOptions options, Runnable runnable) {
    BulkOptions oldOptions = BulkOptionsHolder.getBulkOptions();
    BulkOptionsHolder.setBulkOptions(options);
    try {
      runnable.run();
    } finally {
      BulkOptionsHolder.setBulkOptions(oldOptions);
    }
  }

  /**
   * @author: Ares
   * @description: 根据刷新策略构造批量选项
   * @description: Construct bulk options according to the refresh policy
   * @time: 2024-09-10 15:26:05
   * @params: [refreshPolicy] 刷新策略
   * @return: org.springframework.data.elasticsearch.core.query.BulkOptions 批量选项
   */
  public static BulkOptions buildBulkOptions(RefreshPolicy refreshPolicy) {
    BulkOptionsBuilder builder = BulkOptions.builder();
    if (null != refreshPolicy) {
      builder.withRefreshPolicy(refreshPolicy);
    }
    return builder.build();
  }

}
