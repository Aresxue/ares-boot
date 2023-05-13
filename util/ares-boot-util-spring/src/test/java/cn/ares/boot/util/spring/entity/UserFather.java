package cn.ares.boot.util.spring.entity;

/**
 * @author: Ares
 * @time: 2022-06-08 19:02:17
 * @description: User father
 * @version: JDK 1.8
 */
public class UserFather {

  private String father;

  private Integer fatherAge;

  public String getFather() {
    return father;
  }

  public void setFather(String father) {
    this.father = father;
  }

  public Integer getFatherAge() {
    return fatherAge;
  }

  public void setFatherAge(Integer fatherAge) {
    this.fatherAge = fatherAge;
  }
}
