package cn.ares.boot.util.json.exception;

/**
 * @author: Ares
 * @time: 2021-11-16 12:03:00
 * @description: Json exception
 * @version: JDK 1.8
 */
public class JsonException extends RuntimeException {

  private static final long serialVersionUID = -9172605386635474737L;

  public JsonException(String message, Throwable cause) {
    super(message, cause);
  }
}
