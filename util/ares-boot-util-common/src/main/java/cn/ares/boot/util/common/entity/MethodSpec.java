package cn.ares.boot.util.common.entity;

/**
 * @author: Ares
 * @time: 2023-09-22 16:55:47
 * @description: 方法描述
 * @description: Method specification
 * @version: JDK 1.8
 */
public class MethodSpec {

  private String[] paramIdentifiers;
  private String returnIdentifier;

  public String[] getParamIdentifiers() {
    return paramIdentifiers;
  }

  public void setParamIdentifiers(String[] paramIdentifiers) {
    this.paramIdentifiers = paramIdentifiers;
  }

  public String getReturnIdentifier() {
    return returnIdentifier;
  }

  public void setReturnIdentifier(String returnIdentifier) {
    this.returnIdentifier = returnIdentifier;
  }

}
