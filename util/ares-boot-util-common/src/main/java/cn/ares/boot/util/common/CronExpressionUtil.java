package cn.ares.boot.util.common;

import cn.ares.boot.util.common.entity.CronExpression;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Ares
 * @time: 2023-06-21 13:34:54
 * @description: CronExpression util
 * @version: JDK 1.8
 */
public class CronExpressionUtil {

  /**
   * @author: Ares
   * @description: 查询最近指定次数的执行时间列表
   * @description: Query the execution time list of the latest specified times
   * @time: 2023-06-21 13:40:28
   * @params: [cornExpression, times] cron表达式，次数
   * @return: java.util.List<java.util.Date> 最近执行时间列表
   */
  public static List<Date> queryListLastRumDate(String cornExpression, int times)
      throws ParseException {
    List<Date> lastRumTimeList = new ArrayList<>(times);
    Date curTime = new Date();
    CronExpression expression = new CronExpression(cornExpression);
    for (int i = 0; i < times; i++) {
      Date newDate = expression.getNextValidTimeAfter(curTime);
      lastRumTimeList.add(newDate);
      curTime = newDate;
    }

    return lastRumTimeList;
  }

  /**
   * @author: Ares
   * @description: 查询最近指定次数的执行时间列表
   * @description: Query the execution time list of the latest specified times
   * @time: 2023-06-21 13:40:28
   * @params: [cornExpression, times] cron表达式，次数
   * @return: java.util.List<java.time.LocalDateTime> 最近执行时间列表
   */
  public static List<LocalDateTime> queryListLastRumTime(String cornExpression, int times)
      throws ParseException {
    return queryListLastRumDate(cornExpression, times).stream().map(DateUtil::dateToLocalDateTime)
        .collect(Collectors.toList());
  }

  /**
   * @author: Ares
   * @description: 查询最近指定次数的执行时间列表
   * @description: Query the execution time list of the latest specified times
   * @time: 2023-06-21 13:40:28
   * @params: [cornExpression, times, dateFormat] cron表达式，次数，日期格式
   * @return: java.util.List<String> 最近执行时间列表
   */
  public static List<String> queryListLastRumTime(String cornExpression, int times,
      String dateFormat) throws ParseException {
    return queryListLastRumDate(cornExpression, times).stream()
        .map(date -> DateUtil.getFormat(dateFormat).format(date)).collect(Collectors.toList());
  }

}
