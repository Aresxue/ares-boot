package cn.ares.boot.util.spring.entity;

/**
 * @author: Ares
 * @time: 2022-06-08 19:03:27
 * @description: Copy user
 * @version: JDK 1.8
 */
public class CopyUser extends CopyUserFather {

  private String name;
  private Long id;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "CopyUser{" +
        "father='" + super.getFather() + '\'' +
        ", fatherAge='" + super.getFatherAge() + '\'' +
        ", name='" + name + '\'' +
        ", id=" + id +
        '}';
  }
}
