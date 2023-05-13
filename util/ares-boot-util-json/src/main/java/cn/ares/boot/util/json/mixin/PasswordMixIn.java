package cn.ares.boot.util.json.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author: Ares
 * @time: 2022-06-27 10:54:18
 * @description: 序列化时忽略password字段，防止密码泄露
 * @description: The password field is ignored during serialization to prevent password leakage
 * @version: JDK 1.8
 */
public interface PasswordMixIn {

  @JsonIgnore
  String password();

}
