package cn.ares.boot.util.common;


import static cn.ares.boot.util.common.constant.CommonConstant.CHINESE_CHARACTER_LENGTH;
import static cn.ares.boot.util.common.constant.CommonConstant.CHINESE_CHARACTER_START;
import static cn.ares.boot.util.common.constant.CommonConstant.FORMAT_PATTERN;
import static cn.ares.boot.util.common.constant.CommonConstant.NOT_ALPHABET_AND_NUMBER_PATTERN;
import static cn.ares.boot.util.common.constant.StringConstant.EMPTY;
import static cn.ares.boot.util.common.constant.SymbolConstant.MINUS;
import static cn.ares.boot.util.common.constant.SymbolConstant.MINUS_CHAR;
import static cn.ares.boot.util.common.constant.SymbolConstant.PLUS_CHAR;
import static cn.ares.boot.util.common.constant.SymbolConstant.SPACE;
import static cn.ares.boot.util.common.constant.SymbolConstant.SPACE_CHAR;
import static cn.ares.boot.util.common.constant.SymbolConstant.UNDERLINE;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: Ares
 * @time: 2021-05-13 10:14:00
 * @description: 为字符串提供额外的功能
 * @description: Provides extra functionality for Java String classes
 * @version: JDK 1.8
 */
public class StringUtil {

  /**
   * 找不到字符串时的下标 index when str not found
   */
  private static final int INDEX_NOT_FOUND = -1;
  /**
   * 左填充或右填充的限制 limit when leftPad or rightPad
   */
  private static final int PAD_LIMIT = 8192;

  /**
   * @author: Ares
   * @description: 判断字符串为空
   * @description: Determine str is empty
   * @time: 2022-05-19 10:48:05
   * @params: [str] 字符串
   * @return: boolean 是否为空
   */
  public static boolean isEmpty(final String str) {
    return null == str || str.isEmpty();
  }

  /**
   * @author: Ares
   * @description: 判断多个字符串全部为空
   * @description: Judging that multiple strings all are empty
   * @time: 2022-06-09 11:18:25
   * @params: [strArr] 字符串数组
   * @return: boolean 全部为空
   */
  public static boolean allIsEmpty(final String... strArr) {
    if (ArrayUtil.isEmpty(strArr)) {
      throw new IllegalArgumentException("Str arr is empty");
    }
    return Arrays.stream(strArr).allMatch(StringUtil::isEmpty);
  }

  /**
   * @author: Ares
   * @description: 判断多个字符串中是否存在为空的字符串
   * @description: Determines whether there are empty strings in multiple strings
   * @time: 2023-06-30 12:19:56
   * @params: [strArr] 字符串数组
   * @return: boolean 任一为空
   */
  public static boolean anyIsEmpty(final String... strArr) {
    if (ArrayUtil.isEmpty(strArr)) {
      throw new IllegalArgumentException("Str arr is empty");
    }
    return Arrays.stream(strArr).anyMatch(StringUtil::isEmpty);
  }

  /**
   * @author: Ares
   * @description: 判断字符串非空
   * @description: Determine str is not empty
   * @time: 2022-05-19 10:48:05
   * @params: [str] 字符串
   * @return: boolean 是否非空
   */
  public static boolean isNotEmpty(final String str) {
    return !isEmpty(str);
  }

  /**
   * @author: Ares
   * @description: 判断多个字符串全部非空
   * @description: Judging that multiple strings all are not empty
   * @time: 2022-06-09 11:18:25
   * @params: [strArr] 字符串数组
   * @return: boolean 全部非空
   */
  public static boolean allIsNotEmpty(final String... strArr) {
    if (ArrayUtil.isEmpty(strArr)) {
      throw new IllegalArgumentException("Str arr is empty");
    }
    return Arrays.stream(strArr).allMatch(StringUtil::isNotEmpty);
  }

  /**
   * @author: Ares
   * @description: 判断多个字符串中是否存在不为空的字符串
   * @description: Determines whether there is a non-empty string in multiple strings
   * @time: 2023-06-30 12:18:26
   * @params: [strArr] 字符串数组
   * @return: boolean 任一非空
   */
  public static boolean anyIsNotEmpty(final String... strArr) {
    if (ArrayUtil.isEmpty(strArr)) {
      throw new IllegalArgumentException("Str arr is empty");
    }
    return Arrays.stream(strArr).anyMatch(StringUtil::isNotEmpty);
  }

  /**
   * @author: Ares
   * @description: 判断字符串为空白（所以字符为空或空格）
   * @description: Determine str is blank(all chars ares empty or blank)
   * @time: 2022-05-19 10:48:05
   * @params: [str] 字符串
   * @return: boolean 是否为空白
   */
  public static boolean isBlank(final String str) {
    return null == str || str.trim().isEmpty();
  }

  /**
   * @author: Ares
   * @description: 判断字符串非空白（所以字符为空或空格）
   * @description: Determine str is not blank(all chars ares empty or blank)
   * @time: 2022-05-19 10:48:05
   * @params: [str] 字符串
   * @return: boolean 是否非空白
   */
  public static boolean isNotBlank(final String str) {
    return !isBlank(str);
  }

  /**
   * @author: Ares
   * @description: 判断对象为null或者空
   * @description: Determine obj is null or empty
   * @time: 2022-05-19 10:48:05
   * @params: [obj] 对象
   * @return: boolean 是否为空
   */
  public static boolean isEmpty(final Object obj) {
    return null == obj || isEmpty(obj.toString());
  }

  /**
   * @author: Ares
   * @description: 判断对象非null或者空
   * @description: Determine obj is not null or empty
   * @time: 2022-05-19 10:48:05
   * @params: [obj] 对象
   * @return: boolean 是否非空
   */
  public static boolean isNotEmpty(final Object obj) {
    return !isEmpty(obj);
  }

  /**
   * @author: Ares
   * @description: 将带下划线的字符串转大驼峰式
   * @description: Convert an underlined string to camelCase
   * @time: 2019-06-13 17:31:00
   * @params: [source] 源字符串
   * @return: java.lang.String 转换后字符串
   */
  public static String underlineToBigCamelCase(final String source) {
    if (isEmpty(source)) {
      return source;
    }
    final List<String> splitList = listSplit(source, UNDERLINE);
    final StringBuilder builder = new StringBuilder();
    for (String str : splitList) {
      builder.append(upperFirst(str.toLowerCase()));
    }
    return builder.toString();
  }

  /**
   * @author: Ares
   * @description: 将带中划线的字符串转大驼峰式
   * @description: Convert dashed string to camelCase
   * @time: 2019-03-24 13:34:00
   * @params: [source] 将字符串中的源字符串
   * @return: java.lang.String 转换后字符串
   */
  public static String strikeToBigCamelCase(final String source) {
    if (isEmpty(source)) {
      return source;
    }
    final List<String> splitList = listSplit(source, MINUS);
    final StringBuilder builder = new StringBuilder();
    for (String str : splitList) {
      builder.append(upperFirst(str.toLowerCase()));
    }
    return builder.toString();
  }


