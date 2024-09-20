package cn.ares.boot.base.model;


import static cn.ares.boot.base.model.status.BaseSystemStatus.SUCCESS;

import cn.ares.boot.base.model.exception.BaseException;
import cn.ares.boot.base.model.status.Status;
import java.io.Serializable;

/**
 * @author: Ares
 * @time: 2022-06-10 15:28:43
 * @description: 统一响应结果
 * @description: Unified response results
 * @version: JDK 1.8
 */
public final class Result<T> implements Serializable {

  private static final long serialVersionUID = -8195193710627247258L;

  /**
   * Whether the call was successful 调用是否成功
   */
  private boolean success;
  /**
   * Response code (a string of 4 bytes int and length 10 is 24 bytes under pointer compression)
   * 响应码（int为4字节而长度为10的字符串在指针压缩下为24个字节）
   */
  private int code = SUCCESS.getCode();
  /**
   * Response message (usually error message) 响应信息（一般是错误信息）
   */
  private String message;

  /**
   * Data (usually successful and only available when there is data) 数据（一般成功且有数据时才有）
   */
  private T data;

  /**
   * @author: Ares
   * @description: 返回成功的结果
   * @description: Return success result
   * @time: 2024-07-10 14:52:32
   * @return: cn.ares.boot.base.model.Result<T> 结果
   */
  public static <T> Result<T> success() {
    Result<T> result = new Result<>();
    result.setSuccess(true);
    return result;
  }

  /**
   * @author: Ares
   * @description: 返回带响应数据的成功结果
   * @description: Return success result with response data
   * @time: 2024-07-10 14:52:32
   * @params: [data] 响应数据
   * @return: cn.ares.boot.base.model.Result<T> 结果
   */
  public static <T> Result<T> success(T data) {
    Result<T> result = success();
    result.setData(data);
    return result;
  }

  /**
   * @author: Ares
   * @description: 返回失败结果
   * @description: Return fail result
   * @time: 2024-07-10 14:52:32
   * @params: [status, params] 状态，参数数组
   * @return: cn.ares.boot.base.model.Result<T> 结果
   */
  public static <T> Result<T> fail(Status status, Object... params) {
    Result<T> result = new Result<>();
    result.code(status.getCode());
    result.message(status.getMessage(), params);
    return result;
  }

  /**
   * @author: Ares
   * @description: 返回失败结果
   * @description: Return fail result
   * @time: 2024-07-23 20:13:34
   * @params: [baseException, params] 基础异常，入参
   * @return: cn.ares.boot.base.model.Result<T> 结果
   */
  public static <T> Result<T> fail(BaseException baseException, Object... params) {
    return fail(baseException.getStatus(), params);
  }

  /**
   * @author: Ares
   * @description: 返回失败的结果
   * @description: Return fail result
   * @time: 2024-07-10 14:52:32
   * @return: cn.ares.boot.base.model.Result<T> 结果
   */
  public static <T> Result<T> fail() {
    Result<T> result = new Result<>();
    result.setSuccess(false);
    return result;
  }

  /**
   * @author: Ares
   * @description: 设置返回数据
   * @description: Set return data
   * @time: 2024-07-10 14:52:32
   * @params: [data] 返回数据
   * @return: cn.ares.boot.base.model.Result<T> 结果
   */
  public Result<T> data(T data) {
    this.data = data;
    return this;
  }

  /**
   * @author: Ares
   * @description: 设置信息
   * @description: Set message
   * @time: 2024-07-10 14:52:32
   * @params: [message, params] 信息，参数数组
   * @return: cn.ares.boot.base.model.Result<T> 结果
   */
  public Result<T> message(String message, Object... params) {
    this.message = String.format(message, params);
    return this;
  }

  /**
   * @author: Ares
   * @description: 设置状态码
   * @description: Set status code
   * @time: 2024-07-10 14:52:32
   * @params: [code] 状态码
   * @return: cn.ares.boot.base.model.Result<T> 结果
   */
  public Result<T> code(Integer code) {
    this.code = code;
    return this;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "Result{" +
        "success=" + success +
        ", code=" + code +
        ", message='" + message + '\'' +
        ", data=" + data +
        '}';
  }

}
