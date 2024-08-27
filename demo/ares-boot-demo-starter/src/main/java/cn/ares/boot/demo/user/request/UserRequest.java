package cn.ares.boot.demo.user.request;

import cn.ares.boot.base.model.BaseRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

/**
 * @author: Ares
 * @time: 2024-07-19 14:06:28
 * @description: 用户请求对象
 * @version: JDK 1.8
 */
@Data
public class UserRequest implements BaseRequest {

  private static final long serialVersionUID = 1715850197468332769L;

  private LocalDateTime bizTime;
  private LocalTime start;

  private Long id;

}
