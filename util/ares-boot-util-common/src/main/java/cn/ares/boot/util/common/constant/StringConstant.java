package cn.ares.boot.util.common.constant;

/**
 * @author: Ares
 * @time: 2021-12-26 17:27:08
 * @description: 字符串常量
 * @description: String constant
 * @version: JDK 1.8
 */
public interface StringConstant {

  String ZERO = "0";
  String ONE = "1";
  String TRUE = "true";
  String FALSE = "false";

  String EMPTY = "";
  String[] EMPTY_ARRAY = new String[0];

  String FORMAT_SPECIFIER = "%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";

  String FLOATING_POINT_NUMBER_FORMAT = "^[-\\+]?[.\\d]*$";

  /**
   * email format 邮箱格式
   */
  String EMAIL_FORMAT = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

  String UNKNOWN = "unknown";

  String DEFAULT = "default";

  String LINE_BREAK = "\n";

  String JAVA = "java";
  String SUN = "sun";
  String JAVA_FILE_SUFFIX = ".java";

  String MAGIC_NUMBER = "Ares";

  String HTTPS = "https";
  int HTTPS_DEFAULT_PORT = 443;
  int HTTP_DEFAULT_PORT = 80;

  String CLASS_SUFFIX = ".class";
  String JAR_SUFFIX = ".jar";

  String NULL = "null";

}
