package cn.ares.boot.base.log.config;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author: Ares
 * @time: 2024-08-26 20:25:02
 * @description: 日志打印配置
 * @description: Log print configuration
 * @version: JDK 1.8
 */
@Configuration
@Role(value = ROLE_INFRASTRUCTURE)
// TODO 支持动态刷新
public class LogPrintConfiguration {

  /**
   * 日志打印开关
   * Log print switch
   */
  @Value("${ares.log-print.enabled:true}")
  private boolean enabled;

  /**
   * 是否打印入参
   * Whether to print the input params
   */
  @Value("${ares.log-print.params.enabled:true}")
  private boolean paramsEnabled;

  /**
   * 是否打印结果
   * Whether to print the result
   */
  @Value("${ares.log-print.result.enabled:true}")
  private boolean resultEnabled;

  /**
   * 是否启用异步打印
   * Whether to enable asynchronous printing
   */
  @Value("${ares.log-print.async.enabled:false}")
  private boolean asyncEnabled;

  /**
   * 异步工作线程数
   * Asynchronous working thread number
   */
  @Value("${ares.log-print.async.worker-num:}")
  private Integer asyncWorkerNum;

  /**
   * 只在失败时打印日志
   * Print logs only when failed
   */
  @Value("${ares.log-print.only-error:false}")
  private boolean onlyError;

  /**
   * 打印日志的采样率（万分比），默认全采
   * Sampling rate of log printing (per ten thousand), default full sampling
   */
  @Value("${ares.log-print.sample-rate:10000}")
  private int sampleRate;

  /**
   * 打印日志长度的阈值，超过此长度不做打印（对性能的开销太大）
   * Print log length threshold, no printing if it exceeds this length (too much performance overhead)
   */
  @Value("${ares.log-print.threshold:8192}")
  private int threshold;

  // TODO 根据用户信息等信息使用白名单，现在暂不使用
  @Value("${ares.log-print.white-list:}")
  private Set<String> whiteList;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isParamsEnabled() {
    return paramsEnabled;
  }

  public void setParamsEnabled(boolean paramsEnabled) {
    this.paramsEnabled = paramsEnabled;
  }

  public boolean isResultEnabled() {
    return resultEnabled;
  }

  public void setResultEnabled(boolean resultEnabled) {
    this.resultEnabled = resultEnabled;
  }

  public boolean isAsyncEnabled() {
    return asyncEnabled;
  }

  public void setAsyncEnabled(boolean asyncEnabled) {
    this.asyncEnabled = asyncEnabled;
  }

  public Integer getAsyncWorkerNum() {
    return asyncWorkerNum;
  }

  public void setAsyncWorkerNum(Integer asyncWorkerNum) {
    this.asyncWorkerNum = asyncWorkerNum;
  }

  public boolean isOnlyError() {
    return onlyError;
  }

  public void setOnlyError(boolean onlyError) {
    this.onlyError = onlyError;
  }

  public int getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
  }

  public int getThreshold() {
    return threshold;
  }

  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }

  public Set<String> getWhiteList() {
    return whiteList;
  }

  public void setWhiteList(Set<String> whiteList) {
    this.whiteList = whiteList;
  }

}
