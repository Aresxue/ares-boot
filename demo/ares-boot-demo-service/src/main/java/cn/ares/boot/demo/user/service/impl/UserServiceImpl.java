package cn.ares.boot.demo.user.service.impl;

import cn.ares.boot.demo.user.domain.mapper.UserMapper;
import cn.ares.boot.demo.user.service.UserService;
import cn.ares.boot.demo.user.service.bo.UserBo;
import cn.ares.boot.util.spring.BeanCopyUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author: Ares
 * @time: 2024-04-24 13:53:55
 * @description: 用户服务
 * @version: JDK 1.8
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;

  @Override
  public UserBo queryUserById(Long id) {
    return BeanCopyUtil.copy(userMapper.selectById(id), UserBo.class);
  }

}
