package cn.ares.boot.util.common;

import static cn.ares.boot.util.common.DateUtil.DATE_FORMAT_WHIFFLETREE_SECOND;
import static cn.ares.boot.util.common.DateUtil.DATE_FORMAT_WHIFFLETREE_SECOND_FORMATTER;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: Ares
 * @time: 2023-06-21 13:41:57
 * @description: CronExpressionUtil test
 * @version: JDK 1.8
 */
public class CronExpressionUtilTest {

  public static void main(String[] args) throws ParseException {
    List<String> list = CronExpressionUtil.queryListLastRumTime("*/5 * * * * ?", 5,
        DATE_FORMAT_WHIFFLETREE_SECOND);
    System.out.println("每隔5秒执行一次: " + list);

    list = CronExpressionUtil.queryListLastRumTime("0 */1 * * * ?", 5,
        DATE_FORMAT_WHIFFLETREE_SECOND);
    System.out.println("每隔1分钟执行一次: " + list);

    list = CronExpressionUtil.queryListLastRumTime("0 0 2 1 * ? *", 5,
        DATE_FORMAT_WHIFFLETREE_SECOND);
    System.out.println("每月1日的凌晨2点执行一次: " + list);

    list = CronExpressionUtil.queryListLastRumTime("0 0 1 ? * L", 5,
        DATE_FORMAT_WHIFFLETREE_SECOND);
    System.out.println("每周星期天凌晨1点执行一次: " + list);

    list = CronExpressionUtil.queryListLastRumTime("0 0 12 ? * WED", 5,
        DATE_FORMAT_WHIFFLETREE_SECOND);
    System.out.println("每个星期三中午12点执行一次: " + list);

    for (LocalDateTime time : CronExpressionUtil.queryListLastRumTime("0 0 12 ? * WED", 5)) {
      System.out.println(DATE_FORMAT_WHIFFLETREE_SECOND_FORMATTER.format(time));
    }
  }

}
