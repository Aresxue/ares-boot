package cn.ares.boot.util.yaml.entity;

/**
 * @author: Ares
 * @time: 2022-08-09 10:44:41
 * @description:
 * @version: JDK 1.8
 */
public class Detail {

  private Integer age;

  private Long money;

  private String detailName;

  private String specialDetailName;

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public Long getMoney() {
    return money;
  }

  public void setMoney(Long money) {
    this.money = money;
  }

  public String getDetailName() {
    return detailName;
  }

  public void setDetailName(String detailName) {
    this.detailName = detailName;
  }

  public String getSpecialDetailName() {
    return specialDetailName;
  }

  public void setSpecialDetailName(String specialDetailName) {
    this.specialDetailName = specialDetailName;
  }
}
