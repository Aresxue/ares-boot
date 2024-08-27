package cn.ares.boot.demo.user.controller;

import cn.ares.boot.base.model.Result;
import cn.ares.boot.demo.user.request.UserRequest;
import cn.ares.boot.demo.user.service.UserService;
import cn.ares.boot.demo.user.service.param.UserParam;
import cn.ares.boot.demo.user.vo.UserVo;
import cn.ares.boot.util.spring.BeanCopyUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @Operation(summary = "获取用户", description = "根据主键获取用户信息")
  @ApiOperationSupport(order = 1)
  @ApiResponse(responseCode = "200", description = "用户信息")
  @GetMapping("/queryUserById")
  public Result<UserVo> queryUserById(@RequestParam @NotNull Long id) {
    UserVo user = BeanCopyUtil.copy(userService.queryUserById(id), UserVo.class);
    user.setUuid(UUID.randomUUID().toString());
    user.setNow(LocalDateTime.now());
    return Result.success(user);
  }

  @Operation(summary = "测试Get请求时间传参", description = "主要针对LocalDataTime和LocalTime类型")
  @GetMapping("/testLocalDataTimeByGet")
  public Result<UserVo> testLocalDataTimeByGet(@RequestParam LocalDateTime bizTime,
      @RequestParam LocalTime start) {
    UserVo userVo = new UserVo();
    userVo.setBizTime(bizTime);
    userVo.setStart(start);
    return Result.success(userVo);
  }

  @Operation(summary = "测试Post请求时间传参", description = "主要针对LocalDataTime和LocalTime类型")
  @PostMapping("/testLocalDataTimeByPost")
  public Result<UserVo> testLocalDataTimeByPost(@RequestBody UserRequest userRequest) {
    UserVo userVo = new UserVo();
    userVo.setBizTime(userRequest.getBizTime());
    userVo.setStart(userRequest.getStart());
    return Result.success(userVo);
  }


  @Operation(summary = "测试日志打印", description = "测试@LogPrint注解")
  @PostMapping("/testLogPrint")
  public Result<UserVo> testLogPrint(@RequestBody UserRequest userRequest) {
    UserParam userParam = BeanCopyUtil.copy(userRequest, UserParam.class);
    UserVo user = BeanCopyUtil.copy(userService.testLogPrint(userParam), UserVo.class);
    return Result.success(user);
  }

}
