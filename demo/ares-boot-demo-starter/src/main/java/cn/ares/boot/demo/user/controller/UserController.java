package cn.ares.boot.demo.user.controller;

import cn.ares.boot.demo.user.service.UserService;
import cn.ares.boot.demo.user.vo.UserVo;
import cn.ares.boot.util.spring.BeanCopyUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Ares
 * @time: 2024-07-19 11:15:57
 * @description: 用户请求控制器
 * @version: JDK 1.8
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Tags(@Tag(name = "用户请求控制器"))
public class UserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  private final UserService userService;

  @GetMapping("/queryUserById")
  public UserVo queryUserById(@RequestParam @NotNull Long id) {
    UserVo user = BeanCopyUtil.copy(userService.queryUserById(id), UserVo.class);
    user.setUuid(UUID.randomUUID().toString());
    return user;
  }


}
