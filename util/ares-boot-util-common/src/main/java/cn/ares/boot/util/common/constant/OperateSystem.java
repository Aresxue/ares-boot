package cn.ares.boot.util.common.constant;

import java.util.Arrays;

/**
 * @author: Ares
 * @time: 2021-08-09 15:02:00
 * @description: 操作系统
 * @description: Operate system
 * @version: JDK 1.8
 */
public enum OperateSystem {
  /*
  detail
   */
  WINDOWS("Windows", "windows"),
  MAC("Mac", "mac"),
  UNIX("Unix", "x11"),
  LINUX("Linux", "linux"),
  ANDROID("Android", "android"),
  IPHONE("IPhone", "iphone"),
  UNKNOWN("UnKnown", "unKnown");

  private final String system;

  private final String lowerSystem;

  private static final OperateSystem LOCAL_OPERATE_SYSTEM;

  static {
    LOCAL_OPERATE_SYSTEM = getOperateSystem(System.getProperty("os.name"));
  }

  OperateSystem(String system, String lowerSystem) {
    this.system = system;
    this.lowerSystem = lowerSystem;
  }

  public String getSystem() {
    return system;
  }

  public String getLowerSystem() {
    return lowerSystem;
  }

  public static OperateSystem getOperateSystem(String info) {
    return Arrays.stream(OperateSystem.values())
        .filter(system -> info.toLowerCase().contains(system.getLowerSystem()))
        .findFirst()
        .orElse(OperateSystem.UNKNOWN);
  }

  public static OperateSystem getLocalOperateSystem() {
    return LOCAL_OPERATE_SYSTEM;
  }

  public static boolean isLocalMachine() {
    OperateSystem operateSystem = OperateSystem.getLocalOperateSystem();
    // 只在windows和mac时匹配，认为这两种是开发的本地机器
    // Match only on windows and mac, consider these two to be the local machine for development
    return MAC.equals(operateSystem) || WINDOWS.equals(operateSystem);
  }

}
