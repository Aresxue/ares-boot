package cn.ares.boot.base.log.util;

import cn.ares.boot.base.log.annotation.LogPrintIgnore;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * @author: Ares
 * @time: 2024-08-26 19:22:21
 * @description: 忽略日志打印的修改器
 * @description: Modifier to ignore log printing
 * @version: JDK 1.8
 */
public class LogPrintIgnoreAnnotationIntrospector extends JacksonAnnotationIntrospector {

  private static final long serialVersionUID = 5442829376545079451L;

  @Override
  public boolean hasIgnoreMarker(AnnotatedMember annotatedMember) {
    LogPrintIgnore logPrintIgnore = _findAnnotation(annotatedMember, LogPrintIgnore.class);
    if (null != logPrintIgnore) {
      return logPrintIgnore.value();
    }
    return super.hasIgnoreMarker(annotatedMember);
  }

}
