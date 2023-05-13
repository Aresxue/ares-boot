package cn.ares.boot.base.meta;


import static cn.ares.boot.base.meta.status.BaseSystemStatus.SUCCESS;

import cn.ares.boot.base.meta.status.BaseSystemStatus;
import java.io.Serializable;

/**
 * @author: Ares
 * @time: 2022-06-10 15:28:43
 * @description: Unified response results
 * @description: 统一响应结果
 * @version: JDK 1.8
 */
public final class Result<T> implements Serializable {

  private static final long serialVersionUID = -8195193710627247258L;

  /**
   * Whether the call was successful
   * 调用是否成功
   */
  private boolean success;
  /**
   * Response code (a string of 4 bytes int and length 10 is 24 bytes under pointer compression)
   * 响应码（int为4字节而长度为10的字符串在指针压缩下为24个字节）
   */
  private int code = SUCCESS.getCode();
  /**
   * Response message (usually error message)
   * 响应信息（一般是错误信息）
   */
  private String message;

  /**
   * Data (usually successful and only available when there is data)
   * 数据（一般成功且有数据时才有）
   */
  private T data;

  /***
   * Constructor private
   * 构造器私有
   */
  private Result() {
  }

  public static <T> Result<T> success() {
    Result<T> result = new Result<>();
    result.success(true);
    return result;
  }

  public static <T> Result<T> success(T data) {
    Result<T> result = success();
    result.data(data);
    return result;
  }

  private Result<T> success(boolean success) {
    this.success = success;
    return this;
  }
  private Result<T> code(int code) {
    this.code = code;
    return this;
  }
  private Result<T> message(String message) {
    this.message = message;
    return this;
  }
  private Result<T> data(T data) {
    this.data = data;
    return this;
  }

  public boolean isSuccess() {
    return success;
  }
  public int getCode() {
    return code;
  }
  public String getMessage() {
    return message;
  }
  public T getData() {
    return data;
  }

}
