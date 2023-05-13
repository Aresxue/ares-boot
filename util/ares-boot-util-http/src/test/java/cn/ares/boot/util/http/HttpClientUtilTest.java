package cn.ares.boot.util.http;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2021-10-21 16:47
 * @description: http client util test
 * @version: JDK 1.8
 */
public class HttpClientUtilTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtilTest.class);

  @Test
  public void testGet() {
    try {
      String result = HttpClientUtil.get("https://www.baidu.com");
      LOGGER.info(result);
    } catch (Exception e) {
      LOGGER.error("exception: ", e);
    }
  }

}