  /**
   * @author: Ares
   * @description: 将带中划线的字符串转小驼峰式
   * @description: Convert dashed string to camelCase
   * @time: 2019-06-13 17:31:00
   * @params: [source] 源字符串
   * @return: java.lang.String 转换后字符串
   */
  public static String strikeToLittleCamelCase(final String source) {
    return lowerFirst(strikeToBigCamelCase(source));
  }

  /**
   * @author: Ares
   * @description: 将字符串中的中划线转下划线
   * @description: Convert dashes to underscores in a string
   * @time: 2021-12-13 20:04:00
   * @params: [source] 源字符串
   * @return: java.lang.String 转换后字符串
   */
  public static String strikeToUnderline(final String source) {
    if (isEmpty(source)) {
      return source;
    }
    final List<String> splitList = listSplit(source, MINUS);
    final StringJoiner result = new StringJoiner(UNDERLINE);
    for (String str : splitList) {
      result.add(str);
    }
    return String.valueOf(result);
  }

  /**
   * @author: Ares
   * @description: 大写加下划线转小驼峰式
   * @description: Uppercase and underline to small camelCase
   * @time: 2022-08-23 09:33:30
   * @params: [source] 源字符串
   * @return: java.lang.String 转换后字符串
   */
  public static String underlineUpperToLittleCamelCase(final String source) {
    if (isEmpty(source)) {
      return source;
    }
    final List<String> splitList = listSplit(source, UNDERLINE);
    final StringBuilder builder = new StringBuilder();
    for (String s : splitList) {
      builder.append(upperFirst(s));
    }
    return builder.toString();
  }

  /**
   * @author: Ares
   * @description: 将字符串中的中划线转下划线并大写
   * @description: Convert underscores to underscores in a string and capitalize
   * @time: 2022-08-23 09:18:32
   * @params: [source] 源字符串
   * @return: java.lang.String 转换后字符串
   */
  public static String strikeToUnderlineUpper(final String source) {
    if (isEmpty(source)) {
      return source;
    }
    final  List<String> splitList = listSplit(source, MINUS);
    final  StringJoiner result = new StringJoiner(UNDERLINE);
    for (String str : splitList) {
      result.add(str.toUpperCase());
    }
    return String.valueOf(result);
  }

  /**
   * @author: Ares
   * @description: 将带下划线的字符串转小驼峰式
   * @description: Convert an underlined string to camelCase
   * @time: 2019-06-13 17:31:00
   * @params: [source] 源字符串
   * @return: java.lang.String 转换后字符串
   */
  public static String underlineToLittleCamelCase(final String source) {
    return lowerFirst(underlineToBigCamelCase(source));
  }

  /**
   * @author: Ares
   * @description: 首字母大写
   * @description: Capital letters
   * @time: 2019-06-13 17:31:00
   * @params: [source] 源字符串
   * @return: java.lang.String 转换后字符串
   */
  public static String upperFirst(final String source) {
    if (isEmpty(source)) {
      return source;
    }
    final char[] chars = source.toCharArray();
    chars[0] = 97 <= chars[0] && chars[0] <= 122 ? (char) (chars[0] - 32) : chars[0];
    return String.valueOf(chars);
  }

  /**
   * @author: Ares
   * @description: 首字母小写
   * @description: First letter lowercase
   * @time: 2019-06-13 17:33:00
   * @params: [source] 源字符串
   * @return: java.lang.String 转换后字符串
   */
  public static String lowerFirst(final String source) {
    if (isEmpty(source)) {
      return source;
    }
    final char[] chars = source.toCharArray();
    chars[0] = 65 <= chars[0] && chars[0] <= 90 ? (char) (chars[0] + 32) : chars[0];
    return String.valueOf(chars);
  }

