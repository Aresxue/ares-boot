package cn.ares.boot.util.servlet;

import org.apache.commons.io.output.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.io.output.TeeOutputStream;

/**
 * @author: Ares
 * @time: 2021-04-14 20:47:00
 * @description: 对HttpServletResponse进行重写，解决多次读取问题
 * @description: Rewrite HttpServletResponse to solve the problem of multiple reads
 * @version: JDK 1.8
 */
public class RepeatableHttpServletResponseWrapper extends HttpServletResponseWrapper {

  private final ByteArrayOutputStream output;

  private ServletOutputStream filterOutput;

  public RepeatableHttpServletResponseWrapper(HttpServletResponse response) {
    super(response);
    output = new ByteArrayOutputStream();
  }

  /**
   * @author: Ares
   * @description: 利用TeeOutputStream复制流，解决多次读写问题
   * 用super.getOutputStream来获取源outPutStream，也可以用注释的那种方式获取，传过来
   * @time: 2021/4/15 15:30 请求参数
   * @return: javax.servlet.ServletOutputStream 响应参数
   */
  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (filterOutput == null) {
      filterOutput = new ServletOutputStream() {
        // 替换构造方法
        // 拿父类的response，初始化的时候，里面还没有数据，只有一些request信息和response信息,但是调用了创建outputStream
        private final TeeOutputStream teeOutputStream = new TeeOutputStream(
            RepeatableHttpServletResponseWrapper.super.getOutputStream(), output);

        @Override
        public boolean isReady() {
          return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        @Override
        public void write(int b) throws IOException {
          teeOutputStream.write(b);
        }
      };
    }
    return filterOutput;
  }

  public byte[] toByteArray() {
    return output.toByteArray();
  }

}
