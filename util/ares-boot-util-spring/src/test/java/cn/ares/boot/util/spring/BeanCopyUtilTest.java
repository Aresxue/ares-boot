package cn.ares.boot.util.spring;

import cn.ares.boot.util.spring.entity.CopyUser;
import cn.ares.boot.util.spring.entity.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2021-07-29 10:46:00
 * @description: BeanCopyUtil test
 * @version: JDK 1.8
 */
public class BeanCopyUtilTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeanCopyUtilTest.class);

  @Test
  public void test() {
    final User user = new User();
    user.setId(1L);
    user.setNames("kele");
    user.setFather("ares");
    user.setFatherAge(18);
    CopyUser copyUser = BeanCopyUtil.copyProperties(user, CopyUser.class);
    LOGGER.info("copy user: {}", copyUser);
    // 包装类和基本类型不会互转
    copyUser = BeanCopyUtil.copy(user, CopyUser.class);
    LOGGER.info("copy user: {}", copyUser);
  }
}