  /**
   * @author: Ares
   * @description: 校验邮箱格式
   * @description: Check Email Format
   * @time: 2019-11-02 16:43:00
   * @params: [email] 邮箱
   * @return: boolean 是否符合邮箱格式
   */
  public static boolean validateEmailFormat(final String email) {
    if (isEmpty(email)) {
      return false;
    }
    final String regex = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    final Pattern pattern = Pattern.compile(regex);
    final Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  public static String trim(final String originStr) {
    return trim(originStr, " ");
  }


  /**
   * @author: Ares
   * @description: 去除原始字符串中开头和结尾的指定字符串(多个都会去除)
   * @description: Remove the specified string at the beginning and end of the original string (more
   * than one will be removed)
   * @time: 2020-09-28 14:46:00
   * @params: [originStr, trim] 源字符串，首尾要去除的空格
   * @return: java.lang.String 转换后字符串
   */
  public static String trim(final String originStr, final String trim) {
    if (isEmpty(originStr)) {
      return originStr;
    }
    final String result = trimStart(originStr, trim);
    return trimEnd(result, trim);
  }

  private static String trimStart(String str, final String trim) {
    if (null == str || isEmpty(trim)) {
      return str;
    }
    while (true) {
      if (str.startsWith(trim)) {
        str = str.substring(trim.length());
      } else {
        break;
      }
    }
    return str;
  }

  private static String trimEnd(String str, final String trim) {
    if (null == str || isEmpty(trim)) {
      return str;
    }
    while (true) {
      if (str.endsWith(trim)) {
        str = str.substring(0, str.length() - trim.length());
      } else {
        break;
      }
    }
    return str;
  }

  /**
   * @author: Ares
   * @description: 按照指定字符串分割源字符串
   * @description: Split the source string by the specified string
   * @time: 2021-05-21 17:04:00
   * @params: [str, delimiter, allowStrEmpty] 字符串，分隔符，空字符是否算一个字符串
   * @return: java.util.List<java.lang.String> 响应参数
   */
  public static List<String> listSplit(final String str, final String separator) {
    return listSplit(str, separator, true);
  }

  /**
   * @author: Ares
   * @description: 以指定分隔符分割字符串返回字符串数组
   * @description: Split by separator and return str array
   * @time: 2022-06-07 11:04:51
   * @params: [str, separator] 字符串，分隔符
   * @return: java.lang.String[] str array after split
   * @return: java.lang.String[] 分割后的字符串数组
   */
  public static String[] split(final String str, final String separator) {
    final List<String> listSplit = listSplit(str, separator);
    final String[] strArray = new String[listSplit.size()];
    listSplit.toArray(strArray);
    return strArray;
  }

  /**
   * @author: Ares
   * @description: 以指定分隔符分割字符串返回字符串列表（排除空字符串）
   * @description: Split by separator and return str list exclude empty str
   * @time: 2021-05-21 17:04:00
   * @params: [str, separator, allowStrEmpty] 字符串，分隔符，是否允许结果为空
   * @return: java.util.List<java.lang.String> str list after split
   * @return: java.util.List<java.lang.String> 分割后的字符串列表
   */
  public static List<String> listSplit(final String str, final String separator, final boolean allowStrEmpty) {
    if (str == null) {
      return null;
    } else {
      final int len = str.length();
      if (len == 0) {
        return Collections.emptyList();
      } else if (separator != null && !"".equals(separator)) {
        final int separatorLength = separator.length();
        final List<String> substrings = new ArrayList<>();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;

        while (end < len) {
          end = str.indexOf(separator, beg);
          if (end > -1) {
            boolean flag = allowStrEmpty ? end >= beg : end > beg;
            if (flag) {
              ++numberOfSubstrings;
              if (numberOfSubstrings == -1) {
                end = len;
                substrings.add(str.substring(beg));
              } else {
                substrings.add(str.substring(beg, end));
                beg = end + separatorLength;
              }
            } else {

              beg = end + separatorLength;
            }
          } else {
            substrings.add(str.substring(beg));
            end = len;
          }
        }

        return substrings;
      } else {
        {
          List<String> list = new ArrayList<>();
          int sizePlus1 = 1;
          int i = 0;
          int start = 0;
          boolean match = false;
          if (separator != null) {
            label:
            while (true) {
              while (true) {
                if (i >= len) {
                  break label;
                }

                match = true;
                ++i;
              }
            }
          } else {
            label:
            while (true) {
              while (true) {
                if (i >= len) {
                  break label;
                }

                if (Character.isWhitespace(str.charAt(i))) {
                  if (match) {
                    if (sizePlus1++ == -1) {
                      i = len;
                    }

                    list.add(str.substring(start, i));
                    match = false;
                  }

                  ++i;
                  start = i;
                } else {
                  match = true;
                  ++i;
                }
              }
            }
          }

          if (match) {
            list.add(str.substring(start, i));
          }

          return list;
        }
      }
    }
  }

  /**
   * @author: Ares
   * @description: 判断字符序列是否为数字
   * @description: Determine if a sequence of characters is a number
   * @time: 2021-06-07 16:51:00
   * @params: [sequence] 字符序列
   * @return: boolean 是否为数字
   */
  public static boolean isNumeric(final CharSequence sequence) {
    if (isEmpty(sequence)) {
      return false;
    } else {
      final int sequenceLength = sequence.length();

      for (int i = 0; i < sequenceLength; ++i) {
        if (!Character.isDigit(sequence.charAt(i))) {
          return false;
        }
      }

      return true;
    }
  }

  /**
   * @author: Ares
   * @description: 判断字符序列是否为空
   * @description: Determine if a sequence of characters is empty
   * @time: 2022-06-07 11:07:14
   * @params: [sequence] 字符序列
   * @return: boolean 是否为空
   */
  public static boolean isEmpty(final CharSequence sequence) {
    return sequence == null || sequence.length() == 0;
  }

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回字符串
   * @description: Returns a string when the parsed object is not empty
   * @time: 2022-06-07 11:08:00
   * @params: [object] 对象
   * @return: java.lang.String 解析结果
   */
  public static String parseString(final Object object) {
    if (null == object) {
      return null;
    }
    return object.toString();
  }

  /**
   * @author: Ares
   * @description: 解析对象不为空时返回字符串为空返回默认值
   * @description: When the parsing object is not empty, the return string is empty and the default
   * value is returned
   * @time: 2022-06-07 11:08:00
   * @params: [object, defaultValue] 对象，默认值
   * @return: java.lang.String 解析结果
   */
  public static String parseStringOrDefault(final Object object, final String defaultValue) {
    if (null == object) {
      return defaultValue;
    }
    return object.toString();
  }

  /**
   * @author: Ares
   * @description: 解析输入流为字符串
   * @description: Parse input stream as string
   * @time: 2022-06-07 11:09:48
   * @params: [inputStream] 输入流
   * @return: java.lang.String 解析结果
   */
  public static String parseSteamToString(final InputStream inputStream) {
    return parseSteamToString(inputStream, true);
  }

  /**
   * @author: Ares
   * @description: 解析输入流为字符串（可指定为空时是否抛出异常）
   * @description: Parse input stream as string(Can specify whether to throw an exception when
   * empty)
   * @time: 2023-12-28 15:07:35
   * @params: [inputStream, throwEx] 输入流，是否抛出异常
   * @return: java.lang.String 解析结果
   */
  public static String parseSteamToString(final InputStream inputStream, final boolean throwEx) {
    if (null == inputStream) {
      if (throwEx) {
        throw new IllegalArgumentException("InputStream is null");
      } else {
        return null;
      }
    }
    return new BufferedReader(new InputStreamReader(inputStream))
        .lines().parallel().collect(Collectors.joining(System.lineSeparator()));
  }

  /**
   * @author: Ares
   * @description: 以指定编码解析字符串为输入流
   * @description: Parse the input stream as a string with the specified encoding
   * @time: 2022-06-07 11:10:27
   * @params: [content, charset] 内容, 编码
   * @return: java.io.InputStream 输入流
   */
  public static InputStream parseStringToStream(final String content, final Charset charset) {
    return parseStringToStream(content, charset, true);
  }

  /**
   * @author: Ares
   * @description: 以指定编码解析字符串为输入流（可指定为空时是否抛出异常）
   * @description: Parse the input stream as a string with the specified encoding(Can specify
   * whether to throw an exception when empty)
   * @time: 2023-12-28 15:16:08
   * @params: [content, charset, throwEx] 内容，编码，是否抛出异常
   * @return: java.io.InputStream 输入流
   */
  public static InputStream parseStringToStream(final String content, final Charset charset, final boolean throwEx) {
    if (isEmpty(content)) {
      if (throwEx) {
        throw new IllegalArgumentException("Content is empty");
      } else {
        return null;
      }
    }
    return new ByteArrayInputStream(content.getBytes(charset));
  }

  /*
   * @author: Ares
   * @description: 以默认编码解析字符串为输入流
   * @description: Parse the input stream as a string with the default encoding
   * @time: 2022-06-07 11:11:35
   * @params: [content] 内容
   * @return: java.io.InputStream 输入流
   */
  public static InputStream parseStringToStream(final String content) {
    return parseStringToStream(content, Charset.defaultCharset());
  }

  /**
   * @author: Ares
   * @description: 生产指定长度的随机字符串
   * @description: Produce a random string of specified length
   * @time: 2022-06-07 11:12:05
   * @params: [count] 长度
   * @return: java.lang.String 随机字符串
   */
  public static String random(final int count) {
    return random(count, true, true);
  }

  /**
   * @author: Ares
   * @description: 生产指定长度的随机字符串
   * @description: Produce a random string of specified length
   * @time: 2022-06-07 11:14:01
   * @params: [count, letters, numbers] 长度，是否包含字母，是否包含数字
   * @return: java.lang.String 随机字符串
   */
  public static String random(final int count, final boolean letters, final boolean numbers) {
    return random(count, 0, 0, letters, numbers);
  }

  /**
   * @author: Ares
   * @description: 生产指定长度的随机字符串
   * @description: Produce a random string of specified length
   * @time: 2022-06-07 11:18:13
   * @params: [count, start, end, letters, numbers] 长度，起始字符，结束字符，是否包含字母，是否包含数字
   * @return: java.lang.String out 出参
   */
  public static String random(final int count, final int start, final int end, final boolean letters, final boolean numbers) {
    return random(count, start, end, letters, numbers, null);
  }

  /**
   * @author: Ares
   * @description: 生产指定长度的随机字符串
   * @description: Produce a random string of specified length
   * @time: 2022-06-07 11:19:20
   * @params: [count, start, end, letters, numbers, chars] 长度，起始字符，结束字符，是否包含字母，是否包含数字，指定字符数组
   * @return: java.lang.String 随机字符串
   */
  public static String random(int count, int start, int end, final boolean letters,
      final boolean numbers, final char[] chars) {
    if (count == 0) {
      return "";
    } else if (count < 0) {
      throw new IllegalArgumentException(
          "Requested random string length " + count + " is less than 0.");
    }
    if (chars != null && chars.length == 0) {
      throw new IllegalArgumentException("The chars array must not be empty");
    }

    if (start == 0 && end == 0) {
      if (chars != null) {
        end = chars.length;
      } else if (!letters && !numbers) {
        end = Character.MAX_CODE_POINT;
      } else {
        end = 'z' + 1;
        start = ' ';
      }
    } else if (end <= start) {
      throw new IllegalArgumentException(
          "Parameter end (" + end + ") must be greater than start (" + start + ")");
    }

    final int zeroDigitAscii = 48;
    final int firstLetterAscii = 65;

    if (chars == null && (numbers && end <= zeroDigitAscii
        || letters && end <= firstLetterAscii)) {
      throw new IllegalArgumentException(
          "Parameter end (" + end + ") must be greater then (" + zeroDigitAscii
              + ") for generating digits " +
              "or greater then (" + firstLetterAscii + ") for generating letters.");
    }

    final StringBuilder builder = new StringBuilder(count);
    final int gap = end - start;

    while (count-- != 0) {
      final int codePoint;
      if (chars == null) {
        codePoint = ThreadLocalRandom.current().nextInt(gap) + start;

        switch (Character.getType(codePoint)) {
          case Character.UNASSIGNED:
          case Character.PRIVATE_USE:
          case Character.SURROGATE:
            count++;
            continue;
          default:
        }

      } else {
        codePoint = chars[ThreadLocalRandom.current().nextInt(gap) + start];
      }

      final int numberOfChars = Character.charCount(codePoint);
      if (count == 0 && numberOfChars > 1) {
        count++;
        continue;
      }

      if (letters && Character.isLetter(codePoint) || numbers && Character.isDigit(codePoint)
          || !letters && !numbers) {
        builder.appendCodePoint(codePoint);

        if (numberOfChars == 2) {
          count--;
        }

      } else {
        count++;
      }
    }
    return builder.toString();
  }

  /**
   * @author: Ares
   * @description: 当值不为空时执行操作
   * @description: Perform action when value is not empty
   * @time: 2022-06-07 11:20:13
   * @params: [value, setter] 值, 操作
   * @return: void
   */
  public static void setIfPresent(final String value, final Consumer<String> setter) {
    if (isNotEmpty(value)) {
      setter.accept(value);
    }
  }

  /**
   * @author: Ares
   * @description: 使用空格左填充到指定长度
   * @description: Left pad with spaces to specified length
   * @time: 2022-06-07 11:21:51
   * @params: [str, size] 字符串，长度
   * @return: java.lang.String 填充后字符串
   */
  public static String leftPad(final String str, final int size) {
    return leftPad(str, size, ' ');
  }

  /**
   * @author: Ares
   * @description: 使用指定字符左填充到指定长度
   * @description: Left pad with pad char to specified length
   * @time: 2022-06-07 11:22:42
   * @params: [str, size, padChar] 字符串，长度，填充字符
   * @return: java.lang.String 填充后字符串
   */
  public static String leftPad(final String str, final int size, final char padChar) {
    if (str == null) {
      return null;
    }
    final int pads = size - str.length();
    if (pads <= 0) {
      return str;
    }
    if (pads > PAD_LIMIT) {
      return leftPad(str, size, String.valueOf(padChar));
    }
    return repeat(padChar, pads).concat(str);
  }

  /**
   * @author: Ares
   * @description: 使用指定字符串左填充到指定长度
   * @description: Left pad with pad str to specified length
   * @time: 2022-06-07 11:22:42
   * @params: [str, size, padStr] 字符串，长度，填充字符串
   * @return: java.lang.String 填充后字符串
   */
  public static String leftPad(final String str, final int size, String padStr) {
    if (str == null) {
      return null;
    }
    if (isEmpty(padStr)) {
      padStr = SPACE;
    }
    final int padLen = padStr.length();
    final int strLen = str.length();
    final int pads = size - strLen;
    if (pads <= 0) {
      // Returns original String when possible
      return str;
    }
    if (padLen == 1 && pads <= PAD_LIMIT) {
      return leftPad(str, size, padStr.charAt(0));
    }

    if (pads == padLen) {
      return padStr.concat(str);
    } else if (pads < padLen) {
      return padStr.substring(0, pads).concat(str);
    } else {
      final char[] padding = new char[pads];
      final char[] padChars = padStr.toCharArray();
      for (int i = 0; i < pads; i++) {
        padding[i] = padChars[i % padLen];
      }
      return new String(padding).concat(str);
    }
  }

  /**
   * @author: Ares
   * @description: 超过指定大小取前size位否则右补0
   * @description: If the size exceeds the specified size, take the first size bits, otherwise add 0
   * to the right
   * @time: 2022-05-05 11:12:27
   * @params: [str, size, padStr] 字符串，长度，填充字符串
   * @return: java.lang.String 填充后字符串
   */
  public static String rightPadWithOver(final String str, final int size, final String padStr) {
    if (str.length() > size) {
      return str.substring(0, size);
    }
    return rightPad(str, size, padStr);
  }

  /**
   * @author: Ares
   * @description: 使用指定字符串右填充到指定长度
   * @description: Right pad with pad str to specified length
   * @time: 2022-05-05 11:12:27
   * @params: [str, size, padStr] 字符串，长度，填充字符串
   * @return: java.lang.String 填充后字符串
   */
  public static String rightPad(final String str, final int size, String padStr) {
    if (str == null) {
      return null;
    }
    if (isEmpty(padStr)) {
      padStr = SPACE;
    }
    final int padLen = padStr.length();
    final int strLen = str.length();
    final int pads = size - strLen;
    if (pads <= 0) {
      // Returns original String when possible
      return str;
    }
    if (padLen == 1 && pads <= PAD_LIMIT) {
      return rightPad(str, size, padStr.charAt(0));
    }

    if (pads == padLen) {
      return str.concat(padStr);
    } else if (pads < padLen) {
      return str.concat(padStr.substring(0, pads));
    } else {
      final char[] padding = new char[pads];
      final char[] padChars = padStr.toCharArray();
      for (int i = 0; i < pads; i++) {
        padding[i] = padChars[i % padLen];
      }
      return str.concat(new String(padding));
    }
  }

  /**
   * @author: Ares
   * @description: 使用空格右填充到指定长度
   * @description: Right pad with spaces to specified length
   * @time: 2023-12-25 11:21:51
   * @params: [str, size] 字符串，长度
   * @return: java.lang.String 填充后字符串
   */
  public static String rightPad(final String str, final int size) {
    return rightPad(str, size, ' ');
  }

  /**
   * @author: Ares
   * @description: 使用指定字符右填充到指定长度
   * @description: Right pad with pad char to specified length
   * @time: 2022-05-05 11:12:27
   * @params: [str, size, padChar] 字符串，长度，填充字符
   * @return: java.lang.String 填充后字符串
   */
  public static String rightPad(final String str, final int size, final char padChar) {
    if (str == null) {
      return null;
    }
    final int pads = size - str.length();
    if (pads <= 0) {
      return str;
    }
    if (pads > PAD_LIMIT) {
      return rightPad(str, size, String.valueOf(padChar));
    }
    return str.concat(repeat(padChar, pads));
  }

  public static String repeat(final char ch, final int repeat) {
    if (repeat <= 0) {
      return EMPTY;
    }
    final char[] buf = new char[repeat];
    Arrays.fill(buf, ch);
    return new String(buf);
  }

  /**
   * @author: Ares
   * @description: 使用分割符连接多个字符串
   * @description: Concatenate multiple strArr using delimiter
   * @time: 2022-06-07 15:37:54
   * @params: [delimiter, strArr] 分割符，字符串
   * @return: java.lang.String 连接后字符串
   */
  public static String join(final char delimiter, final String... strArr) {
    if (strArr.length == 0) {
      return null;
    }
    if (strArr.length == 1) {
      return strArr[0];
    }
    int length = strArr.length - 1;
    for (final String str : strArr) {
      if (str == null) {
        continue;
      }
      length += str.length();
    }
    final StringBuilder builder = new StringBuilder(length);
    if (strArr[0] != null) {
      builder.append(strArr[0]);
    }
    for (int i = 1; i < strArr.length; ++i) {
      if (!isEmpty(strArr[i])) {
        builder.append(delimiter).append(strArr[i]);
      } else {
        builder.append(delimiter);
      }
    }
    return builder.toString();
  }


  /**
   * @author: Ares
   * @description: 使用分割符连接字符串列表
   * @description: Concatenate string list using delimiter
   * @time: 2024-06-19 21:39:14
   * @params: [delimiter, stringList] 分隔符，字符串列表
   * @return: java.lang.String 连接后字符串
   */
  public static String join(final String delimiter, final List<String> stringList) {
    if (CollectionUtil.isEmpty(stringList)) {
      return null;
    }
    if (stringList.size() == 1) {
      return stringList.get(0);
    }
    int length = stringList.size() - 1;
    for (final String str : stringList) {
      if (str == null) {
        continue;
      }
      length += str.length();
    }
    final StringBuilder builder = new StringBuilder(length);
    if (stringList.get(0) != null) {
      builder.append(stringList.get(0));
    }
    for (int i = 1; i < stringList.size(); ++i) {
      if (!isEmpty(stringList.get(i))) {
        builder.append(delimiter).append(stringList.get(i));
      } else {
        builder.append(delimiter);
      }
    }
    return builder.toString();
  }

  /**
   * @author: Ares
   * @description: 替换文本中检索字符串为指定字符串
   * @description: Replace the retrieved string in the text with the specified string
   * @time: 2022-06-07 15:39:37
   * @params: [text, searchString, replacement] 文本，检索字符串，替换字符串
   * @return: java.lang.String 替换后字符串
   */
  public static String replace(final String text, final String searchString,
      final String replacement) {
    return replace(text, searchString, replacement, -1);
  }

  /**
   * @author: Ares
   * @description: 替换文本中检索字符串为指定字符串（只替换指定个数）
   * @description: Replace the retrieved string in the text with the specified string(only the
   * specified number is replaced)
   * @time: 2022-06-07 15:40:52
   * @params: [text, searchString, replacement, max] 文本，检索字符串，替换字符串，最大替换个数
   * @return: java.lang.String 替换后字符串
   */
  public static String replace(final String text, final String searchString,
      final String replacement, final int max) {
    return replace(text, searchString, replacement, max, false);
  }

  /**
   * @author: Ares
   * @description: 替换文本中检索字符串(可忽略大小写)为指定字符串（只替换指定个数）
   * @description: Replace the retrieved string in the text(case can be ignored) with the specified
   * string(only the specified number is replaced)
   * @time: 2022-06-07 15:41:57
   * @params: [text, searchString, replacement, max, ignoreCase] 文本，检索字符串，替换字符串，最大替换个数，是否忽略大小写
   * @return: java.lang.String 替换后字符串
   */
  private static String replace(final String text, String searchString, final String replacement,
      int max, final boolean ignoreCase) {
    if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
      return text;
    }
    if (ignoreCase) {
      searchString = searchString.toLowerCase();
    }
    int start = 0;
    int end = ignoreCase ? indexOfIgnoreCase(text, searchString, start)
        : indexOf(text, searchString, start);
    if (end == INDEX_NOT_FOUND) {
      return text;
    }
    final int replLength = searchString.length();
    int increase = Math.max(replacement.length() - replLength, 0);
    increase *= max < 0 ? 16 : Math.min(max, 64);
    final StringBuilder buf = new StringBuilder(text.length() + increase);
    while (end != INDEX_NOT_FOUND) {
      buf.append(text, start, end).append(replacement);
      start = end + replLength;
      if (--max == 0) {
        break;
      }
      end = ignoreCase ? indexOfIgnoreCase(text, searchString, start)
          : indexOf(text, searchString, start);
    }
    buf.append(text, start, text.length());
    return buf.toString();
  }

