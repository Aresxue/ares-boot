package cn.ares.boot.demo;

import cn.ares.boot.starter.spring.AresApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: Ares
 * @time: 2022-06-09 16:38:01
 * @description: Application startup main class
 * @description: 应用启动主类
 * @version: JDK 1.8
 */
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    AresApplication.run(Application.class, args);
  }

}
