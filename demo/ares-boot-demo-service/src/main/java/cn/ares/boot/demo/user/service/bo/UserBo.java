package cn.ares.boot.demo.user.service.bo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

@Data
public class UserBo {

  private String name;

  private LocalTime start;

  private LocalDateTime bizTime;

  private String createBy;

  private String updateBy;

  /**
   * 是否已删除
   */
  private Integer deleted;

  /**
   * 主键
   */
  private Long id;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  private LocalDateTime updateTime;

  private String appName;

}