  /**
   * @author: Ares
   * @description:
   * @time: 2024-08-14 14:56:10
   * @params: [text, searchList, replacementList, repeat, timeToLive] 文本，检索字符串数组，替换字符串数组
   * @return: java.lang.String 替换后字符串
   */
  public static String replace(final String text, final String[] searchList,
      final String[] replacementList, final boolean repeat, final int timeToLive) {
    if (timeToLive < 0) {
      final Set<String> searchSet = new HashSet<>(Arrays.asList(searchList));
      final Set<String> replacementSet = new HashSet<>(Arrays.asList(replacementList));
      searchSet.retainAll(replacementSet);
      if (!searchSet.isEmpty()) {
        throw new IllegalStateException("Aborting to protect against StackOverflowError - " +
            "output of one loop is the input of another");
      }
    }

    if (isEmpty(text) || searchList == null || searchList.length == 0 ||
        replacementList == null || replacementList.length == 0) {
      return text;
    }

    final int searchLength = searchList.length;
    final int replacementLength = replacementList.length;

    // make sure lengths are ok, these need to be equal
    if (searchLength != replacementLength) {
      throw new IllegalArgumentException("Search and Replace array lengths don't match: "
          + searchLength + " vs " + replacementLength);
    }

    boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

    int textIndex = -1;
    int replaceIndex = -1;
    int tempIndex = -1;

    for (int i = 0; i < searchLength; i++) {
      if (noMoreMatchesForReplIndex[i] || isEmpty(searchList[i]) || replacementList[i] == null) {
        continue;
      }
      tempIndex = text.indexOf(searchList[i]);

      if (tempIndex == -1) {
        noMoreMatchesForReplIndex[i] = true;
      } else if (textIndex == -1 || tempIndex < textIndex) {
        textIndex = tempIndex;
        replaceIndex = i;
      }
    }

    if (textIndex == -1) {
      return text;
    }

    int start = 0;
    int increase = 0;

    for (int i = 0; i < searchList.length; i++) {
      if (searchList[i] == null || replacementList[i] == null) {
        continue;
      }
      int greater = replacementList[i].length() - searchList[i].length();
      if (greater > 0) {
        increase += 3 * greater;
      }
    }
    increase = Math.min(increase, text.length() / 5);

    final StringBuilder buf = new StringBuilder(text.length() + increase);
    while (textIndex != -1) {

      for (int i = start; i < textIndex; i++) {
        buf.append(text.charAt(i));
      }
      buf.append(replacementList[replaceIndex]);

      start = textIndex + searchList[replaceIndex].length();

      textIndex = -1;
      replaceIndex = -1;
      for (int i = 0; i < searchLength; i++) {
        if (noMoreMatchesForReplIndex[i] || searchList[i] == null ||
            searchList[i].isEmpty() || replacementList[i] == null) {
          continue;
        }
        tempIndex = text.indexOf(searchList[i], start);

        // see if we need to keep searching for this
        if (tempIndex == -1) {
          noMoreMatchesForReplIndex[i] = true;
        } else if (textIndex == -1 || tempIndex < textIndex) {
          textIndex = tempIndex;
          replaceIndex = i;
        }
      }
    }

    final int textLength = text.length();
    for (int i = start; i < textLength; i++) {
      buf.append(text.charAt(i));
    }
    final String result = buf.toString();
    if (!repeat) {
      return result;
    }

    return replace(result, searchList, replacementList, repeat, timeToLive - 1);
  }

