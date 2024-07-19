package cn.ares.business.demo.user.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

@Data
public class UserDo implements Serializable {

  private static final long serialVersionUID = -6591628495091122080L;

  private String name;

  private LocalTime start;

  private LocalDateTime bizTime;

  private Long createBy;

  private Long updateBy;

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

}
