package cn.ares.boot.base.model.page;

import cn.ares.boot.base.model.BaseRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Ares
 * @time: 2024-07-10 15:06:01
 * @description: 分页请求基类
 * @description: Page request
 * @version: JDK 1.8
 */
public class PageRequest extends BasePage implements BaseRequest {

  private static final long serialVersionUID = 3337327262007037606L;

  public PageRequest() {
  }

  private PageRequest(long current, long size) {
    super.setCurrent(current);
    super.setSize(size);
  }

  public PageRequest(Long current, Long size, List<SortField> sortFieldList) {
    super.setCurrent(current);
    super.setSize(size);
    super.setSortFieldList(sortFieldList);
  }

  /**
   * @author: Ares
   * @description: 构造分页请求
   * @description: Construct PageRequest object
   * @time: 2024-07-10 16:17:40
   * @params: [current, size] 当前页，每页显示条数
   * @return: cn.ares.boot.base.model.page.PageRequest 分页请求
   */
  public static PageRequest of(long current, long size) {
    return new PageRequest(current, size);
  }

  /**
   * 添加新的排序字段数组
   *
   * @param sortFields 排序字段数组
   * @return 返回分页参数本身
   */
  public PageRequest addSortField(SortField... sortFields) {
    getSortFieldList().addAll(Arrays.asList(sortFields));
    return this;
  }

  /**
   * 添加新的排序字段列表
   *
   * @param sortFieldList 排序字段列表
   * @return 返回分页参数本身
   */
  public PageRequest addSortField(List<SortField> sortFieldList) {
    getSortFieldList().addAll(sortFieldList);
    return this;
  }

  @Override
  public String toString() {
    return "PageRequest{" +
        "current=" + super.getCurrent() +
        ", size=" + super.getSize() +
        ", sortFieldList=" + super.getSortFieldList() +
        '}';
  }

}
