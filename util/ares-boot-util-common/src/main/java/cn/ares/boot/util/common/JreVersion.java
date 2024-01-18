package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import cn.ares.boot.util.common.primitive.IntegerUtil;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2024-01-18 16:57:52
 * @description: Jre version
 * @version: JDK 1.8
 */
public enum JreVersion {
  /*
  detail
   */
  JAVA_8,

  JAVA_9,

  JAVA_10,

  JAVA_11,

  JAVA_12,

  JAVA_13,

  JAVA_14,

  JAVA_15,

  JAVA_16,

  JAVA_17,

  JAVA_18,

  JAVA_19,

  JAVA_20,

  JAVA_21,

  JAVA_22,

  JAVA_23,

  OTHER;


  private static final Logger LOGGER = JdkLoggerUtil.getLogger(JreVersion.class);

  private static final JreVersion JRE_VERSION = getJreVersion();

  /**
   * get current JRE version
   *
   * @return JRE version
   */
  public static JreVersion currentVersion() {
    return JRE_VERSION;
  }

  /**
   * is current version
   *
   * @return true if current version
   */
  public boolean isCurrentVersion() {
    return this == JRE_VERSION;
  }

  private static JreVersion getJreVersion() {
    // get java version from system property
    String version = System.getProperty("java.version");
    boolean isBlank = StringUtil.isBlank(version);
    if (isBlank) {
      LOGGER.warning("java.version is blank");
    }
    // if start with 1.8 return java 8
    if (!isBlank && version.startsWith("1.8")) {
      return JAVA_8;
    }
    // if JRE version is 9 or above, we can get version from java.lang.Runtime.version()
    try {
      Method method = Runtime.class.getDeclaredMethod("version");
      Object javaRunTimeVersion = method.invoke(Runtime.getRuntime());
      method = javaRunTimeVersion.getClass().getDeclaredMethod("major");
      int majorVersion = IntegerUtil.parseInteger(method.invoke(javaRunTimeVersion));
      switch (majorVersion) {
        case 9:
          return JAVA_9;
        case 10:
          return JAVA_10;
        case 11:
          return JAVA_11;
        case 12:
          return JAVA_12;
        case 13:
          return JAVA_13;
        case 14:
          return JAVA_14;
        case 15:
          return JAVA_15;
        case 16:
          return JAVA_16;
        case 17:
          return JAVA_17;
        case 18:
          return JAVA_18;
        case 19:
          return JAVA_19;
        case 20:
          return JAVA_20;
        case 21:
          return JAVA_21;
        case 22:
          return JAVA_22;
        default:
          return OTHER;
      }
    } catch (Exception exception) {
      JdkLoggerUtil.warn(LOGGER,
          "can't determine current jre version (maybe java.version is null), assuming that jre version is 8.",
          exception);
    }
    // default java 8
    return JAVA_8;
  }

}
