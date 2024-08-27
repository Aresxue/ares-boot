package cn.ares.boot.demo.user.service;


import cn.ares.boot.demo.user.service.bo.UserBo;
import cn.ares.boot.demo.user.service.param.UserParam;

/**
 * @author: Ares
 * @time: 2024-04-24 13:52:14
 * @description: 用户服务
 * @version: JDK 1.8
 */
public interface UserService {

  UserBo queryUserById(Long id);

  UserBo testLogPrint(UserParam userParam);

}
