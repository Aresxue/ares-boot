package cn.ares.boot.base.model.page;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: Ares
 * @time: 2024-07-10 15:00:29
 * @description: 分页对象
 * @description: Page object
 * @version: JDK 1.8
 */
public class Pageable<T> extends BasePage {

  private static final long serialVersionUID = 5384462636946835567L;

  /**
   * 记录列表
   */
  private List<T> recordList = Collections.emptyList();
  /**
   * 总数
   */
  private Long total = 0L;

  public Pageable() {
  }

  private Pageable(long current, long size) {
    this(current, size, 0);
  }

  private Pageable(long current, long size, long total) {
    if (current > 1) {
      super.setCurrent(current);
    }
    super.setSize(size);
    this.total = total;
  }


  /**
   * @author: Ares
   * @description: 构造分页对象
   * @description: Construct Pageable object
   * @time: 2024-07-10 15:36:13
   * @params: [current, size] 当前页，每页显示条数
   * @return: cn.ares.boot.base.model.page.Pageable<T> 分页对象
   */
  public static <T> Pageable<T> of(long current, long size) {
    return new Pageable<>(current, size);
  }

  /**
   * @author: Ares
   * @description: 构造分页对象
   * @description: Construct Pageable object
   * @time: 2024-07-10 15:36:13
   * @params: [current, size, total] 当前页，每页显示条数，总数
   * @return: cn.ares.boot.base.model.page.Pageable<T> 分页对象
   */
  public static <T> Pageable<T> of(long current, long size, long total) {
    return new Pageable<>(current, size, total);
  }

  /**
   * 是否存在上一页
   *
   * @return true / false
   */
  public boolean hasPrevious() {
    return super.getCurrent() > 1;
  }

  /**
   * 是否存在下一页
   *
   * @return true / false
   */
  public boolean hasNext() {
    return super.getCurrent() < this.queryPages();
  }

  /**
   * 当前分页总页数
   */
  public long queryPages() {
    long size = getSize();
    if (size == 0) {
      return 0L;
    }
    long total = getTotal();
    long pages = total / size;
    if (total % size != 0) {
      pages++;
    }
    return pages;
  }

  /**
   * 添加新的排序字段数组
   *
   * @param sortFields 排序字段数组
   * @return 返回分页参数本身
   */
  public Pageable<T> addSortField(SortField... sortFields) {
    getSortFieldList().addAll(Arrays.asList(sortFields));
    return this;
  }

  /**
   * 添加新的排序字段列表
   *
   * @param sortFieldList 排序字段列表
   * @return 返回分页参数本身
   */
  public Pageable<T> addSortField(List<SortField> sortFieldList) {
    getSortFieldList().addAll(sortFieldList);
    return this;
  }

  /**
   * 转换分页对象
   *
   * @param mapping 转换函数
   * @return 转换后的分页对象
   */
  public <R> Pageable<R> convert(Function<? super T, ? extends R> mapping) {
    List<T> recordList = this.getRecordList();
    if (null == recordList) {
      recordList = Collections.emptyList();
    }
    Pageable<R> newPageable = Pageable.of(this.getCurrent(), this.getSize(), this.getTotal());
    newPageable.setRecordList(recordList.stream().map(mapping).collect(Collectors.toList()));
    return newPageable;
  }

  public List<T> getRecordList() {
    return recordList;
  }

  public void setRecordList(List<T> recordList) {
    this.recordList = recordList;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  @Override
  public String toString() {
    return "Pageable{" + ", current=" + super.getCurrent() + ", size=" + super.getSize()
        + ", sortFieldList=" + super.getSortFieldList() + ", total=" + total + "recordList="
        + recordList + '}';
  }

}
