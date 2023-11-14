package cn.ares.boot.util.common;


import static cn.ares.boot.util.common.constant.StringConstant.ZERO;
import static cn.ares.boot.util.common.constant.SymbolConstant.REGEX_SPOT;

import cn.ares.boot.util.common.primitive.IntegerUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author: Ares
 * @time: 2019-02-25 21:31:00
 * @description: 日期工具类
 * @description: Date util
 * @version: JDK 1.8
 */
public class DateUtil {

  /**
   * 存储格式的Map
   */
  private static final ThreadLocal<Map<String, SimpleDateFormat>> DATE_FORMAT_MAP = ThreadLocal.withInitial(
      HashMap::new);
  private static final int DATABASE_PRECISION = 6;

  public static final String DATE_FORMAT_WHIFFLETREE_SECOND = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_FORMAT_WHIFFLETREE_DAY = "yyyy-MM-dd";
  public static final String DATE_FORMAT_DIAGONAL_SECOND = "yyyy/MM/dd HH:mm:ss";
  public static final String DATE_FORMAT_TIMESTAMP = "yyyy/MM/dd HH:mm:ss.S";
  public static final String DATE_FORMAT_DIAGONAL_DAY = "yyyy/MM/dd";
  public static final String DATE_FORMAT_MINUTE = "yyyyMMddHHmm";
  public static final String DATE_FORMAT_SECOND = "yyyyMMddHHmmss";
  public static final String DATE_FORMAT_MILLIS = "yyyyMMddHHmmssSSS";
  public static final String DATE_FORMAT_DAY = "yyyyMMdd";
  public static final String DATE_FORMAT_MONTH = "yyyyMM";
  public static final String DATE_FORMAT_WHIFFLETREE_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
  public static final String DATE_FORMAT_WHIFFLETREE_MICRO = "yyyy-MM-dd HH:mm:ss.SSSSSS";
  public static final String DATE_FORMAT_T_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  public static final String DATE_FORMAT_ZONE_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'+0000'";
  public static final String DATE_FORMAT_ZONE_COLON_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'";
  public static final String DATE_FORMAT_TIME = "HH:mm:ss";
  public static final String DATE_FORMAT_TIME_MILLIS = "HH:mm:ss.SSS";
  public static final String DATE_FORMAT_TIME_MICRO = "HH:mm:ss.SSSSSS";

  public static final SimpleDateFormat DATE_FORMAT_WHIFFLETREE_SECOND_DATE_FORMAT = getFormat(
      DATE_FORMAT_WHIFFLETREE_SECOND);
  public static final SimpleDateFormat DATE_FORMAT_WHIFFLETREE_DAY_DATE_FORMAT = getFormat(
      DATE_FORMAT_WHIFFLETREE_DAY);
  public static final SimpleDateFormat DATE_FORMAT_TIME_DATE_FORMAT = getFormat(DATE_FORMAT_TIME);
  public static final SimpleDateFormat DATE_FORMAT_TIME_MILLIS_FORMAT = getFormat(
      DATE_FORMAT_TIME_MILLIS);
  public static final SimpleDateFormat DATE_FORMAT_WHIFFLETREE_MILLIS_DATE_FORMAT = getFormat(
      DATE_FORMAT_WHIFFLETREE_MILLIS);

