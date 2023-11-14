package cn.ares.boot.base.log.util;

import cn.ares.boot.util.common.thread.ThreadLocalMapUtil;
import cn.ares.boot.util.log.BaseLoggerUtil;
import cn.ares.boot.util.log.util.SunReflectionUtil;
import org.springframework.boot.logging.DeferredLog;

/**
 * @author: Ares
 * @description: Deferred日志工具，使用静态方法打印Deferred日志，无需每个类中定义日志对象（Deferred
 * log方法名不正确因为异步启用后拿不到打印日志线程的堆栈信息）
 * @description: The Deferred logging tool uses a static method to print Deferred logs without
 * defining log objects in each class (the Deferred log method name is incorrect because the stack
 * information for printing log threads cannot be obtained after asynchronous is enabled).
 * @time: 2021-04-12 17:38:00
 * @version: JDK 1.8
 */
public class LoggerUtil extends BaseLoggerUtil {

  public static void errorDeferred(String msg) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.error(msg);
    }
  }

  public static void errorDeferred(String msg, Throwable e) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.error(msg, e);
    }
  }

  public static void warnDeferred(String msg) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.warn(msg);
    }
  }

  public static void infoDeferred(String msg) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.info(msg);
    }
  }

  public static void debugDeferred(String msg) {
    DeferredLog deferredLog = getDeferredLog();
    if (deferredLog.isErrorEnabled()) {
      deferredLog.debug(msg);
    }
  }

  public static DeferredLog getDeferredLog() {
    // Get the class that calls the error, info, debug static classes
    // 获取调用error、info、warn、debug静态类的类
    // 这里更极致的性能优化是把getDeferredLog和adaptiveGetCallerClass的逻辑直接放到到各方法中（一个方法节省一层堆栈），但是这样会导致代码可读性变差
    // A more extreme performance optimization here would be to put the getDeferredLog and adaptiveGetCallerClass logic directly into each method (saving one layer of stack per method), but this would result in less readable code
    String className = SunReflectionUtil.adaptiveGetCallerClass(INVOKE_DEPTH).getName();

    /*
     在SpringBoot加载的过程中 EnvironmentPostProcessor 的执行比较早; 这个时候日志系统根本就还没有初始化; 所以在此之前的日志操作都不会有效果; 使用
     DeferredLog 缓存日志；并在合适的时机回放日志
     EnvironmentPostProcessor was executed early during SpringBoot loading; At this point the logging system has not been initialized at all; So no logging operations before that will have any effect; use
     DeferredLog Indicates deferred log. And play back the log at the right time
     */
    DeferredLog deferredLog = ThreadLocalMapUtil.get(className);
    if (null == deferredLog) {
      deferredLog = new DeferredLog();
      ThreadLocalMapUtil.set(className, deferredLog);
    }
    return deferredLog;
  }

}
