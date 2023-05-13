package cn.ares.boot.util.servlet.constant;

/**
 * @author: Ares
 * @time: 2022-06-08 16:10:29
 * @description: 浏览器类型
 * @description: Browser type
 * @version: JDK 1.8
 */
public enum BrowserType {

  /**
   * detail
   */
  CHROME("chrome"),
  FIREFOX("firefox"),
  IE("rv"),
  NETSCAPE("mozilla/7.0"),
  NETSCAPE6("netscape6"),
  EDGE("edge"),
  MSIE("msie"),
  SAFARI("safari"),
  OPR("opr"),
  OPERA("opera");

  BrowserType(String name) {
    this.name = name;
  }

  private final String name;

  public String value() {
    return this.name;
  }

}
