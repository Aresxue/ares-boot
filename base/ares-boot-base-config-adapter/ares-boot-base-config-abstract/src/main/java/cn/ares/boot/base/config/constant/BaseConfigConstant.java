package cn.ares.boot.base.config.constant;

/**
 * @author: Ares
 * @time: 2024-07-02 17:51:39
 * @description: 基础配置常量
 * @description: Base config constant
 * @version: JDK 1.8
 */
public interface BaseConfigConstant {

  /**
   * 应用名称键
   */
  String APP_NAME_KEY = "app.name";
  /**
   * 框架应用配置前缀
   * Framework application configuration prefix
   */
  String ARES_APPLICATION_PREFIX = "ares.application.";
  /**
   * 框架应用名称键
   * Framework application name key
   */
  String BOOT_APP_NAME_KEY = ARES_APPLICATION_PREFIX + "name";
  /**
   * 项目名称键
   */
  String PROJECT_NAME_KEY = "project.name";
  /**
   * 文件编码键
   */
  String FILE_ENCODING_KEY = "file.encoding";

}
