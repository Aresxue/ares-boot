package cn.ares.boot.demo.user.domain.entity;

import cn.ares.boot.starter.datasource.model.OperatorDo;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("user")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDo extends OperatorDo {

  private static final long serialVersionUID = -6591628495091122080L;

  private String name;

  private LocalTime start;

  private LocalDateTime bizTime;

}
