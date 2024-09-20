package cn.ares.boot.starter.web.util;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author: Ares
 * @time: 2023-02-09 11:02:26
 * @description: web工具类
 * @description: web util
 * @version: JDK 1.8
 */
public class WebUtil {

  /**
   * @author: Ares
   * @description: 获取当前http servlet请求
   * @description: Get current http servlet request
   * @time: 2023-02-09 11:03:18
   * @return: javax.servlet.http.HttpServletRequest
   */
  public static HttpServletRequest getHttpServletRequest() {
    ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
    if (null == servletRequestAttributes) {
      return null;
    }
    return servletRequestAttributes.getRequest();
  }

}
