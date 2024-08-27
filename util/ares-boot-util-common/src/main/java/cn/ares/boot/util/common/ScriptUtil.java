package cn.ares.boot.util.common;

import static cn.ares.boot.util.common.constant.ScriptConstant.CALCULATE_METHOD_NAME;
import static cn.ares.boot.util.common.constant.ScriptConstant.FUNCTION_PARAM;
import static cn.ares.boot.util.common.constant.ScriptConstant.SCRIPT_TEMPLATE_SUFFIX;
import static cn.ares.boot.util.common.constant.ScriptConstant.SIMPLE_FUNCTION_PARAM;

import cn.ares.boot.util.common.constant.ScriptLang;
import cn.ares.boot.util.common.throwable.ExecuteScriptException;
import cn.ares.boot.util.common.throwable.UnknownException;
import cn.ares.boot.util.common.thread.ThreadUtil;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author: Ares
 * @time: 2022-06-13 17:03:13
 * @description: Script util
 * @version: JDK 1.8
 */
public class ScriptUtil {

  private static ScriptEngineManager engineManager = new ScriptEngineManager();
  private static final Map<String, String> SCRIPT_TEMPLATE_CACHE = new ConcurrentHashMap<>();
  private static final Map<String, ScriptEngine> SCRIPT_ENGINE_CACHE = new ConcurrentHashMap<>();
  private static final String RETURN = "return";

  /**
   * @author: Ares
   * @description: 执行表达式
   * @description: Execution expression
   * @time: 2023-05-08 13:07:00
   * @params: [lang, expression, param] 语言，表达式，参数
   * @return: java.lang.Object
   */
  public static Object calculate(ScriptLang lang, String expression, Object param) {
    if (StringUtil.isEmpty(expression) || StringUtil.isEmpty(lang)) {
      return param;
    }
    // 替换$为$0, 实际脚本中为$0但为了使用简洁允许用户直接用$表示对象
    // Replace $ with $0, which is $0 in the actual script but allows users to directly use $ to represent objects for brevity
    expression = StringUtil.replace(expression, SIMPLE_FUNCTION_PARAM, FUNCTION_PARAM);
    try {
      String langName = lang.getLangName();
      String templatePath = lang.getTemplatePath();
      if (StringUtil.isEmpty(templatePath)) {
        templatePath = langName + SCRIPT_TEMPLATE_SUFFIX;
      }
      ScriptEngine engine = SCRIPT_ENGINE_CACHE.computeIfAbsent(langName,
          value -> engineManager.getEngineByName(langName));
      if (null == engine) {
        throw new ExecuteScriptException("Not found script engine by " + langName);
      }
      String finalTemplatePath = templatePath;
      String template = SCRIPT_TEMPLATE_CACHE.computeIfAbsent(templatePath, value -> {
        InputStream templateStream = ThreadUtil.getResourceAsStream(finalTemplatePath);
        if (null == templateStream) {
          templateStream = ScriptUtil.class.getClassLoader().getResourceAsStream(finalTemplatePath);
        }
        return StringUtil.parseSteamToString(templateStream);
      });
      if (!expression.contains(RETURN)) {
        expression = "return " + expression;
      }
      String script = String.format(template, CALCULATE_METHOD_NAME, FUNCTION_PARAM, expression);
      engine.eval(script);
      Invocable invocable = (Invocable) engine;

      try {
        // invokeFunction没法自动识别Object[], 会导致脚本无法有效识别参数
        if (param instanceof Object[]) {
          return invocable.invokeFunction(CALCULATE_METHOD_NAME, (Object[]) param);
        } else {
          return invocable.invokeFunction(CALCULATE_METHOD_NAME, param);
        }
      } catch (Exception e) {
        throw new ExecuteScriptException("Execute script exception: ", e);
      }
    } catch (ExecuteScriptException e) {
      throw e;
    } catch (Throwable e) {
      throw new UnknownException("Unknown exception: ", e);
    }
  }

  /**
   * @author: Ares
   * @description: 设置引擎管理器的类加载器
   * @description: Set the classloader for the engine manager
   * @time: 2023-05-08 13:07:52
   * @params: [classLoader]
   */
  public static void setEngineManager(ClassLoader classLoader) {
    engineManager = new ScriptEngineManager(classLoader);
  }

}
