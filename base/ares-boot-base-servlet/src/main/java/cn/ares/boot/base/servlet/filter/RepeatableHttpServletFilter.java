package cn.ares.boot.base.servlet.filter;

import static cn.ares.boot.util.common.constant.StringConstant.TRUE;
import static cn.ares.boot.util.servlet.constant.ServletConstant.MATCH_ALL_URL;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.servlet.HttpServletUtil;
import cn.ares.boot.util.servlet.RepeatableHttpServletRequestWrapper;
import cn.ares.boot.util.servlet.RepeatableHttpServletResponseWrapper;
import java.io.IOException;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author: Ares
 * @time: 2022-12-21 12:40:30
 * @description: Filter for support repeatable HttpServletRequest and HttpServletResponse
 * @description: 支持可重复读的HttpServletRequest和HttpServletResponse的过滤器
 * @version: JDK 1.8
 */
@Component
@ConditionalOnClass(Filter.class)
@ConditionalOnProperty(name = "ares.servlet.repeatable-http-servlet.enabled", havingValue = TRUE, matchIfMissing = true)
@WebFilter(filterName = "repeatableHttpServletFilter", urlPatterns = {MATCH_ALL_URL})
@Order(-1_000)
@Role(value = ROLE_INFRASTRUCTURE)
public class RepeatableHttpServletFilter implements Filter {

  private static final String FILE_CONTENT_TYPES = "multipart/form-data,image/bmp,image/gif,image/jpeg,image/png,image/svg+xml,image/tiff,image/webp";
  public static final String FILE_CONTENT_TYPE_SET_EXPRESSION = "${ares.servlet.file.content-types:" + FILE_CONTENT_TYPES + "}";

  /**
   * File Content-Type set 文件文本类型不可重复集合
   */
  @Value(FILE_CONTENT_TYPE_SET_EXPRESSION)
  private Set<String> fileContentTypeSet;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    String contentType = HttpServletUtil.getContentType(servletRequest);
    boolean isFile = StringUtil.isNotBlank(contentType) && fileContentTypeSet.contains(contentType);
    // File stream is not processed
    // 文件流不做处理
    if (isFile) {
      filterChain.doFilter(servletRequest, servletResponse);
      return;
    }

    if (servletRequest instanceof HttpServletRequest
        && !(servletRequest instanceof RepeatableHttpServletRequestWrapper)) {
      servletRequest = new RepeatableHttpServletRequestWrapper((HttpServletRequest) servletRequest);
    }

    if (servletResponse instanceof HttpServletResponse
        && !(servletResponse instanceof RepeatableHttpServletResponseWrapper)) {
      servletResponse = new RepeatableHttpServletResponseWrapper(
          (HttpServletResponse) servletResponse);
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }

}
