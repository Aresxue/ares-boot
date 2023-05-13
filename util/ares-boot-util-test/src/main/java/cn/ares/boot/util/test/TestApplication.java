package cn.ares.boot.util.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: Ares
 * @time: 2021-10-21 16:55:00
 * @description: Test application
 * @version: JDK 1.8
 */
@SpringBootApplication(scanBasePackages = {"cn", "com"})
public class TestApplication {

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }

}
