package cn.ares.boot.util.ognl;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import ognl.OgnlException;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * @author: Ares
 * @time: 2021-05-11 15:22:00
 * @description: ognl util test
 * @version: JDK 1.8
 */
public class OgnlUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(OgnlUtilTest.class);

  @Test
  public void testParse() {
    String expressionText = "ognl util test is #{result}";
    Map<String, String> map = new HashMap<>();
    map.put("result", "good");
    String parseResult = null;
    try {
      parseResult = OgnlUtil.parse(expressionText, "#{", "'", "}", "'", map);
    } catch (OgnlException ognlException) {
      JdkLoggerUtil.warn(LOGGER, "parse error: ", ognlException);
    }
    LOGGER.info("parse result: " + parseResult);
    Assert.isTrue(null != parseResult && parseResult.contains("good"), "解析失败");
  }

}
