package cn.ares.boot.util.yaml.entity;

/**
 * @author: Ares
 * @time: 2022-06-08 15:21:24
 * @description: person
 * @version: JDK 1.8
 */
public class Person {

  private String name;

  private String complexName;

  private String specialComplexName;

  private Detail detail;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getComplexName() {
    return complexName;
  }

  public void setComplexName(String complexName) {
    this.complexName = complexName;
  }

  public String getSpecialComplexName() {
    return specialComplexName;
  }

  public void setSpecialComplexName(String specialComplexName) {
    this.specialComplexName = specialComplexName;
  }

  public Detail getDetail() {
    return detail;
  }

  public void setDetail(Detail detail) {
    this.detail = detail;
  }
}
