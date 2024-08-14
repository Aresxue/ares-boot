package cn.ares.boot.starter.web.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author: Ares
 * @time: 2024-08-14 16:22:53
 * @description: 逻辑删除响应对象基类
 * @version: JDK 1.8
 */
@Schema(description = "逻辑删除响应对象基类")
public class LogicDeleteVo extends BaseVo {

  private static final long serialVersionUID = -2615948401148929833L;

  @Schema(description = "逻辑删除字段")
  private Integer deleted;

  public Integer getDeleted() {
    return deleted;
  }

  public void setDeleted(Integer deleted) {
    this.deleted = deleted;
  }

  @Override
  public String toString() {
    return "LogicDeleteVo{" +
        "deleted=" + deleted +
        "} " + super.toString();
  }

}
