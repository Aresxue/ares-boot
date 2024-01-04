package cn.ares.boot.version;

/**
 * @author: Ares
 * @time: 2023-11-09 17:41:18
 * @description: Ares boot version
 * @version: JDK 1.8
 */
public class AresBootVersion {

  /**
   * 版本键 Version key
   */
  public static final String VERSION_KEY = "ares.boot.version";

  /**
   * 版本值 Version value
   */
  private static final String VERSION_VALUE = "1.0.1";

  public static String getVersion() {
    return VERSION_VALUE;
  }

}
