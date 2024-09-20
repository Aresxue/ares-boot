package cn.ares.boot.util.spring;

import org.springframework.util.StopWatch;

/**
 * @author: Ares
 * @time: 2022-04-29 13:00:36
 * @description: 计时器装饰器
 * @description: StopWatch decorator
 * @version: JDK 1.8
 */
public class StopWatchDecorator extends StopWatch {

  private StopWatchDecorator(String id) {
    super(id);
  }

  public static StopWatchDecorator buildWithStart() {
    return buildWithStart("");
  }

  public static StopWatchDecorator buildWithStart(String id) {
    StopWatchDecorator stopWatchDecorator = new StopWatchDecorator(id);
    stopWatchDecorator.start();
    return stopWatchDecorator;
  }

  /**
   * @author: Ares
   * @description: 停止并获取从开始过去的总毫秒数（如果不是运行状态不会停止）
   * @description: Gets the number of milliseconds since the start(will not stop if it is not in running state)
   * @time: 2023-05-08 17:13:57
   * @return: long
   */
  public long stopAndGetTotalMillis() {
    if (super.isRunning()) {
      stop();
    }
    return getTotalTimeMillis();
  }

  /**
   * @author: Ares
   * @description: 停止并获取从开始过去的总秒数（如果不是运行状态不会停止）
   * @description: Gets the number of seconds since the start(will not stop if it is not in running state)
   * @time: 2023-05-08 17:13:57
   * @return: long
   */
  public double stopAndGetTotalSeconds() {
    if (super.isRunning()) {
      stop();
    }
    return getTotalTimeSeconds();
  }

  /**
   * @author: Ares
   * @description: 停止并获取从开始过去的总纳秒数（如果不是运行状态不会停止）
   * @description: Gets the number of nanoseconds since the start(will not stop if it is not in running state)
   * @time: 2023-05-08 17:13:57
   * @return: long
   */
  public long stopAndGetTotalNanos() {
    if (super.isRunning()) {
      stop();
    }
    return getTotalTimeNanos();
  }

  /**
   * @author: Ares
   * @description: 停止并获取上一次的纳秒数（如果不是运行状态不会停止）
   * @description: Stop and get the last nanosecond(will not stop if it is not in running state)
   * @time: 2023-05-11 12:48:15
   * @return: long 纳秒数
   */
  public long stopAndGetLastNanos() {
    if (super.isRunning()) {
      stop();
    }
    return getLastTaskTimeNanos();
  }

  /**
   * @author: Ares
   * @description: 停止并获取上一次的纳秒数（如果不是运行状态不会停止）
   * @description: Stop and get the last millisecond(will not stop if it is not in running state)
   * @time: 2023-05-11 12:48:15
   * @return: long 毫秒数
   */
  public long stopAndGetLastMillis() {
    if (super.isRunning()) {
      stop();
    }
    return getLastTaskTimeMillis();
  }

}
