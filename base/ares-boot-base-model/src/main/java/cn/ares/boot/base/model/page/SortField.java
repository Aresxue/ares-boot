package cn.ares.boot.base.model.page;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Ares
 * @time: 2022-06-10 15:28:43
 * @description: 排序字段
 * @description: Sort field
 * @version: JDK 1.8
 */
public class SortField implements Serializable {

  private static final long serialVersionUID = -7342671270425244017L;

  /**
   * 需要进行排序的字段
   */
  private String column;
  /**
   * 是否正序排列，默认 true
   */
  private boolean asc = true;

  public SortField() {
  }

  private SortField(String column, boolean asc) {
    this.column = column;
    this.asc = asc;
  }

  public static SortField of(String column, boolean asc) {
    return new SortField(column, asc);
  }

  public static SortField asc(String column) {
    return build(column, true);
  }

  public static SortField desc(String column) {
    return build(column, false);
  }

  public static List<SortField> ascs(String... columns) {
    return Arrays.stream(columns).map(SortField::asc).collect(Collectors.toList());
  }

  public static List<SortField> descs(String... columns) {
    return Arrays.stream(columns).map(SortField::desc).collect(Collectors.toList());
  }

  private static SortField build(String column, boolean asc) {
    return new SortField(column, asc);
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public boolean isAsc() {
    return asc;
  }

  public void setAsc(boolean asc) {
    this.asc = asc;
  }

  @Override
  public String toString() {
    return "SortField{" +
        "column='" + column + '\'' +
        ", asc=" + asc +
        '}';
  }

}