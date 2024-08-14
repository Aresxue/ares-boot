package cn.ares.boot.starter.web.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author: Ares
 * @time: 2024-08-14 16:24:57
 * @description: 带操作人信息的逻辑删除响应对象基类
 * @version: JDK 1.8
 */
@Schema(description = "带操作人信息的逻辑删除响应对象基类")
public class OperatorVo extends LogicDeleteVo {

  private static final long serialVersionUID = 6553013112182972334L;

  @Schema(description = "创建人")
  private String createBy;

  @Schema(description = "更新人")
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
    return "OperatorVo{" +
        "createBy='" + createBy + '\'' +
        ", updateBy='" + updateBy + '\'' +
        "} " + super.toString();
  }

}
