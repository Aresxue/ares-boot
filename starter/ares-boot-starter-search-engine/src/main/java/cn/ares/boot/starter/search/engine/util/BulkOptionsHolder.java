package cn.ares.boot.starter.search.engine.util;

import org.springframework.data.elasticsearch.core.query.BulkOptions;

/**
 * @author: Ares
 * @time: 2024-09-10 15:12:13
 * @description: BulkOptions持有者
 * @description: BulkOptions holder
 * @version: JDK 1.8
 */
public class BulkOptionsHolder {

  private static final InheritableThreadLocal<BulkOptions> BULK_OPTIONS_REF = new InheritableThreadLocal<>();

  /**
   * @author: Ares
   * @description: 获取批量选项
   * @time: 2024-09-10 15:23:31
   * @return: org.springframework.data.elasticsearch.core.query.BulkOptions 批量选项
   */
  public static BulkOptions getBulkOptions() {
    return BULK_OPTIONS_REF.get();
  }

  /*
   * @author: Ares
   * @description: 设置批量选项
   * @time: 2024-09-10 15:23:18
   * @params: [bulkOptions] 批量选项
   */
  public static void setBulkOptions(BulkOptions bulkOptions) {
    BULK_OPTIONS_REF.set(bulkOptions);
  }

  /**
   * @author: Ares
   * @description: 清理
   * @time: 2024-09-09 19:10:56
   */
  public static void clear() {
    BULK_OPTIONS_REF.remove();
  }

}