  /**
   * @author: Ares
   * @description: 在字符串中检索指定字符串（忽略大小写）
   * @description: Retrieves the specified string within a string (ignoring case)
   * @time: 2022-06-07 15:43:14
   * @params: [str, searchStr] 字符串，检索字符串
   * @return: int 下标
   */
  public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
    return indexOfIgnoreCase(str, searchStr, 0);
  }

  /**
   * @author: Ares
   * @description: 在字符串中从指定下标开始检索指定字符串（忽略大小写）
   * @description: Retrieves the specified string starting from the specified subscript in the
   * string (ignoring case)
   * @time: 2022-06-07 15:43:14
   * @params: [str, searchStr] 字符串，检索字符串，起始下标
   * @return: int 下标
   */
  public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr,
      int startPos) {
    if (str == null || searchStr == null) {
      return INDEX_NOT_FOUND;
    }
    if (startPos < 0) {
      startPos = 0;
    }
    final int endLimit = str.length() - searchStr.length() + 1;
    if (startPos > endLimit) {
      return INDEX_NOT_FOUND;
    }
    if (searchStr.length() == 0) {
      return startPos;
    }
    for (int i = startPos; i < endLimit; i++) {
      if (regionMatches(str, i, searchStr, searchStr.length())) {
        return i;
      }
    }
    return INDEX_NOT_FOUND;
  }

  /**
   * @author: Ares
   * @description: 在字符串中从指定下标开始检索指定字符串
   * @description: Retrieves the specified string starting from the specified subscript in the
   * string
   * @time: 2022-06-07 16:06:50
   * @params: [sequence, searchSequence, startPos] 字符串，检索字符串，起始下标
   * @return: int 下标
   */
  public static int indexOf(final CharSequence sequence, final CharSequence searchSequence,
      final int startPos) {
    if (sequence == null || searchSequence == null) {
      return INDEX_NOT_FOUND;
    }
    if (sequence instanceof String) {
      return ((String) sequence).indexOf(searchSequence.toString(), startPos);
    } else if (sequence instanceof StringBuilder) {
      return ((StringBuilder) sequence).indexOf(searchSequence.toString(), startPos);
    } else if (sequence instanceof StringBuffer) {
      return ((StringBuffer) sequence).indexOf(searchSequence.toString(), startPos);
    }
    return sequence.toString().indexOf(searchSequence.toString(), startPos);
  }

  private static boolean regionMatches(final CharSequence cs,
      final int thisStart, final CharSequence substring, final int length) {
    if (cs instanceof String && substring instanceof String) {
      return ((String) cs).regionMatches(true, thisStart, (String) substring, 0, length);
    }
    int index1 = thisStart;
    int index2 = 0;
    int tmpLen = length;

    final int srcLen = cs.length() - thisStart;
    final int otherLen = substring.length();

    if ((thisStart < 0) || (length < 0)) {
      return false;
    }

    if (srcLen < length || otherLen < length) {
      return false;
    }

    while (tmpLen-- > 0) {
      final char c1 = cs.charAt(index1++);
      final char c2 = substring.charAt(index2++);

      if (c1 == c2) {
        continue;
      }

      final char u1 = Character.toUpperCase(c1);
      final char u2 = Character.toUpperCase(c2);
      if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
        return false;
      }
    }

    return true;
  }

  /**
   * @author: Ares
   * @description: 是否包含占位符，例如%s，%d
   * @description: Whether to include placeholders, such as %s,%d
   * @time: 2022-06-07 16:12:56
   * @params: [str] 字符串
   * @return: boolean 是否包含占位符
   */
  public static boolean isFormat(final String str) {
    final Matcher matcher = FORMAT_PATTERN.matcher(str);
    return matcher.find();
  }

  /**
   * @author: Ares
   * @description: 校验字符串是否不全是字母和数字
   * @description: Check if the string is not all letters and numbers
   * @time: 2024-07-03 17:53:41
   * @params: [str] 字符串
   * @return: boolean 是否不全是字母和数字
   */
  public static boolean notAlphabetAndNumber(final String str) {
    final Matcher matcher = NOT_ALPHABET_AND_NUMBER_PATTERN.matcher(str);
    return matcher.find();
  }

  /**
   * @author: Ares
   * @description: Get string reader
   * @time: 2022-06-07 16:15:55
   * @params: [str] 字符串
   * @return: java.io.StringReader
   */
  public static StringReader getReader(final CharSequence str) {
    if (null == str) {
      return null;
    }
    return new StringReader(str.toString());
  }

  /**
   * @author: Ares
   * @description: 将字符序列转为字符串
   * @description: Convert character sequence to string
   * @time: 2022-06-07 16:16:26
   * @params: [cs] 字符序列
   * @return: java.lang.String 字符串
   */
  public static String str(final CharSequence cs) {
    return null == cs ? null : cs.toString();
  }

  /**
   * @author: Ares
   * @description: atoi
   * @time: 2022-06-07 16:16:58
   * @params: [str] 字符串
   * @return: int 转换后字符串
   */
  public static int atoi(final String str) {
    long result = 0;

    boolean start = false;
    boolean plusOrMinus = true;
    for (int i = 0; i < str.length(); i++) {
      final char currentChar = str.charAt(i);

      if (Character.isDigit(currentChar)) {
        final int numericValue = Character.getNumericValue(currentChar);
        if (plusOrMinus) {
          result = result * 10 + numericValue;
          if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
          }
        } else {
          result = result * 10 - numericValue;
          if (result < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
          }
        }

        start = true;
      } else if (start) {
        break;
      } else if (MINUS_CHAR == currentChar) {
        plusOrMinus = false;
        start = true;
      } else if (PLUS_CHAR == currentChar) {
        start = true;
      } else if (SPACE_CHAR == currentChar) {
      } else {
        break;
      }
    }
    return (int) result;
  }

  /**
   * @author: Ares
   * @description: 包含某段文本
   * @description: Contains text
   * @time: 2022-06-07 16:18:08
   * @params: [str] 被包含文本
   * @return: boolean 是否包含
   */
  public static boolean hasText(final String str) {
    return (str != null && !str.isEmpty() && containsText(str));
  }

  private static boolean containsText(final CharSequence str) {
    final int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * @author: Ares
   * @description: 字符序列包含指定字符序列
   * @description: The character sequence contains the specified character sequence
   * @time: 2022-06-07 16:27:01
   * @params: [charSequence, searchSeq] 字符序列，被包含字符序列
   * @return: boolean 是否包含
   */
  public static boolean contains(final CharSequence charSequence, final CharSequence searchSeq) {
    if (charSequence == null || searchSeq == null) {
      return false;
    }
    return indexOf(charSequence, searchSeq, 0) >= 0;
  }

  /**
   * @author: Ares
   * @description: 字符序列包含指定字符
   * @description: The character sequence contains the specified character
   * @time: 2022-06-07 16:29:41
   * @params: [charSequence, searchChar] 字符序列，被包含字符
   * @return: boolean 是否包含
   */
  public static boolean contains(final CharSequence charSequence, final int searchChar) {
    if (isEmpty(charSequence)) {
      return false;
    }
    return indexOf(charSequence, searchChar, 0) >= 0;
  }

  /**
   * @author: Ares
   * @description: 在字符串中从指定下标开始检索指定字符
   * @description: Retrieves the specified character starting from the specified subscript in the
   * string
   * @time: 2022-06-07 16:06:50
   * @params: [sequence, searchChar, startPos] 字符串，检索字符，起始下标
   * @return: int 下标
   */
  public static int indexOf(final CharSequence charSequence, final int searchChar, int start) {
    if (charSequence instanceof String) {
      return ((String) charSequence).indexOf(searchChar, start);
    }
    final int sequenceLength = charSequence.length();
    if (start < 0) {
      start = 0;
    }
    if (searchChar < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
      for (int i = start; i < sequenceLength; i++) {
        if (charSequence.charAt(i) == searchChar) {
          return i;
        }
      }
      return INDEX_NOT_FOUND;
    }
    if (searchChar <= Character.MAX_CODE_POINT) {
      final char[] chars = Character.toChars(searchChar);
      for (int i = start; i < sequenceLength - 1; i++) {
        final char high = charSequence.charAt(i);
        final char low = charSequence.charAt(i + 1);
        if (high == chars[0] && low == chars[1]) {
          return i;
        }
      }
    }
    return INDEX_NOT_FOUND;
  }

  /**
   * @author: Ares
   * @description: 随机获取一个汉字
   * @description: Get a random chinese character
   * @time: 2022-09-12 19:47:24
   * @params: []
   * @return: char random chinese character 随机汉字
   */
  public static char getRandomChineseCharacter() {
    return (char) (CHINESE_CHARACTER_START + ThreadLocalRandom.current()
        .nextInt(CHINESE_CHARACTER_LENGTH));
  }

  /**
   * @author: Ares
   * @description: 从字符串中提取起始字符串和结束字符串中间的字符串
   * @description: Gets the string between the start string and the end string from the string
   * @time: 2023-07-05 11:13:41
   * @params: [str, open, close] 字符串，起始字符串，结束字符串
   * @return: java.lang.String 结果
   */
  public static String substringBetween(final String str, final String open, final String close) {
    if (null == str || null == open || null == close) {
      return null;
    }
    final int start = str.indexOf(open);
    if (start != INDEX_NOT_FOUND) {
      int end = str.indexOf(close, start + open.length());
      if (end != INDEX_NOT_FOUND) {
        return str.substring(start + open.length(), end);
      }
    }
    return null;
  }

  /**
   * @author: Ares
   * @description: 从源字符串的第一个某个字符串后开始截取
   * @description: Intercept begins after the first string of the source string
   * @time: 2023-09-22 17:00:59
   * @params: [str, separator] 源字符串，分隔符
   * @return: java.lang.String 结果
   */
  public static String substringAfter(final String str, final String separator) {
    if (isEmpty(str)) {
      return str;
    }
    if (separator == null) {
      return EMPTY;
    }
    final int pos = str.indexOf(separator);
    if (pos == INDEX_NOT_FOUND) {
      return EMPTY;
    }
    return str.substring(pos + separator.length());
  }

  /**
   * @author: Ares
   * @description: 从头截取到源字符串的第一个某个字符串
   * @description: Cut from the beginning to the first string of the source string
   * @time: 2023-10-23 10:02:30
   * @params: [str, separator] 源字符串，分隔符
   * @return: java.lang.String 结果
   */
  public static String substringBefore(final String str, final int separator) {
    if (isEmpty(str)) {
      return str;
    }
    final int pos = str.indexOf(separator);
    if (pos == INDEX_NOT_FOUND) {
      return str;
    }
    return str.substring(0, pos);
  }

  /**
   * @author: Ares
   * @description: 从头截取到源字符串的最后一个某个字符串
   * @description: Cut from the beginning to the last string of the source string
   * @time: 2023-10-23 10:02:30
   * @params: [str, separator] 源字符串，分隔符
   * @return: java.lang.String 结果
   */
  public static String substringBeforeLast(final String str, final String separator) {
    if (isEmpty(str) || isEmpty(separator)) {
      return str;
    }
    final int pos = str.lastIndexOf(separator);
    if (pos == INDEX_NOT_FOUND) {
      return str;
    }
    return str.substring(0, pos);
  }

  /**
   * @author: Ares
   * @description: 从源字符串的最后一个某个字符串后向后截取
   * @description: Cut backwards from the last of a certain string in the source string
   * @time: 2023-10-23 10:02:30
   * @params: [str, separator] 源字符串，分隔符
   * @return: java.lang.String 结果
   */
  public static String substringAfterLast(final String str, final int separator) {
    if (isEmpty(str)) {
      return str;
    }
    final int pos = str.lastIndexOf(separator);
    if (pos == INDEX_NOT_FOUND || pos == str.length() - 1) {
      return EMPTY;
    }
    return str.substring(pos + 1);
  }

  /**
   * <p>Compares all Strings in an array and returns the initial sequence of
   * characters that is common to all of them.</p>
   *
   * <p>For example,
   * {@code getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) -&gt; "i am a "}</p>
   *
   * <pre>
   * StringUtils.getCommonPrefix(null) = ""
   * StringUtils.getCommonPrefix(new String[] {}) = ""
   * StringUtils.getCommonPrefix(new String[] {"abc"}) = "abc"
   * StringUtils.getCommonPrefix(new String[] {null, null}) = ""
   * StringUtils.getCommonPrefix(new String[] {"", ""}) = ""
   * StringUtils.getCommonPrefix(new String[] {"", null}) = ""
   * StringUtils.getCommonPrefix(new String[] {"abc", null, null}) = ""
   * StringUtils.getCommonPrefix(new String[] {null, null, "abc"}) = ""
   * StringUtils.getCommonPrefix(new String[] {"", "abc"}) = ""
   * StringUtils.getCommonPrefix(new String[] {"abc", ""}) = ""
   * StringUtils.getCommonPrefix(new String[] {"abc", "abc"}) = "abc"
   * StringUtils.getCommonPrefix(new String[] {"abc", "a"}) = "a"
   * StringUtils.getCommonPrefix(new String[] {"ab", "abxyz"}) = "ab"
   * StringUtils.getCommonPrefix(new String[] {"abcde", "abxyz"}) = "ab"
   * StringUtils.getCommonPrefix(new String[] {"abcde", "xyz"}) = ""
   * StringUtils.getCommonPrefix(new String[] {"xyz", "abcde"}) = ""
   * StringUtils.getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) = "i am a "
   * </pre>
   *
   * @param strArr array of String objects, entries may be null
   * @return the initial sequence of characters that are common to all Strings in the array; empty
   * String if the array is null, the elements are all null or if there is no common prefix.
   */
  public static String getCommonPrefix(final String... strArr) {
    if (ArrayUtil.isEmpty(strArr)) {
      return EMPTY;
    }
    final int smallestIndexOfDiff = indexOfDifference(strArr);
    if (smallestIndexOfDiff == INDEX_NOT_FOUND) {
      // all strings were identical
      if (strArr[0] == null) {
        return EMPTY;
      }
      return strArr[0];
    } else if (smallestIndexOfDiff == 0) {
      // there were no common initial characters
      return EMPTY;
    } else {
      // we found a common initial character sequence
      return strArr[0].substring(0, smallestIndexOfDiff);
    }
  }


  /**
   * <p>Compares all CharSequences in an array and returns the index at which the
   * CharSequences begin to differ.</p>
   *
   * <p>For example,
   * {@code indexOfDifference(new String[] {"i am a machine", "i am a robot"}) -> 7}</p>
   *
   * <pre>
   * StringUtils.indexOfDifference(null) = -1
   * StringUtils.indexOfDifference(new String[] {}) = -1
   * StringUtils.indexOfDifference(new String[] {"abc"}) = -1
   * StringUtils.indexOfDifference(new String[] {null, null}) = -1
   * StringUtils.indexOfDifference(new String[] {"", ""}) = -1
   * StringUtils.indexOfDifference(new String[] {"", null}) = 0
   * StringUtils.indexOfDifference(new String[] {"abc", null, null}) = 0
   * StringUtils.indexOfDifference(new String[] {null, null, "abc"}) = 0
   * StringUtils.indexOfDifference(new String[] {"", "abc"}) = 0
   * StringUtils.indexOfDifference(new String[] {"abc", ""}) = 0
   * StringUtils.indexOfDifference(new String[] {"abc", "abc"}) = -1
   * StringUtils.indexOfDifference(new String[] {"abc", "a"}) = 1
   * StringUtils.indexOfDifference(new String[] {"ab", "abxyz"}) = 2
   * StringUtils.indexOfDifference(new String[] {"abcde", "abxyz"}) = 2
   * StringUtils.indexOfDifference(new String[] {"abcde", "xyz"}) = 0
   * StringUtils.indexOfDifference(new String[] {"xyz", "abcde"}) = 0
   * StringUtils.indexOfDifference(new String[] {"i am a machine", "i am a robot"}) = 7
   * </pre>
   *
   * @param charSequences array of CharSequences, entries may be null
   * @return the index where the strings begin to differ; -1 if they are all equal
   */
  public static int indexOfDifference(final CharSequence... charSequences) {
    if (ArrayUtil.getLength(charSequences) <= 1) {
      return INDEX_NOT_FOUND;
    }
    boolean anyStringNull = false;
    boolean allStringsNull = true;
    final int arrayLen = charSequences.length;
    int shortestStrLen = Integer.MAX_VALUE;
    int longestStrLen = 0;

    // find the min and max string lengths; this avoids checking to make
    // sure we are not exceeding the length of the string each time through
    // the bottom loop.
    for (final CharSequence cs : charSequences) {
      if (cs == null) {
        anyStringNull = true;
        shortestStrLen = 0;
      } else {
        allStringsNull = false;
        shortestStrLen = Math.min(cs.length(), shortestStrLen);
        longestStrLen = Math.max(cs.length(), longestStrLen);
      }
    }

    // handle lists containing all nulls or all empty strings
    if (allStringsNull || longestStrLen == 0 && !anyStringNull) {
      return INDEX_NOT_FOUND;
    }

    // handle lists containing some nulls or some empty strings
    if (shortestStrLen == 0) {
      return 0;
    }

    // find the position with the first difference across all strings
    int firstDiff = -1;
    for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
      final char comparisonChar = charSequences[0].charAt(stringPos);
      for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
        if (charSequences[arrayPos].charAt(stringPos) != comparisonChar) {
          firstDiff = stringPos;
          break;
        }
      }
      if (firstDiff != -1) {
        break;
      }
    }

    if (firstDiff == -1 && shortestStrLen != longestStrLen) {
      // we compared all characters up to the length of the
      // shortest string and didn't find a match, but the string lengths
      // vary, so return the length of the shortest string.
      return shortestStrLen;
    }
    return firstDiff;
  }

  /**
   * @author: Ares
   * @description: 返回第一个非空值
   * @description: Returns the first non-null value
   * @time: 2024-01-29 10:20:47
   * @params: [values] 值数组
   * @return: java.util.Optional<T> 结果
   */
  @SafeVarargs
  public static <T> Optional<T> coalesce(final T... values) {
    if (ArrayUtil.isNotEmpty(values)) {
      for (T value : values) {
        if (null != value) {
          return Optional.of(value);
        }
      }
    }
    return Optional.empty();
  }

}
