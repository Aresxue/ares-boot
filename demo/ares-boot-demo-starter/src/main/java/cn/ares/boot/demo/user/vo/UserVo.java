package cn.ares.boot.demo.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

/**
 * @author: Ares
 * @time: 2024-05-11 13:26:46
 * @description: 用户响应实体
 * @version: JDK 1.8
 */
@Data
@Schema(description = "用户响应对象")
public class UserVo {

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

  private String uuid;
  private LocalDateTime now;
  private String appName;

}
