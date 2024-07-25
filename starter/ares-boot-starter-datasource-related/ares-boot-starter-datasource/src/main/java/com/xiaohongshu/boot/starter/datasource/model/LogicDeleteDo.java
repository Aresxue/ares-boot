package com.xiaohongshu.boot.starter.datasource.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * @author: Ares
 * @time: 2024-07-02 14:01:23
 * @description: 逻辑删除领域对象基类
 * @description: Logical delete domain object base class
 * @version: JDK 1.8
 */
public class LogicDeleteDo extends BaseDo {

  private static final long serialVersionUID = -150310452063319002L;

  /**
   * 逻辑删除字段 这里使用Integer而不是boolean同时字段不要带is避免潜在的序列化问题
   * Logical delete field Use Integer instead of boolean here and do not use is in the field to avoid potential serialization issues
   */
  @TableLogic
  @TableField(fill = FieldFill.INSERT, value = "is_deleted")
  private Integer deleted;

  public Integer getDeleted() {
    return deleted;
  }

  public void setDeleted(Integer deleted) {
    this.deleted = deleted;
  }

  @Override
  public String toString() {
    return "LogicDeleteDo{" +
        "deleted=" + deleted +
        "} " + super.toString();
  }

}
