package cn.ares.boot.util.servlet;


import static cn.ares.boot.util.common.constant.StringConstant.UNKNOWN;
import static cn.ares.boot.util.common.constant.SymbolConstant.COMMA;
import static cn.ares.boot.util.common.constant.SymbolConstant.SEMICOLON;
import static cn.ares.boot.util.servlet.constant.BrowserType.CHROME;
import static cn.ares.boot.util.servlet.constant.BrowserType.EDGE;
import static cn.ares.boot.util.servlet.constant.BrowserType.FIREFOX;
import static cn.ares.boot.util.servlet.constant.BrowserType.IE;
import static cn.ares.boot.util.servlet.constant.BrowserType.MSIE;
import static cn.ares.boot.util.servlet.constant.BrowserType.NETSCAPE;
import static cn.ares.boot.util.servlet.constant.BrowserType.NETSCAPE6;
import static cn.ares.boot.util.servlet.constant.BrowserType.OPERA;
import static cn.ares.boot.util.servlet.constant.BrowserType.OPR;
import static cn.ares.boot.util.servlet.constant.BrowserType.SAFARI;
import static cn.ares.boot.util.servlet.constant.ServletConstant.CLIENT_MAC_ADDRESS;
import static cn.ares.boot.util.servlet.constant.ServletConstant.USER_AGENT;

import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.common.constant.OperateSystem;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


/**
 * @author: Ares
 * @time: 2021-07-05 20:17:00
 * @description: Http servlet util
 * @version: JDK 1.8
 */
public class HttpServletUtil {

  private static final String VERSION = "version";

  /**
   * @author: Ares
   * @description: 获取请求的客户端ip
   * @description: Get the requested client ip
   * @time: 2021-04-14 15:52
   * @params: [request] 请求
   * @return: java.lang.String ip
   */
  public static String getFromIp(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
      // 多次反向代理后会有多个ip值，第一个ip才是真实ip
      // After multiple reverse proxies, there will be multiple ip values, the first ip is the real ip
      if (ip.contains(COMMA)) {
        ip = ip.split(COMMA)[0];
      }
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }

  /**
   * @author: Ares
   * @description: 获取操作系统
   * @description: Get operate system
   * @time: 2021-08-09 15:00：00
   * @params: [request] request
   * @return: java.lang.String 操作系统
   */
  public static String getOperateSystem(HttpServletRequest request) {
    String userAgent = request.getHeader(USER_AGENT);
    if (null == userAgent) {
      return null;
    }

    return OperateSystem.getOperateSystem(userAgent).getSystem();
  }


  /**
   * @author: Ares
   * @description: 获取浏览器信息
   * @description: Get browser information
   * @time: 2021-08-09 15:14:00
   * @params: [request] request
   * @return: java.lang.String browser
   */
  public static String getBrowser(HttpServletRequest request) {
    String userAgent = request.getHeader(USER_AGENT);
    if (null == userAgent) {
      return null;
    }
    String user = userAgent.toLowerCase();

    String browser = null;
    if (user.contains(EDGE.value())) {
      browser = StringUtil.replace(userAgent.substring(userAgent.indexOf("Edge")).split(" ")[0],
          "/", "-");
    } else if (user.contains(MSIE.value())) {
      String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
      browser =
          StringUtil.replace(substring.split(" ")[0], "MSIE", "IE") + "-" + substring.split(" ")[1];
    } else {
      String[] split = userAgent.substring(userAgent.indexOf("Version")).split(" ");
      if (user.contains(SAFARI.value()) && user.contains(VERSION)) {
        browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]
            + "-" + (split[0]).split("/")[1];
      } else if (user.contains(OPR.value()) || user.contains(OPERA.value())) {
        if (user.contains(OPERA.value())) {
          browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]
              + "-" + (split[0]).split("/")[1];
        } else if (user.contains(OPR.value())) {
          browser = StringUtil.replace(
              StringUtil.replace((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]), "/",
                  "-"), "OPR", "Opera");
        }
      } else if (user.contains(CHROME.value())) {
        browser = StringUtil.replace(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0],
            "/", "-");
      } else if ((user.contains(NETSCAPE.value())) || (user.contains(NETSCAPE6.value())) ||
          (user.contains("mozilla/4.7")) || (user.contains("mozilla/4.78")) ||
          (user.contains("mozilla/4.08")) || (user.contains("mozilla/3"))) {
        browser = "Netscape-?";
      } else if (user.contains(FIREFOX.value())) {
        browser = StringUtil.replace(
            userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0], "/", "-");
      } else if (user.contains(IE.value())) {
        String version = StringUtil.replace(
            userAgent.substring(userAgent.indexOf(IE.value())).split(" ")[0], "rv:", "-");
        browser = "IE" + version.substring(0, version.length() - 1);
      } else {
        browser = "UnKnown, More-Info: " + userAgent;
      }
    }

    return browser;
  }

  /**
   * @author: Ares
   * @description: 获取页面相对路径
   * @description: Get page relative path
   * @time: 2021-08-09 15:52:00
   * @params: [request] request
   * @return: java.lang.String page path
   */
  public static String getPage(HttpServletRequest request) {
    String referer = request.getHeader("Referer");
    if (null == referer) {
      return null;
    }
    String prefix = StringUtil.replace(request.getRequestURL().toString(), request.getServletPath(),
        "");
    return StringUtil.replace(referer, prefix, "");
  }

  /**
   * @author: Ares
   * @description: 获取客户端mac地址
   * @description: Get client mac address
   * @time: 2023-05-08 16:46:13
   * @params: [httpServletRequest]
   * @return: java.lang.String 客户端mac地址
   */
  public static String getClientMacAddress(HttpServletRequest httpServletRequest) {
    return httpServletRequest.getHeader(CLIENT_MAC_ADDRESS);
  }

  /**
   * @author: Ares
   * @description: 获取内容类型
   * @description: Get Content-Type
   * @time: 2023-03-28 11:29:16
   * @params: [request] servlet request
   * @return: java.lang.String content-Type
   */
  public static String getContentType(ServletRequest request) {
    String contentType = request.getContentType();
    return buildContentType(contentType);
  }

  /**
   * @author: Ares
   * @description: 获取内容类型
   * @description: Get Content-Type
   * @time: 2023-03-28 11:29:16
   * @params: [response] servlet response
   * @return: java.lang.String content-Type
   */
  public static String getContentType(ServletResponse response) {
    String contentType = response.getContentType();
    return buildContentType(contentType);
  }

  private static String buildContentType(String contentType) {
    if (StringUtil.isBlank(contentType)) {
      return contentType;
    }
    if (contentType.contains(SEMICOLON)) {
      contentType = StringUtil.split(contentType, SEMICOLON)[0];
    }
    return contentType;
  }

}
