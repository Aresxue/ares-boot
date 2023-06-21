package cn.ares.boot.util.common.entity;


import cn.ares.boot.util.common.CollectionUtil;
import cn.ares.boot.util.common.DateUtil;
import cn.ares.boot.util.common.MapUtil;
import cn.ares.boot.util.common.primitive.IntegerUtil;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * @author: Ares
 * @time: 2023-06-21 12:45:00
 * @description: Cron expression
 * @version: JDK 1.8
 */
public final class CronExpression {

  private static final int SECOND = 0;
  private static final int MINUTE = 1;
  private static final int HOUR = 2;
  private static final int DAY_OF_MONTH = 3;
  private static final int MONTH = 4;
  private static final int DAY_OF_WEEK = 5;
  private static final int YEAR = 6;
  /**
   * '*'
   */
  private static final int ALL_SPEC_INT = 99;
  /**
   * '?'
   */
  private static final int NO_SPEC_INT = 98;
  private static final Integer ALL_SPEC = ALL_SPEC_INT;
  private static final Integer NO_SPEC = NO_SPEC_INT;

  private static final Map<String, Integer> MONTH_MAP = MapUtil.newHashMap(12);
  private static final Map<String, Integer> DAY_MAP = MapUtil.newHashMap(31);

  static {
    MONTH_MAP.put("JAN", 0);
    MONTH_MAP.put("FEB", 1);
    MONTH_MAP.put("MAR", 2);
    MONTH_MAP.put("APR", 3);
    MONTH_MAP.put("MAY", 4);
    MONTH_MAP.put("JUN", 5);
    MONTH_MAP.put("JUL", 6);
    MONTH_MAP.put("AUG", 7);
    MONTH_MAP.put("SEP", 8);
    MONTH_MAP.put("OCT", 9);
    MONTH_MAP.put("NOV", 10);
    MONTH_MAP.put("DEC", 11);

    DAY_MAP.put("SUN", 1);
    DAY_MAP.put("MON", 2);
    DAY_MAP.put("TUE", 3);
    DAY_MAP.put("WED", 4);
    DAY_MAP.put("THU", 5);
    DAY_MAP.put("FRI", 6);
    DAY_MAP.put("SAT", 7);
  }

  private final String cronExpression;
  private TimeZone timeZone = null;
  private transient TreeSet<Integer> seconds;
  private transient TreeSet<Integer> minutes;
  private transient TreeSet<Integer> hours;
  private transient TreeSet<Integer> daysOfMonth;
  private transient TreeSet<Integer> months;
  private transient TreeSet<Integer> daysOfWeek;
  private transient TreeSet<Integer> years;

  private transient boolean lastDayOfWeek = false;
  private transient int nthDayOfWeek = 0;
  private transient boolean lastDayOfMonth = false;
  private transient boolean nearestWeekday = false;
  private transient int lastDayOffset = 0;

