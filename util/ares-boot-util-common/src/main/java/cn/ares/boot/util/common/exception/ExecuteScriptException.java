package cn.ares.boot.util.common.exception;

/**
 * @author: Ares
 * @time: 2022-08-17 15:15:52
 * @description: Invocable invoke functionException
 * @version: JDK 1.8
 */
public class ExecuteScriptException extends RuntimeException {

  private static final long serialVersionUID = -2590455067812497134L;

  public ExecuteScriptException(String message) {
    super(message);
  }

  public ExecuteScriptException(String message, Throwable cause) {
    super(message, cause);
  }

}
