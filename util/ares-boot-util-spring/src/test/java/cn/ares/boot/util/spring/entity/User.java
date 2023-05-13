package cn.ares.boot.util.spring.entity;

/**
 * @author: Ares
 * @time: 2022-06-08 19:01:59
 * @description: User
 * @version: JDK 1.8
 */
public class User extends UserFather {

  private static int level = 520;

  private String names;
  private Long id;
  private String extend;

  public User() {
  }

  public User(String names, Long id, String extend) {
    this.names = names;
    this.id = id;
    this.extend = extend;
  }

  public String getNames() {
    return names;
  }

  public void setNames(String names) {
    this.names = names;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "User{" +
        "names='" + names + '\'' +
        ", id=" + id +
        ", extend='" + extend + '\'' +
        '}';
  }
}
