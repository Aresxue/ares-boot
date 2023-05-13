package cn.ares.boot.util.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

/**
 * @author: Ares
 * @time: 2021-04-14 20:30:00
 * @description: 对HttpServletRequest进行重写，解决多次读取问题
 * 1、用来接收application/json参数数据类型，即@RequestBody注解标注的参数,解决多次读取问题
 * 2、用来解决注解@RequestParam通过POST/PUT/DELETE/PATCH方法传递参数，解决多次读取问题 首先看一下springboot控制器三个注解：
 * 1、@PathVariable注解是REST风格url获取参数的方式，只能用在GET请求类型，通过getParameter获取参数
 * 2、@RequestParam注解支持GET和POST/PUT/DELETE/PATCH方式，Get方式通过getParameter获取参数和post方式通过getInputStream或getReader获取参数
 * 3、@RequestBody注解支持POST/PUT/DELETE/PATCH，可以通过getInputStream和getReader获取参数
 * @description: Rewrite HttpServletRequest to solve the problem of multiple reads
 * @version: JDK 1.8
 */
public class RepeatableHttpServletRequestWrapper extends HttpServletRequestWrapper {

  private final byte[] cachedBytes;

  private final HttpServletRequest request;

  private final Map<String, String[]> parameterMap;

  public RepeatableHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    this.request = request;
    parameterMap = request.getParameterMap();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    IOUtils.copy(request.getInputStream(), byteArrayOutputStream);
    this.cachedBytes = byteArrayOutputStream.toByteArray();
  }


  @Override
  public ServletInputStream getInputStream() {
    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBytes);
    return new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
      }

      @Override
      public int read() {
        return byteArrayInputStream.read();
      }
    };
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  public byte[] getCachedBytes() {
    return cachedBytes;
  }

  @Override
  public HttpServletRequest getRequest() {
    return request;
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    return parameterMap;
  }

}
