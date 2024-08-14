package cn.ares.boot.starter.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: Ares
 * @time: 2024-08-14 16:20:52
 * @description: 响应对象基类
 * @version: JDK 1.8
 */
@Schema(description = "响应对象基类")
public class BaseVo implements Serializable {

  private static final long serialVersionUID = 5505774677115832488L;

  @Schema(description = "主键")
  private Long id;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
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
    return "BaseVo{" +
        "id=" + id +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        '}';
  }

}
