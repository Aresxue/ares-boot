package cn.ares.boot.util.json.entity;

import cn.ares.boot.util.json.annotation.JsonIgnoreParentProperties;

/**
 * @author: Ares
 * @time: 2022-06-08 15:21:24
 * @description: person
 * @version: JDK 1.8
 */
@JsonIgnoreParentProperties
public class Person extends Father {

  private String name;
  private Integer age;
  private String love;
  private boolean high;

  private String genderName;

  // https://stackoverflow.com/questions/74342327/jackson-parse-double-from-string-pure-number-format
  private Double doubleValue;

  public String getGenderName() {
    return name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getLove() {
    return love;
  }

  public void setLove(String love) {
    this.love = love;
  }

  public boolean isHigh() {
    return high;
  }

  public void setHigh(boolean high) {
    this.high = high;
  }

}
