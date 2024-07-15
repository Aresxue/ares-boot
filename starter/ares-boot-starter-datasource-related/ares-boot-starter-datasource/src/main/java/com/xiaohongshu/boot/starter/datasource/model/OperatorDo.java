package com.xiaohongshu.boot.starter.datasource.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * @author: Ares
 * @time: 2024-07-02 14:06:34
 * @description: 带操作人信息的逻领域对象基类
 * @description: Domain object base class with operator information
 * @version: JDK 1.8
 */
public class OperatorDo extends LogicDeleteDo {

  private static final long serialVersionUID = -49725604308795846L;

  /**
   * 创建人
   */
  @TableField(fill = FieldFill.INSERT, value = "create_by")
  private String createBy;
  /**
   * 更新人
   */
  @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_by")
  private String updateBy;

  public String getCreateBy() {
    return createBy;
  }

  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }

  public String getUpdateBy() {
    return updateBy;
  }

  public void setUpdateBy(String updateBy) {
    this.updateBy = updateBy;
  }

  @Override
  public String toString() {
    return "OperatorDo{" +
        "createBy='" + createBy + '\'' +
        ", updateBy='" + updateBy + '\'' +
        "} " + super.toString();
  }

}
