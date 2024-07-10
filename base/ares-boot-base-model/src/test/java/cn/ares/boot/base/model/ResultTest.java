package cn.ares.boot.base.model;

import static cn.ares.boot.base.model.status.BaseSystemStatus.UNKNOWN_ERROR;

import cn.ares.boot.util.json.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author: Ares
 * @time: 2024-07-10 16:05:59
 * @description: Result test
 * @version: JDK 1.8
 */
public class ResultTest {

  @Test
  public void test() {
    Result<String> result = Result.success();
    Assertions.assertTrue(result.isSuccess());

    result = Result.success("ares");
    Assertions.assertTrue(result.isSuccess());
    Assertions.assertNotNull(result.getData());

    result = Result.fail();
    Assertions.assertFalse(result.isSuccess());

    result = Result.fail(UNKNOWN_ERROR);
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertEquals(UNKNOWN_ERROR.getMessage(), result.getMessage());

    String resultStr = JsonUtil.toJsonString(result);
    Result<String> restoreResult = JsonUtil.parseObject(resultStr,
        new TypeReference<Result<String>>() {
        });
    Assertions.assertNotNull(restoreResult);
    Assertions.assertFalse(result.isSuccess());
    Assertions.assertEquals(UNKNOWN_ERROR.getMessage(), result.getMessage());
    Assertions.assertNull(restoreResult.getData());

    result.message("i am a format message: %s", "ares");
    Assertions.assertEquals("i am a format message: ares", result.getMessage());
  }

}
