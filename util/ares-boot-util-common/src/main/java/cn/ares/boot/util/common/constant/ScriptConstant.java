package cn.ares.boot.util.common.constant;

/**
 * @author: Ares
 * @time: 2022-02-07 16:51:53
 * @description: Script constant
 * @version: JDK 1.8
 */
public interface ScriptConstant {

  /**
   * script template suffix 脚本模板后缀
   */
  String SCRIPT_TEMPLATE_SUFFIX = ".template";

  /**
   * calculate method name 校验脚本方法名
   */
  String CALCULATE_METHOD_NAME = "calculate";

  /**
   * function param 方法入参
   */
  String FUNCTION_PARAM = "$0";

  /**
   * simple function param 简化 方法入参
   */
  String SIMPLE_FUNCTION_PARAM = "$";

  String INVOCABLE_KEY_FORMAT = "%s:%s:%s";

}
