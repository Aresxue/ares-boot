package cn.ares.boot.base.model.page;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Ares
 * @time: 2024-07-10 15:10:07
 * @description: 分页基础对象
 * @description: Base page
 * @version: JDK 1.8
 */
public class BasePage implements Serializable {

  private static final long serialVersionUID = -7351452443121990343L;

  /**
   * 当前页
   */
  private Long current = 1L;
  /**
   * 每页显示条数
   */
  private Long size = 10L;

  /**
   * 排序字段列表
   */
  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  private List<SortField> sortFieldList = new ArrayList<>();

  public Long getCurrent() {
    return current;
  }

  public void setCurrent(Long current) {
    this.current = current;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public List<SortField> getSortFieldList() {
    return sortFieldList;
  }

  public void setSortFieldList(List<SortField> sortFieldList) {
    this.sortFieldList = sortFieldList;
  }

  @Override
  public String toString() {
    return "BasePage{" +
        "current=" + current +
        ", size=" + size +
        ", sortFieldList=" + sortFieldList +
        '}';
  }

}
