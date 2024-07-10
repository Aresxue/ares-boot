package cn.ares.boot.base.model.exception;

import static cn.ares.boot.base.model.status.BaseSystemStatus.COMMON_FAIL;
import static cn.ares.boot.base.model.status.BaseSystemStatus.UNKNOWN_ERROR;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author: Ares
 * @time: 2024-07-10 16:42:05
 * @description: BusinessException test
 * @version: JDK 1.8
 */
public class BusinessExceptionTest {

  @Test
  public void test() {
    BusinessException businessException = new BusinessException(UNKNOWN_ERROR);
    Assertions.assertEquals(UNKNOWN_ERROR.getMessage(), businessException.getMessage());

    businessException = new BusinessException(COMMON_FAIL, "ares");
    Assertions.assertEquals("ares", businessException.getMessage());
  }


}