  public static final DateTimeFormatter DATE_FORMAT_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      DATE_FORMAT_TIME);
  public static final DateTimeFormatter DATE_FORMAT_TIME_MICRO_FORMATTER = DateTimeFormatter.ofPattern(
      DATE_FORMAT_TIME_MICRO);
  public static final DateTimeFormatter DATE_FORMAT_WHIFFLETREE_SECOND_FORMATTER = DateTimeFormatter
      .ofPattern(DATE_FORMAT_WHIFFLETREE_SECOND);
  public static final DateTimeFormatter DATE_FORMAT_WHIFFLETREE_MICRO_FORMATTER = DateTimeFormatter
      .ofPattern(DATE_FORMAT_WHIFFLETREE_MICRO);
  public static final DateTimeFormatter DATE_FORMAT_WHIFFLETREE_DAY_FORMATTER = DateTimeFormatter.ofPattern(
      DATE_FORMAT_WHIFFLETREE_DAY);
  public static final DateTimeFormatter DATE_FORMAT_DAY_FORMATTER = DateTimeFormatter.ofPattern(
      DATE_FORMAT_DAY);
  public static final DateTimeFormatter DATE_FORMAT_WHIFFLETREE_MILLIS_FORMATTER = DateTimeFormatter.ofPattern(
      DATE_FORMAT_WHIFFLETREE_MILLIS);
  public static final DateTimeFormatter DATE_FORMAT_SECOND_FORMATTER = DateTimeFormatter.ofPattern(
      DATE_FORMAT_SECOND);
  public static final DateTimeFormatter DATE_FORMAT_MILLIS_FORMATTER = DateTimeFormatter.ofPattern(
      DATE_FORMAT_MILLIS);


  /**
   * @author: Ares
   * @description: 根据日期格式获取SimpleDateFormat
   * @description: Get SimpleDateFormat based on date format
   * @time: 2022-06-08 13:39:58
   * @params: [pattern] 日期格式
   * @return: java.text.SimpleDateFormat
   */
  public static SimpleDateFormat getFormat(final String pattern) {
    Map<String, SimpleDateFormat> dateFormatMap = DATE_FORMAT_MAP.get();
    SimpleDateFormat dateFormat = dateFormatMap.get(pattern);
    if (null == dateFormat) {
      if (null == pattern || pattern.length() == 0) {
        dateFormat = new SimpleDateFormat(DATE_FORMAT_ZONE_UTC);
        dateFormatMap.put(pattern, dateFormat);
      } else {
        dateFormat = new SimpleDateFormat(pattern);
        dateFormatMap.put(pattern, dateFormat);
      }
    }
    return dateFormat;
  }

  /**
   * @author: Ares
   * @description: 把日期按照格式转为字符串
   * @description: Convert date to string according to format
   * @time: 2022-06-08 13:40:35
   * @params: [date, pattern] 日期，格式
   * @return: java.lang.String 日期字符串
   */
  public static String format(Date date, String pattern) {
    return getFormat(pattern).format(date);
  }

  /**
   * @author: Ares
   * @description: 把日期字符串按照格式转为日期
   * @description: Convert date string to date according to format
   * @time: 2022-06-08 13:41:21
   * @params: [dateStr, pattern] 日期字符串，格式
   * @return: java.util.Date 日期
   */
  public static Date parse(String dateStr, String pattern) throws ParseException {
    return getFormat(pattern).parse(dateStr);
  }

  /**
   * @author: Ares
   * @description: 把日期字符串按照默认格式转为日期
   * @description: Convert date string to date in default format
   * @time: 2022-06-08 13:42:03
   * @params: [dateStr] 日期字符串
   * @return: java.util.Date 日期
   */
  public static Date parse(String dateStr) throws ParseException {
    return getFormat("").parse(dateStr);
  }

  /**
   * @author: Ares
   * @description: 获取一天后的日期字符串
   * @description: Get the date string one day later
   * @time: 2022-06-08 13:42:40
   * @params: []
   * @return: java.lang.String 日期字符串
   */
  public static String getNextDayStart() {
    long nowTime = System.currentTimeMillis();
    long nextDayStartTime =
        nowTime - (nowTime + TimeZone.getDefault().getRawOffset()) % (3600_000 * 24)
            + 3600_000 * 24;
    String nextDayStart = getFormat(DATE_FORMAT_WHIFFLETREE_SECOND)
        .format(new Date(nextDayStartTime));
    return nextDayStart;
  }

  /**
   * @author: Ares
   * @description: 获取今天最后的日期字符串
   * @description: Get the last date string of today
   * @time: 2022-06-08 13:43:30
   * @params: []
   * @return: java.lang.String 日期字符串
   */
  public static String getTodayEnd() {
    long nowTime = System.currentTimeMillis();
    long todayEndTime =
        nowTime - (nowTime + TimeZone.getDefault().getRawOffset()) % (3600_000 * 24)
            + 3600_000 * 24 - 1;
    String todayEnd = getFormat(DATE_FORMAT_WHIFFLETREE_SECOND).format(new Date(todayEndTime));
    return todayEnd;
  }

  /**
   * @author: Ares
   * @description: 获取今天最后的日期字符串
   * @description: Get the start date string of today
   * @time: 2022-06-08 13:44:36
   * @params: []
   * @return: java.lang.String 日期字符串
   */
  public static String getTodayStart() {
    long nowTime = System.currentTimeMillis();
    long todayStartTime =
        nowTime - (nowTime + TimeZone.getDefault().getRawOffset()) % (3600_000 * 24);
    String todayStart = getFormat(DATE_FORMAT_WHIFFLETREE_SECOND).format(new Date(todayStartTime));
    return todayStart;
  }

  /**
   * @author: Ares
   * @description: 获取指定偏移量天数的开始时间(明天偏移量为1今天偏移量为0昨天偏移量为 - 1)
   * @description: Get the start time of the specified offset days (tomorrow offset is 1, today
   * offset is 0, yesterday offset is - 1)
   * @time: 2022-09-30 12:02:26
   * @params: [offset] 日期偏移量
   * @return: java.time.LocalDateTime 开始时间
   */
  public static LocalDateTime getDayStart(int offset) {
    return LocalDateTime.of(LocalDate.now().plusDays(offset), LocalTime.of(0, 0, 0));
  }

  /**
   * @author: Ares
   * @description: 获取指定偏移量天数的结束时间(明天偏移量为1今天偏移量为0昨天偏移量为 - 1)
   * @description: Get the end time of the specified offset days (tomorrow offset is 1, today offset
   * is 0, yesterday offset is - 1)
   * @time: 2022-09-30 12:02:26
   * @params: [offset] 日期偏移量
   * @return: java.time.LocalDateTime 结束时间
   */
  public static LocalDateTime getDayEnd(int offset) {
    return LocalDateTime.of(LocalDate.now().plusDays(offset), LocalTime.of(23, 59, 59));
  }

  /**
   * @author: Ares
   * @description: 按默认时区日期转本地日期时间
   * @description: Convert LocalDateTime by default time zone date
   * @time: 2022-06-08 13:44:56
   * @params: [date] 日期
   * @return: java.time.LocalDateTime 本地日期
   */
  public static LocalDateTime dateToLocalDateTime(Date date) {
    return dateToLocalDateTime(date, ZoneId.systemDefault());
  }

  /**
   * @author: Ares
   * @description: 按指定时区日期转本地日期时间
   * @description: Convert LocalDateTime by specified time zone date
   * @time: 2022-06-08 13:44:56
   * @params: [date, zoneId] 日期，时区标识
   * @return: java.time.LocalDateTime 本地日期
   */
  public static LocalDateTime dateToLocalDateTime(Date date, ZoneId zoneId) {
    if (null == date) {
      return null;
    }
    Instant instant = date.toInstant();
    return instant.atZone(zoneId).toLocalDateTime();
  }

  /**
   * @author: Ares
   * @description: 按指定时区本地日期时间转日期
   * @description: Convert Date by specified time zone LocalDateTime
   * @time: 2022-06-08 13:47:38
   * @params: [localDateTime, zoneId] 本地日期，时区标识
   * @return: java.util.Date 日期
   */
  public static Date localDateTimeToDate(LocalDateTime localDateTime, ZoneId zoneId) {
    if (null == localDateTime) {
      return null;
    }
    ZonedDateTime zdt = localDateTime.atZone(zoneId);
    return Date.from(zdt.toInstant());
  }

  /**
   * @author: Ares
   * @description: 按默认时区本地日期时间转日期
   * @description: Convert Date by default time zone LocalDateTime
   * @time: 2022-06-08 13:47:38
   * @params: [localDateTime] 本地日
   * @return: java.util.Date 日期
   */
  public static Date localDateTimeToDate(LocalDateTime localDateTime) {
    return localDateTimeToDate(localDateTime, ZoneId.systemDefault());
  }

  /**
   * @author: Ares
   * @description: 把日期字符串转为时间戳(支持日期格式.后0到6位)
   * @description: Convert date string to timestamp (support date format. Last 0 to 6 digits)
   * @time: 2022-06-08 13:48:58
   * @params: [dateTime] 日期字符串
   * @return: long 时间戳
   */
  public static long getMicrosecond(String dateTime) {
    String[] dateTimes = dateTime.split(REGEX_SPOT);
    if (dateTimes.length > 1) {
      dateTime = dateTimes[0];
    }
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime,
        DateTimeFormatter.ofPattern(DATE_FORMAT_WHIFFLETREE_SECOND));
    long result =
        localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() * 1_000_000;
    if (dateTimes.length > 1) {
      int microsecond = Integer.parseInt(
          StringUtil.rightPadWithOver(dateTimes[1], DATABASE_PRECISION, ZERO));
      result += microsecond;
    }

    return result;
  }

  /**
   * @author: Ares
   * @description: 解析yyyy-MM-dd HH:mm:ss开头的所有时间
   * @description: Parse all times starting with yyyy-MM-dd HH:mm:ss
   * @time: 2022-05-05 11:17:31
   * @params: [dateTime] 日期字符串
   * @return: java.time.LocalDateTime 本地日期时间
   */
  public static LocalDateTime getDefaultLocalDateTime(String dateTime) {
    String[] dateTimes = dateTime.split(REGEX_SPOT);
    if (dateTimes.length > 1) {
      dateTime = dateTimes[0];
    }
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime,
        DATE_FORMAT_WHIFFLETREE_SECOND_FORMATTER);
    if (dateTimes.length > 1) {
      int microsecond = Integer.parseInt(
          StringUtil.rightPadWithOver(dateTimes[1], DATABASE_PRECISION, ZERO));
      localDateTime = localDateTime.plusNanos(microsecond * 1_000L);
    }

    return localDateTime;
  }

  /**
   * @author: Ares
   * @description: 解析HH:mm:ss开头的所有时间
   * @description: Parse all times starting with HH:mm:ss
   * @time: 2022-05-05 11:20:42
   * @params: [time] 时间字符串
   * @return: java.time.LocalTime 本地时间
   */
  public static LocalTime getDefaultLocalTime(String time) {
    String[] dateTimes = time.split(REGEX_SPOT);
    if (dateTimes.length > 1) {
      time = dateTimes[0];
    }
    LocalTime localTime = LocalTime.parse(time, DATE_FORMAT_TIME_FORMATTER);
    if (dateTimes.length > 1) {
      int microsecond = Integer.parseInt(
          StringUtil.rightPadWithOver(dateTimes[1], DATABASE_PRECISION, ZERO));
      localTime = localTime.plusNanos(microsecond * 1_000L);
    }

    return localTime;
  }


  /**
   * @author: Ares
   * @description: 时间戳转LocalDateTime（取默认时区）
   * @description: Convert timestamp to LocalDateTime(default zone is eight)
   * @time: 2022-08-31 11:19:45
   * @params: [timestamp] 时间戳
   * @return: java.time.LocalDateTime
   */
  public static LocalDateTime timestampToLocalDateTime(Long timestamp) {
    return timestampToLocalDateTime(timestamp, getDefaultTimeZoneOffset());
  }

  /**
   * @author: Ares
   * @description: 时间戳转LocalDateTime(指定时区偏移量)
   * @description: Convert timestamp to LocalDateTime with zoneOffset
   * @time: 2022-08-31 11:19:45
   * @params: [timestamp, zoneOffset] 时间戳, 时区偏移量
   * @return: java.time.LocalDateTime
   */
  public static LocalDateTime timestampToLocalDateTime(Long timestamp, int zoneOffset) {
    return LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.ofHours(zoneOffset));
  }

  /**
   * @author: Ares
   * @description: LocalDateTime转时间戳
   * @description: Convert LocalDateTime to timestamp
   * @time: 2022-08-31 11:19:45
   * @params: [localDateTime] LocalDateTime
   * @return: java.lang.Long 时间戳
   */
  public static Long localDateTimeToTimestamp(LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;
  }

  /**
   * @author: Ares
   * @description: LocalDateTime转毫秒级时间戳
   * @description: LocalDateTime to millisecond timestamp
   * @time: 2022-08-31 11:19:45
   * @params: [localDateTime] LocalDateTime
   * @return: java.lang.Long 时间戳
   */
  public static Long localDateTimeToMilliTimestamp(LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  /**
   * @author: Ares
   * @description: 获取默认时区
   * @description: Get default timeZone offset
   * @time: 2023-05-08 11:32:49
   * @params: []
   * @return: java.lang.Integer
   */
  public static Integer getDefaultTimeZoneOffset() {
    return IntegerUtil.parseInteger(
        Duration.ofMillis(TimeZone.getDefault().getRawOffset()).toHours());
  }

  /**
   * @author: Ares
   * @description: 毫秒级时间戳转LocalDateTime（默认时区）
   * @description: Millisecond timestamp to LocalDateTime (default time zone)
   * @time: 2023-05-08 11:33:25
   * @params: [timestamp] 时间戳
   * @return: java.time.LocalDateTime
   */
  public static LocalDateTime timestampToLocalDateTimeWithMilli(long timestamp) {
    return timestampToLocalDateTimeWithMilli(timestamp, getDefaultTimeZoneOffset());
  }

  /**
   * @author: Ares
   * @description: 毫秒级时间戳转LocalDateTime（以指定时区偏移）
   * @description: Millisecond timestamp to LocalDateTime (to specify time zone offset)
   * @time: 2023-05-08 11:33:25
   * @params: [timestamp] 时间戳
   * @return: java.time.LocalDateTime
   */
  public static LocalDateTime timestampToLocalDateTimeWithMilli(long timestamp, int zoneOffset) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.ofHours(zoneOffset));
  }

}
