package com.xiaohongshu.boot.starter.datasource.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: Ares
 * @time: 2024-07-02 13:59:23
 * @description: 领域对象基类
 * @description: Domain object base class
 * @version: JDK 1.8
 */
public class BaseDo implements Serializable {

  private static final long serialVersionUID = -2448140408302894683L;

  /**
   * 主键
   */
  @TableId(type = IdType.INPUT)
  private Long id;

  /**
   * 创建时间
   */
  @TableField(fill = FieldFill.INSERT, value = "create_time")
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
  private LocalDateTime updateTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }

  public LocalDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
  }

  @Override
  public String toString() {
    return "DomainObject{" +
        "id=" + id +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        '}';
  }

}