  public static final int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR) + 100;
  public static final Calendar MIN_CAL = Calendar.getInstance();

  static {
    MIN_CAL.set(1970, Calendar.JANUARY, 1);
  }

  public static final Date MIN_DATE = MIN_CAL.getTime();

  /**
   * Constructs a new <CODE>CronExpression</CODE> based on the specified parameter.
   *
   * @param cronExpression String representation of the cron expression the new object should
   *                       represent
   * @throws java.text.ParseException if the string expression cannot be parsed into a valid
   *                                  <CODE>CronExpression</CODE>
   */
  public CronExpression(final String cronExpression) throws ParseException {
    if (cronExpression == null) {
      throw new IllegalArgumentException("cronExpression cannot be null");
    }

    this.cronExpression = cronExpression.toUpperCase(Locale.US);

    buildExpression(this.cronExpression);
  }

  /**
   * Indicates whether the given date satisfies the cron expression. Note that milliseconds are
   * ignored, so two Dates falling on different milliseconds of the same second will always have the
   * same result here.
   *
   * @param date the date to evaluate
   * @return a boolean indicating whether the given date satisfies the cron expression
   */
  public boolean isSatisfiedBy(final Date date) {
    final Calendar testDateCal = Calendar.getInstance(getTimeZone());
    testDateCal.setTime(date);
    testDateCal.set(Calendar.MILLISECOND, 0);
    final Date originalDate = testDateCal.getTime();

    testDateCal.add(Calendar.SECOND, -1);

    final Date timeAfter = getTimeAfter(testDateCal.getTime());

    return ((timeAfter != null) && (timeAfter.equals(originalDate)));
  }

  /**
   * Returns the next date/time <I>after</I> the given date/time which satisfies the cron
   * expression.
   *
   * @param date the date/time at which to begin the search for the next valid date/time
   * @return the next valid date/time
   */
  public Date getNextValidTimeAfter(final Date date) {
    return getTimeAfter(date);
  }

  public LocalDateTime getNextValidTimeAfter(final LocalDateTime localDateTime) {
    Date date=  DateUtil.localDateTimeToDate(localDateTime);
    return DateUtil.dateToLocalDateTime(getTimeAfter(date));
  }

  /**
   * Returns the next date/time <I>after</I> the given date/time which does
   * <I>not</I> satisfy the expression
   *
   * @param date the date/time at which to begin the search for the next invalid date/time
   * @return the next valid date/time
   */
  public Date getNextInvalidTimeAfter(final Date date) {
    long difference = 1_000;

    //move back to the nearest second so differences will be accurate
    final Calendar adjustCal = Calendar.getInstance(getTimeZone());
    adjustCal.setTime(date);
    adjustCal.set(Calendar.MILLISECOND, 0);
    Date lastDate = adjustCal.getTime();

    Date newDate;
    //FUTURE_TODO: (QUARTZ-481) IMPROVE THIS! The following is a BAD solution to this problem. Performance will be very bad here, depending on the cron expression. It is, however A solution.
    //keep getting the next included time until it's farther than one second
    // apart. At that point, lastDate is the last valid fire time. We return
    // the second immediately following it.
    while (difference == 1_000) {
      newDate = getTimeAfter(lastDate);
      if (newDate == null) {
        break;
      }

      difference = newDate.getTime() - lastDate.getTime();

      if (difference == 1_000) {
        lastDate = newDate;
      }
    }

    return new Date(lastDate.getTime() + 1_000);
  }

  /**
   * Returns the time zone for which this <code>CronExpression</code> will be resolved.
   */
  public TimeZone getTimeZone() {
    if (timeZone == null) {
      timeZone = TimeZone.getDefault();
    }

    return timeZone;
  }

  /**
   * Sets the time zone for which  this <code>CronExpression</code> will be resolved.
   */
  public void setTimeZone(final TimeZone timeZone) {
    this.timeZone = timeZone;
  }

  /**
   * Returns the string representation of the <CODE>CronExpression</CODE>
   *
   * @return a string representation of the <CODE>CronExpression</CODE>
   */
  @Override
  public String toString() {
    return cronExpression;
  }

  /**
   * Indicates whether the specified cron expression can be parsed into a valid cron expression
   *
   * @param cronExpression the expression to evaluate
   * @return a boolean indicating whether the given expression is a valid cron expression
   */
  public static boolean isValidExpression(final String cronExpression) {

    try {
      new CronExpression(cronExpression);
    } catch (final ParseException pe) {
      return false;
    }

    return true;
  }

  public static void validateExpression(final String cronExpression) throws ParseException {

    new CronExpression(cronExpression);
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Expression Parsing Functions
  //
  private void buildExpression(final String expression) throws ParseException {
    try {
      if (seconds == null) {
        seconds = new TreeSet<>();
      }
      if (minutes == null) {
        minutes = new TreeSet<>();
      }
      if (hours == null) {
        hours = new TreeSet<>();
      }
      if (daysOfMonth == null) {
        daysOfMonth = new TreeSet<>();
      }
      if (months == null) {
        months = new TreeSet<>();
      }
      if (daysOfWeek == null) {
        daysOfWeek = new TreeSet<>();
      }
      if (years == null) {
        years = new TreeSet<>();
      }

      int exprOn = SECOND;

      StringTokenizer tokenizer = new StringTokenizer(expression, " \t", false);
      while (tokenizer.hasMoreTokens() && exprOn <= YEAR) {
        String expr = tokenizer.nextToken().trim();
        // throw an exception if L is used with other days of the month
        if (exprOn == DAY_OF_MONTH && expr.indexOf('L') != -1 && expr.length() > 1 && expr.contains(
            ",")) {
          throw new ParseException(
              "Support for specifying 'L' and 'LW' with other days of the month is not implemented",
              -1);
        }
        // throw an exception if L is used with other days of the week
        if (exprOn == DAY_OF_WEEK && expr.indexOf('L') != -1 && expr.length() > 1 && expr.contains(
            ",")) {
          throw new ParseException(
              "Support for specifying 'L' with other days of the week is not implemented", -1);
        }
        if (exprOn == DAY_OF_WEEK && expr.indexOf('#') != -1
            && expr.indexOf('#', expr.indexOf('#') + 1) != -1) {
          throw new ParseException(
              "Support for specifying multiple \"nth\" days is not implemented.", -1);
        }

        final StringTokenizer tempTokenizer = new StringTokenizer(expr, ",");
        while (tempTokenizer.hasMoreTokens()) {
          final String value = tempTokenizer.nextToken();
          storeExpressionVals(0, value, exprOn);
        }

        exprOn++;
      }

      if (exprOn <= DAY_OF_WEEK) {
        throw new ParseException("Unexpected end of expression.", expression.length());
      }

      if (exprOn <= YEAR) {
        storeExpressionVals(0, "*", YEAR);
      }

      final TreeSet<Integer> dayOfWeek = getSet(DAY_OF_WEEK);
      final TreeSet<Integer> dayOfMonth = getSet(DAY_OF_MONTH);
      // Copying the logic from the UnsupportedOperationException below
      final boolean dayOfMonthSpecial = !dayOfMonth.contains(NO_SPEC);
      final boolean dayOfWeekSpecial = !dayOfWeek.contains(NO_SPEC);

      if (!dayOfMonthSpecial || dayOfWeekSpecial) {
        if (!dayOfWeekSpecial || dayOfMonthSpecial) {
          throw new ParseException(
              "Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.",
              0);
        }
      }
    } catch (final ParseException pe) {
      throw pe;
    } catch (final Exception e) {
      throw new ParseException("Illegal cron expression format (" + e + ")", 0);
    }
  }

  private int storeExpressionVals(final int pos, final String str, final int type)
      throws ParseException {

    int incr = 0;
    int index = skipWhiteSpace(pos, str);
    if (index >= str.length()) {
      return index;
    }
    char c = str.charAt(index);
    if ((c >= 'A') && (c <= 'Z') && (!"L".equals(str)) && (!"LW".equals(str)) && (!str.matches(
        "^L-[0-9]*[W]?"))) {
      String sub = str.substring(index, index + 3);
      int strVal;
      int eval = -1;
      if (type == MONTH) {
        strVal = getMonthNumber(sub) + 1;
        if (strVal <= 0) {
          throw new ParseException("Invalid Month value: '" + sub + "'", index);
        }
        if (str.length() > index + 3) {
          c = str.charAt(index + 3);
          if (c == '-') {
            index += 4;
            sub = str.substring(index, index + 3);
            eval = getMonthNumber(sub) + 1;
            if (eval <= 0) {
              throw new ParseException("Invalid Month value: '" + sub + "'", index);
            }
          }
        }
      } else if (type == DAY_OF_WEEK) {
        strVal = getDayOfWeekNumber(sub);
        if (strVal < 0) {
          throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", index);
        }
        if (str.length() > index + 3) {
          c = str.charAt(index + 3);
          switch (c) {
            case '-':
              index += 4;
              sub = str.substring(index, index + 3);
              eval = getDayOfWeekNumber(sub);
              if (eval < 0) {
                throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", index);
              }
              break;
            case '#':
              try {
                index += 4;
                nthDayOfWeek = IntegerUtil.parseInt(str.substring(index));
                if (nthDayOfWeek < 1 || nthDayOfWeek > 5) {
                  throw new Exception();
                }
              } catch (final Exception e) {
                throw new ParseException(
                    "A numeric value between 1 and 5 must follow the '#' option", index);
              }
              break;
            case 'L':
              lastDayOfWeek = true;
              index++;
              break;
            default:
              break;
          }
        }

      } else {
        throw new ParseException("Illegal characters for this position: '" + sub + "'", index);
      }
      if (eval != -1) {
        incr = 1;
      }
      addToSet(strVal, eval, incr, type);
      return (index + 3);
    }

    switch (c) {
      case '?':
        index++;
        if ((index + 1) < str.length() && (str.charAt(index) != ' '
            && str.charAt(index + 1) != '\t')) {
          throw new ParseException("Illegal character after '?': " + str.charAt(index), index);
        }
        if (type != DAY_OF_WEEK && type != DAY_OF_MONTH) {
          throw new ParseException("'?' can only be spec fied for Day-of-Month or Day-of-Week.",
              index);
        }
        if (type == DAY_OF_WEEK && !lastDayOfMonth) {
          final int val = daysOfMonth.last();
          if (val == NO_SPEC_INT) {
            throw new ParseException("'?' can only be spec fied for Day-of-Month -OR- Day-of-Week.",
                index);
          }
        }
        addToSet(NO_SPEC_INT, -1, 0, type);
        return index;
      case '*':
      case '/':
        if (c == '*' && (index + 1) >= str.length()) {
          addToSet(ALL_SPEC_INT, -1, incr, type);
          return index + 1;
        } else if (c == '/' && ((index + 1) >= str.length() || str.charAt(index + 1) == ' '
            || str.charAt(index + 1) == '\t')) {
          throw new ParseException("'/' must be followed by an integer.", index);
        } else if (c == '*') {
          index++;
        }
        c = str.charAt(index);
        // is an increment specified?
        if (c == '/') {
          index++;
          if (index >= str.length()) {
            throw new ParseException("Unexpected end of string.", index);
          }

          incr = getNumericValue(str, index);

          index++;
          if (incr > 10) {
            index++;
          }
          if (incr > 59 && (type == SECOND || type == MINUTE)) {
            throw new ParseException("Increment > 60 : " + incr, index);
          } else if (incr > 23 && (type == HOUR)) {
            throw new ParseException("Increment > 24 : " + incr, index);
          } else if (incr > 31 && (type == DAY_OF_MONTH)) {
            throw new ParseException("Increment > 31 : " + incr, index);
          } else if (incr > 7 && (type == DAY_OF_WEEK)) {
            throw new ParseException("Increment > 7 : " + incr, index);
          } else if (incr > 12 && (type == MONTH)) {
            throw new ParseException("Increment > 12 : " + incr, index);
          }
        } else {
          incr = 1;
        }
        addToSet(ALL_SPEC_INT, -1, incr, type);
        return index;
      case 'L':
        index++;
        if (type == DAY_OF_MONTH) {
          lastDayOfMonth = true;
        }
        if (type == DAY_OF_WEEK) {
          addToSet(7, 7, 0, type);
        }
        if (type == DAY_OF_MONTH && str.length() > index) {
          c = str.charAt(index);
          if (c == '-') {
            final ValueSet valueSet = getValue(0, str, index + 1);
            lastDayOffset = valueSet.value;
            if (lastDayOffset > 30) {
              throw new ParseException("Offset from last day must be <= 30", index + 1);
            }
            index = valueSet.pos;
          }
          if (str.length() > index) {
            c = str.charAt(index);
            if (c == 'W') {
              nearestWeekday = true;
              index++;
            }
          }
        }
        return index;
      default:
        if (c >= '0' && c <= '9') {
          int val = Integer.parseInt(String.valueOf(c));
          index++;
          if (index >= str.length()) {
            addToSet(val, -1, -1, type);
          } else {
            c = str.charAt(index);
            if (c >= '0' && c <= '9') {
              final ValueSet valueSet = getValue(val, str, index);
              val = valueSet.value;
              index = valueSet.pos;
            }
            index = checkNext(index, str, val, type);
            return index;
          }
        } else {
          throw new ParseException("Unexpected character: " + c, index);
        }
        break;
    }

    return index;
  }

  private int checkNext(final int pos, final String str, final int val, final int type)
      throws ParseException {
    int end = -1;
    int index = pos;

    if (index >= str.length()) {
      addToSet(val, end, -1, type);
      return index;
    }

    char c = str.charAt(pos);

    if (c == 'L') {
      if (type == DAY_OF_WEEK) {
        if (val < 1 || val > 7) {
          throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
        }
        lastDayOfWeek = true;
      } else {
        throw new ParseException("'L' option is not valid here. (pos=" + index + ")", index);
      }
      final TreeSet<Integer> set = getSet(type);
      set.add(val);
      index++;
      return index;
    }

    if (c == 'W') {
      if (type == DAY_OF_MONTH) {
        nearestWeekday = true;
      } else {
        throw new ParseException("'W' option is not valid here. (pos=" + index + ")", index);
      }
      if (val > 31) {
        throw new ParseException(
            "The 'W' option does not make sense with values larger than 31 (max number of days in a month)",
            index);
      }
      final TreeSet<Integer> set = getSet(type);
      set.add(val);
      index++;
      return index;
    }

    switch (c) {
      case '#':
        if (type != DAY_OF_WEEK) {
          throw new ParseException("'#' option is not valid here. (pos=" + index + ")", index);
        }
        index++;
        try {
          nthDayOfWeek = Integer.parseInt(str.substring(index));
          if (nthDayOfWeek < 1 || nthDayOfWeek > 5) {
            throw new Exception();
          }
        } catch (final Exception e) {
          throw new ParseException("A numeric value between 1 and 5 must follow the '#' option",
              index);
        }
        final TreeSet<Integer> set = getSet(type);
        set.add(val);
        index++;
        return index;
      case '-':
        index++;
        c = str.charAt(index);
        final int value = Integer.parseInt(String.valueOf(c));
        end = value;
        index++;
        if (index >= str.length()) {
          addToSet(val, end, 1, type);
          return index;
        }
        c = str.charAt(index);
        if (c >= '0' && c <= '9') {
          final ValueSet valueSet = getValue(value, str, index);
          end = valueSet.value;
          index = valueSet.pos;
        }
        if (index < str.length() && ((c = str.charAt(index)) == '/')) {
          index++;
          c = str.charAt(index);
          final int value2 = Integer.parseInt(String.valueOf(c));
          index++;
          if (index >= str.length()) {
            addToSet(val, end, value2, type);
            return index;
          }
          c = str.charAt(index);
          if (c >= '0' && c <= '9') {
            final ValueSet valueSet = getValue(value2, str, index);
            final int value3 = valueSet.value;
            addToSet(val, end, value3, type);
            index = valueSet.pos;
          } else {
            addToSet(val, end, value2, type);
          }
          return index;
        } else {
          addToSet(val, end, 1, type);
          return index;
        }
      case '/':
        index++;
        c = str.charAt(index);
        final int v2 = Integer.parseInt(String.valueOf(c));
        index++;
        if (index >= str.length()) {
          addToSet(val, end, v2, type);
          return index;
        }
        c = str.charAt(index);
        if (c >= '0' && c <= '9') {
          final ValueSet valueSet = getValue(v2, str, index);
          final int value3 = valueSet.value;
          addToSet(val, end, value3, type);
          index = valueSet.pos;
          return index;
        } else {
          throw new ParseException("Unexpected character '" + c + "' after '/'", index);
        }
      default:
        break;
    }

    addToSet(val, end, 0, type);
    index++;
    return index;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public String getExpressionSummary() {
    return "seconds: "
        + getExpressionSetSummary(seconds)
        + "\n"
        + "minutes: "
        + getExpressionSetSummary(minutes)
        + "\n"
        + "hours: "
        + getExpressionSetSummary(hours)
        + "\n"
        + "daysOfMonth: "
        + getExpressionSetSummary(daysOfMonth)
        + "\n"
        + "months: "
        + getExpressionSetSummary(months)
        + "\n"
        + "daysOfWeek: "
        + getExpressionSetSummary(daysOfWeek)
        + "\n"
        + "lastDayOfWeek: "
        + lastDayOfWeek
        + "\n"
        + "nearestWeekday: "
        + nearestWeekday
        + "\n"
        + "nthDayOfWeek: "
        + nthDayOfWeek
        + "\n"
        + "lastDayOfMonth: "
        + lastDayOfMonth
        + "\n"
        + "years: "
        + getExpressionSetSummary(years)
        + "\n";
  }

  private String getExpressionSetSummary(final Set<Integer> set) {
    if (set.contains(NO_SPEC)) {
      return "?";
    }
    if (set.contains(ALL_SPEC)) {
      return "*";
    }

    final StringBuilder buf = new StringBuilder();

    final Iterator<Integer> iterator = set.iterator();
    boolean first = true;
    while (iterator.hasNext()) {
      final Integer iVal = iterator.next();
      final String val = iVal.toString();
      if (!first) {
        buf.append(",");
      }
      buf.append(val);
      first = false;
    }

    return buf.toString();
  }

  private int skipWhiteSpace(int i, final String s) {
    for (; i < s.length() && (s.charAt(i) == ' ' || s.charAt(i) == '\t'); i++) {
      // empty
    }

    return i;
  }

  private int findNextWhiteSpace(int i, final String s) {
    for (; i < s.length() && (s.charAt(i) != ' ' || s.charAt(i) != '\t'); i++) {
      // empty
    }

    return i;
  }

  private void addToSet(final int val, final int end, int incr, final int type)
      throws ParseException {
    final TreeSet<Integer> set = getSet(type);

    switch (type) {
      case SECOND:
      case MINUTE:
        if ((val < 0 || val > 59 || end > 59) && (val != ALL_SPEC_INT)) {
          throw new ParseException("Minute and Second values must be between 0 and 59", -1);
        }
        break;
      case HOUR:
        if ((val < 0 || val > 23 || end > 23) && (val != ALL_SPEC_INT)) {
          throw new ParseException("Hour values must be between 0 and 23", -1);
        }
        break;
      case DAY_OF_MONTH:
        if ((val < 1 || val > 31 || end > 31) && (val != ALL_SPEC_INT) && (val != NO_SPEC_INT)) {
          throw new ParseException("Day of month values must be between 1 and 31", -1);
        }
        break;
      case MONTH:
        if ((val < 1 || val > 12 || end > 12) && (val != ALL_SPEC_INT)) {
          throw new ParseException("Month values must be between 1 and 12", -1);
        }
        break;
      case DAY_OF_WEEK:
        if ((val == 0 || val > 7 || end > 7) && (val != ALL_SPEC_INT) && (val != NO_SPEC_INT)) {
          throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
        }
        break;
      default:
        break;
    }

    if ((incr == 0 || incr == -1) && val != ALL_SPEC_INT) {
      if (val != -1) {
        set.add(val);
      } else {
        set.add(NO_SPEC);
      }

      return;
    }

    int startAt = val;
    int stopAt = end;

    if (val == ALL_SPEC_INT && incr <= 0) {
      incr = 1;
      // put in a marker, but also fill values
      set.add(ALL_SPEC);
    }

    switch (type) {
      case SECOND:
      case MINUTE:
        if (stopAt == -1) {
          stopAt = 59;
        }
        if (startAt == -1 || startAt == ALL_SPEC_INT) {
          startAt = 0;
        }
        break;
      case HOUR:
        if (stopAt == -1) {
          stopAt = 23;
        }
        if (startAt == -1 || startAt == ALL_SPEC_INT) {
          startAt = 0;
        }
        break;
      case DAY_OF_MONTH:
        if (stopAt == -1) {
          stopAt = 31;
        }
        if (startAt == -1 || startAt == ALL_SPEC_INT) {
          startAt = 1;
        }
        break;
      case MONTH:
        if (stopAt == -1) {
          stopAt = 12;
        }
        if (startAt == -1 || startAt == ALL_SPEC_INT) {
          startAt = 1;
        }
        break;
      case DAY_OF_WEEK:
        if (stopAt == -1) {
          stopAt = 7;
        }
        if (startAt == -1 || startAt == ALL_SPEC_INT) {
          startAt = 1;
        }
        break;
      case YEAR:
        if (stopAt == -1) {
          stopAt = MAX_YEAR;
        }
        if (startAt == -1 || startAt == ALL_SPEC_INT) {
          startAt = 1970;
        }
        break;
      default:
        break;
    }

    // if the end of the range is before the start, then we need to overflow into
    // the next day, month etc. This is done by adding the maximum amount for that
    // type, and using modulus max to determine the value being added.
    int max = -1;
    if (stopAt < startAt) {
      switch (type) {
        case SECOND:
          max = 60;
          break;
        case MINUTE:
          max = 60;
          break;
        case HOUR:
          max = 24;
          break;
        case MONTH:
          max = 12;
          break;
        case DAY_OF_WEEK:
          max = 7;
          break;
        case DAY_OF_MONTH:
          max = 31;
          break;
        case YEAR:
          throw new IllegalArgumentException("Start year must be less than stop year");
        default:
          throw new IllegalArgumentException("Unexpected type encountered");
      }
      stopAt += max;
    }

    for (int i = startAt; i <= stopAt; i += incr) {
      if (max == -1) {
        // ie: there's no max to overflow over
        set.add(i);
      } else {
        // take the modulus to get the real value
        int i2 = i % max;

        // 1-indexed ranges should not include 0, and should include their max
        if (i2 == 0 && (type == MONTH || type == DAY_OF_WEEK || type == DAY_OF_MONTH)) {
          i2 = max;
        }

        set.add(i2);
      }
    }
  }

  TreeSet<Integer> getSet(final int type) {
    switch (type) {
      case SECOND:
        return seconds;
      case MINUTE:
        return minutes;
      case HOUR:
        return hours;
      case DAY_OF_MONTH:
        return daysOfMonth;
      case MONTH:
        return months;
      case DAY_OF_WEEK:
        return daysOfWeek;
      case YEAR:
        return years;
      default:
        return null;
    }
  }

  private ValueSet getValue(final int value, final String str, int index) {
    char c = str.charAt(index);
    final StringBuilder s1 = new StringBuilder(String.valueOf(value));
    while (c >= '0' && c <= '9') {
      s1.append(c);
      index++;
      if (index >= str.length()) {
        break;
      }
      c = str.charAt(index);
    }
    final ValueSet val = new ValueSet();
    val.pos = (index < str.length()) ? index : index + 1;
    val.value = IntegerUtil.parseInt(s1.toString());
    return val;
  }

  private int getNumericValue(final String s, final int i) {
    final int endOfVal = findNextWhiteSpace(i, s);
    final String val = s.substring(i, endOfVal);
    return IntegerUtil.parseInt(val);
  }

  private int getMonthNumber(final String s) {
    final Integer integer = MONTH_MAP.get(s);
    if (integer == null) {
      return -1;
    }
    return integer;
  }

  private int getDayOfWeekNumber(final String s) {
    final Integer integer = DAY_MAP.get(s);
    if (integer == null) {
      return -1;
    }
    return integer;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Computation Functions
  //
  ////////////////////////////////////////////////////////////////////////////
  public Date getTimeAfter(Date afterTime) {

    // Computation is based on Gregorian year only.
    final Calendar calendar = new GregorianCalendar(getTimeZone());

    // move ahead one second, since we're computing the time *after* the
    // given time
    afterTime = new Date(afterTime.getTime() + 1000);
    // CronTrigger does not deal with milliseconds
    calendar.setTime(afterTime);
    calendar.set(Calendar.MILLISECOND, 0);

    boolean gotOne = false;
    // loop until we've computed the next time, or we've past the endTime
    while (!gotOne) {
      //if (endTime != null && calendar.getTime().after(endTime)) return null;
      if (calendar.get(Calendar.YEAR) > 2999) { // prevent endless loop...
        return null;
      }

      int second = calendar.get(Calendar.SECOND);
      int minute = calendar.get(Calendar.MINUTE);

      // get second.................................................
      SortedSet<Integer> sortedSet = seconds.tailSet(second);
      if (CollectionUtil.isNotEmpty(sortedSet)) {
        second = sortedSet.first();
      } else {
        second = seconds.first();
        minute++;
        calendar.set(Calendar.MINUTE, minute);
      }
      calendar.set(Calendar.SECOND, second);

      minute = calendar.get(Calendar.MINUTE);
      int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
      int time = -1;

      // get minute.................................................
      sortedSet = minutes.tailSet(minute);
      if (CollectionUtil.isNotEmpty(sortedSet)) {
        time = minute;
        minute = sortedSet.first();
      } else {
        minute = minutes.first();
        hourOfDay++;
      }
      if (minute != time) {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, minute);
        setCalendarHour(calendar, hourOfDay);
        continue;
      }
      calendar.set(Calendar.MINUTE, minute);

      hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
      int day = calendar.get(Calendar.DAY_OF_MONTH);
      time = -1;

      // get hour...................................................
      sortedSet = hours.tailSet(hourOfDay);
      if (CollectionUtil.isNotEmpty(sortedSet)) {
        time = hourOfDay;
        hourOfDay = sortedSet.first();
      } else {
        hourOfDay = hours.first();
        day++;
      }
      if (hourOfDay != time) {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        setCalendarHour(calendar, hourOfDay);
        continue;
      }
      calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

      day = calendar.get(Calendar.DAY_OF_MONTH);
      int month = calendar.get(Calendar.MONTH) + 1;
      // '+ 1' because calendar is 0-based for this field, and we are
      // 1-based
      time = -1;
      int tmon = month;

      // get day...................................................
      final boolean dayOfMonthSpecial = !daysOfMonth.contains(NO_SPEC);
      final boolean dayOfWeekSpecial = !daysOfWeek.contains(NO_SPEC);
      // get day by day of month rule
      if (dayOfMonthSpecial && !dayOfWeekSpecial) {
        sortedSet = daysOfMonth.tailSet(day);
        if (lastDayOfMonth) {
          if (!nearestWeekday) {
            time = day;
            day = getLastDayOfMonth(month, calendar.get(Calendar.YEAR));
            day -= lastDayOffset;
            if (time > day) {
              month++;
              if (month > 12) {
                month = 1;
                // ensure test of month != tmon further below fails
                tmon = 3333;
                calendar.add(Calendar.YEAR, 1);
              }
              day = 1;
            }
          } else {
            time = day;
            day = getLastDayOfMonth(month, calendar.get(Calendar.YEAR));
            day -= lastDayOffset;

            final Calendar tcal = Calendar.getInstance(getTimeZone());
            tcal.set(Calendar.SECOND, 0);
            tcal.set(Calendar.MINUTE, 0);
            tcal.set(Calendar.HOUR_OF_DAY, 0);
            tcal.set(Calendar.DAY_OF_MONTH, day);
            tcal.set(Calendar.MONTH, month - 1);
            tcal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));

            final int ldom = getLastDayOfMonth(month, calendar.get(Calendar.YEAR));
            final int dow = tcal.get(Calendar.DAY_OF_WEEK);

            if (dow == Calendar.SATURDAY && day == 1) {
              day += 2;
            } else if (dow == Calendar.SATURDAY) {
              day -= 1;
            } else if (dow == Calendar.SUNDAY && day == ldom) {
              day -= 2;
            } else if (dow == Calendar.SUNDAY) {
              day += 1;
            }

            tcal.set(Calendar.SECOND, second);
            tcal.set(Calendar.MINUTE, minute);
            tcal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            tcal.set(Calendar.DAY_OF_MONTH, day);
            tcal.set(Calendar.MONTH, month - 1);
            final Date nTime = tcal.getTime();
            if (nTime.before(afterTime)) {
              day = 1;
              month++;
            }
          }
        } else if (nearestWeekday) {
          time = day;
          day = daysOfMonth.first();

          final Calendar tcal = Calendar.getInstance(getTimeZone());
          tcal.set(Calendar.SECOND, 0);
          tcal.set(Calendar.MINUTE, 0);
          tcal.set(Calendar.HOUR_OF_DAY, 0);
          tcal.set(Calendar.DAY_OF_MONTH, day);
          tcal.set(Calendar.MONTH, month - 1);
          tcal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));

          final int ldom = getLastDayOfMonth(month, calendar.get(Calendar.YEAR));
          final int dow = tcal.get(Calendar.DAY_OF_WEEK);

          if (dow == Calendar.SATURDAY && day == 1) {
            day += 2;
          } else if (dow == Calendar.SATURDAY) {
            day -= 1;
          } else if (dow == Calendar.SUNDAY && day == ldom) {
            day -= 2;
          } else if (dow == Calendar.SUNDAY) {
            day += 1;
          }

          tcal.set(Calendar.SECOND, second);
          tcal.set(Calendar.MINUTE, minute);
          tcal.set(Calendar.HOUR_OF_DAY, hourOfDay);
          tcal.set(Calendar.DAY_OF_MONTH, day);
          tcal.set(Calendar.MONTH, month - 1);
          final Date nTime = tcal.getTime();
          if (nTime.before(afterTime)) {
            day = daysOfMonth.first();
            month++;
          }
        } else if (CollectionUtil.isNotEmpty(sortedSet)) {
          time = day;
          day = sortedSet.first();
          // make sure we don'ime over-run a short month, such as february
          final int lastDay = getLastDayOfMonth(month, calendar.get(Calendar.YEAR));
          if (day > lastDay) {
            day = daysOfMonth.first();
            month++;
          }
        } else {
          day = daysOfMonth.first();
          month++;
        }

        if (day != time || month != tmon) {
          calendar.set(Calendar.SECOND, 0);
          calendar.set(Calendar.MINUTE, 0);
          calendar.set(Calendar.HOUR_OF_DAY, 0);
          calendar.set(Calendar.DAY_OF_MONTH, day);
          calendar.set(Calendar.MONTH, month - 1);
          // '- 1' because calendar is 0-based for this field, and we
          // are 1-based
          continue;
        }
      } else if (dayOfWeekSpecial && !dayOfMonthSpecial) {
        // get day by day of week rule
        // are we looking for the last XXX day of
        if (lastDayOfWeek) {
          // the month?
          // desired
          final int dow = daysOfWeek.first();
          final int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
          int daysToAdd = 0;
          if (currentDayOfWeek < dow) {
            daysToAdd = dow - currentDayOfWeek;
          }
          if (currentDayOfWeek > dow) {
            daysToAdd = dow + (7 - currentDayOfWeek);
          }

          final int lDay = getLastDayOfMonth(month, calendar.get(Calendar.YEAR));

          if (day + daysToAdd > lDay) {
            // did we already miss the
            // last one?
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, month);
            // no '- 1' here because we are promoting the month
            continue;
          }

          // find date of last occurrence of this day in this month...
          while ((day + daysToAdd + 7) <= lDay) {
            daysToAdd += 7;
          }

          day += daysToAdd;

          if (daysToAdd > 0) {
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month - 1);
            // '- 1' here because we are not promoting the month
            continue;
          }

        } else if (nthDayOfWeek != 0) {
          // are we looking for the Nth XXX day in the month?
          // desired
          final int dow = daysOfWeek.first();
          final int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // current d-o-w
          int daysToAdd = 0;
          if (currentDayOfWeek < dow) {
            daysToAdd = dow - currentDayOfWeek;
          } else if (currentDayOfWeek > dow) {
            daysToAdd = dow + (7 - currentDayOfWeek);
          }

          boolean dayShifted = daysToAdd > 0;

          day += daysToAdd;
          int weekOfMonth = day / 7;
          if (day % 7 > 0) {
            weekOfMonth++;
          }

          daysToAdd = (nthDayOfWeek - weekOfMonth) * 7;
          day += daysToAdd;
          if (daysToAdd < 0 || day > getLastDayOfMonth(month, calendar.get(Calendar.YEAR))) {
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, month);
            // no '- 1' here because we are promoting the month
            continue;
          } else if (daysToAdd > 0 || dayShifted) {
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month - 1);
            // '- 1' here because we are NOT promoting the month
            continue;
          }
        } else {
          final int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
          // desired
          int dow = daysOfWeek.first();
          // d-o-w
          sortedSet = daysOfWeek.tailSet(currentDayOfWeek);
          if (CollectionUtil.isNotEmpty(sortedSet)) {
            dow = sortedSet.first();
          }

          int daysToAdd = 0;
          if (currentDayOfWeek < dow) {
            daysToAdd = dow - currentDayOfWeek;
          }
          if (currentDayOfWeek > dow) {
            daysToAdd = dow + (7 - currentDayOfWeek);
          }

          int lastDayOfMonth = getLastDayOfMonth(month, calendar.get(Calendar.YEAR));

          if (day + daysToAdd > lastDayOfMonth) { // will we pass the end of
            // the month?
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, month);
            // no '- 1' here because we are promoting the month
            continue;
          } else if (daysToAdd > 0) { // are we switching days?
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, day + daysToAdd);
            calendar.set(Calendar.MONTH, month - 1);
            // '- 1' because calendar is 0-based for this field,
            // and we are 1-based
            continue;
          }
        }
      } else {
        // dayOfWeekSpecial && !dayOfMonthSpecial
        throw new UnsupportedOperationException(
            "Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
      }
      calendar.set(Calendar.DAY_OF_MONTH, day);

      month = calendar.get(Calendar.MONTH) + 1;
      // '+ 1' because calendar is 0-based for this field, and we are
      // 1-based
      int year = calendar.get(Calendar.YEAR);
      time = -1;

      // test for expressions that never generate a valid fire date,
      // but keep looping...
      if (year > MAX_YEAR) {
        return null;
      }

      // get month...................................................
      sortedSet = months.tailSet(month);
      if (CollectionUtil.isNotEmpty(sortedSet)) {
        time = month;
        month = sortedSet.first();
      } else {
        month = months.first();
        year++;
      }
      if (month != time) {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month - 1);
        // '- 1' because calendar is 0-based for this field, and we are
        // 1-based
        calendar.set(Calendar.YEAR, year);
        continue;
      }
      calendar.set(Calendar.MONTH, month - 1);
      // '- 1' because calendar is 0-based for this field, and we are
      // 1-based

      year = calendar.get(Calendar.YEAR);

      // get year...................................................
      sortedSet = years.tailSet(year);
      if (CollectionUtil.isNotEmpty(sortedSet)) {
        time = year;
        year = sortedSet.first();
      } else {
        return null; // ran out of years...
      }

      if (year != time) {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        // '- 1' because calendar is 0-based for this field, and we are
        // 1-based
        calendar.set(Calendar.YEAR, year);
        continue;
      }
      calendar.set(Calendar.YEAR, year);

      gotOne = true;
    }
    // while( !done )
    return calendar.getTime();
  }

  /**
   * Advance the calendar to the particular hour paying particular attention to daylight saving
   * problems.
   *
   * @param cal  the calendar to operate on
   * @param hour the hour to set
   */
  private void setCalendarHour(final Calendar cal, final int hour) {
    cal.set(Calendar.HOUR_OF_DAY, hour);
    if (cal.get(Calendar.HOUR_OF_DAY) != hour && hour != 24) {
      cal.set(Calendar.HOUR_OF_DAY, hour + 1);
    }
  }

  private Date getTimeBefore(final Date targetDate) {
    final Calendar calendar = Calendar.getInstance(getTimeZone());

    // CronTrigger does not deal with milliseconds, so truncate target
    calendar.setTime(targetDate);
    calendar.set(Calendar.MILLISECOND, 0);
    final Date targetDateNoMs = calendar.getTime();

    // to match this
    Date start = targetDateNoMs;
    final long minIncrement = findMinIncrement();
    Date prevFireTime;
    do {
      final Date prevCheckDate = new Date(start.getTime() - minIncrement);
      prevFireTime = getTimeAfter(prevCheckDate);
      if (prevFireTime == null || prevFireTime.before(MIN_DATE)) {
        return null;
      }
      start = prevCheckDate;
    } while (prevFireTime.compareTo(targetDateNoMs) >= 0);
    return prevFireTime;
  }

  public Date getPrevFireTime(final Date targetDate) {
    return getTimeBefore(targetDate);
  }

  private long findMinIncrement() {
    if (seconds.size() != 1) {
      return minInSet(seconds) * 1_000L;
    } else if (seconds.first() == ALL_SPEC_INT) {
      return 1_000;
    }
    if (minutes.size() != 1) {
      return minInSet(minutes) * 60_000L;
    } else if (minutes.first() == ALL_SPEC_INT) {
      return 60_000;
    }
    if (hours.size() != 1) {
      return minInSet(hours) * 3_600_000L;
    } else if (hours.first() == ALL_SPEC_INT) {
      return 3_600_000;
    }
    return 86_400_000;
  }

  private int minInSet(final TreeSet<Integer> set) {
    int previous = 0;
    int min = Integer.MAX_VALUE;
    boolean first = true;
    for (final int value : set) {
      if (first) {
        previous = value;
        first = false;
      } else {
        final int diff = value - previous;
        if (diff < min) {
          min = diff;
        }
      }
    }
    return min;
  }

  /**
   * NOT YET IMPLEMENTED: Returns the final time that the
   * <code>CronExpression</code> will match.
   */
  public Date getFinalFireTime() {
    // FUTURE_TODO: implement QUARTZ-423
    return null;
  }

  private boolean isLeapYear(final int year) {
    return ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0));
  }

  private int getLastDayOfMonth(final int monthNum, final int year) {
    switch (monthNum) {
      case 1:
        return 31;
      case 2:
        return (isLeapYear(year)) ? 29 : 28;
      case 3:
        return 31;
      case 4:
        return 30;
      case 5:
        return 31;
      case 6:
        return 30;
      case 7:
        return 31;
      case 8:
        return 31;
      case 9:
        return 30;
      case 10:
        return 31;
      case 11:
        return 30;
      case 12:
        return 31;
      default:
        throw new IllegalArgumentException("Illegal month number: " + monthNum);
    }
  }


  private static class ValueSet {

    public int value;

    public int pos;
  }


}
