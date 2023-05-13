package cn.ares.boot.util.ognl;

import java.util.HashMap;
import java.util.Map;
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

  @Test
  public void testParse() {
    String expressionText = "ognl util test is #{result}";
    Map<String, String> map = new HashMap<>();
    map.put("result", "good");
    String parseResult = null;
    try {
      parseResult = OgnlUtil.parse(expressionText, "#{", "'", "}", "'", map);
    } catch (OgnlException e) {
      e.printStackTrace();
    }
    System.out.println("parse result: " + parseResult);
    Assert.isTrue(null != parseResult && parseResult.contains("good"), "解析失败");
  }

}
