package cn.ares.boot.util.common.throwable;

/**
 * @author: Ares
 * @time: 2024-05-27 16:22:07
 * @description: 格式化参数异常
 * @description: format IllegalArgumentException
 * @version: JDK 1.8
 */
public class FormatIllegalArgumentException extends IllegalArgumentException {

  private static final long serialVersionUID = 502986204468797461L;

  public FormatIllegalArgumentException() {
  }

  public FormatIllegalArgumentException(String message, Object... args) {
    super(String.format(message, args));
  }

  public FormatIllegalArgumentException(String message, Throwable cause, Object... args) {
    super(String.format(message, args), cause);
  }

  public FormatIllegalArgumentException(Throwable cause) {
    super(cause);
  }

}
