package cn.ares.boot.starter.web.handler;

import cn.ares.boot.starter.web.util.WebUtil;
import cn.ares.boot.util.common.MapUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.json.JsonUtil;
import cn.ares.boot.util.servlet.HttpServletUtil;
import cn.ares.boot.util.servlet.RepeatableHttpServletRequestWrapper;
import java.nio.charset.Charset;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author: Ares
 * @time: 2024-07-19 15:19:57
 * @description: 抽象全局异常处理器
 * @description: Abstract global exception handler
 * @version: JDK 1.8
 */
public abstract class AbstractGlobalControllerExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      AbstractGlobalControllerExceptionHandler.class);

  /**
   * 失败时输出的消息头的名称
   */
  @Value("${ares.web.print-header-names-when-fail:content-type,referer,user-agent}")
  private String[] printHeaderNamesWhenFail;

  protected void printErrorLog(String exceptionMsg, int code, Throwable throwable) {
    HttpServletRequest httpServletRequest = WebUtil.getHttpServletRequest();
    if (null == httpServletRequest) {
      LOGGER.error("http request handle fail, code: {}, {}:  ", code, exceptionMsg, throwable);
    } else {
      String fromIp = HttpServletUtil.getFromIp(httpServletRequest);
      String path = httpServletRequest.getServletPath();
      String query = JsonUtil.toJsonString(httpServletRequest.getParameterMap());
      Map<String, String> printHeaderMap = MapUtil.newHashMap(8);
      for (String headerName : printHeaderNamesWhenFail) {
        printHeaderMap.put(headerName, httpServletRequest.getHeader(headerName));
      }
      String body = "";
      if (httpServletRequest instanceof RepeatableHttpServletRequestWrapper) {
        RepeatableHttpServletRequestWrapper requestWrapper = (RepeatableHttpServletRequestWrapper) httpServletRequest;
        String requestEncoding = httpServletRequest.getCharacterEncoding();
        if (StringUtil.isEmpty(requestEncoding)) {
          requestEncoding = Charset.defaultCharset().name();
        }
        body = new String(requestWrapper.getCachedBytes(), Charset.forName(requestEncoding));
      }
      LOGGER.error(
          "http request handle fail, code: {}, from ip: {}, path: {}, query: {}, body: {}, headers: {}, {}: ",
          code, fromIp, path, query, body, JsonUtil.toJsonString(printHeaderMap), exceptionMsg,
          throwable);
    }
  }

}
