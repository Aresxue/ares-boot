package cn.ares.boot.util.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author: Ares
 * @time: 2021-10-21 16:55:00
 * @description: Test base class
 * @version: JDK 1.8
 */
@SpringBootConfiguration
@SpringBootTest(classes = TestApplication.class)
public class BootBaseTest {

  @BeforeEach
  public void before() {
  }

  @AfterEach
  public void after() {
  }

}
