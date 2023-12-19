package cn.ares.boot.util.yaml;

import cn.ares.boot.util.yaml.entity.Detail;
import cn.ares.boot.util.yaml.entity.Person;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2022-08-09 10:41:21
 * @description: YamlUtil test
 * @version: JDK 1.8
 */
public class YamlUtilTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(YamlUtilTest.class);

  @Test
  public void testYamlToObject() {
    Person person = new Person();
    person.setName("ares");
    person.setComplexName("ares-xue");
    person.setSpecialComplexName("Ares_xue");
    Detail detail = new Detail();
    detail.setDetailName("xuebing");
    detail.setAge(18);
    detail.setMoney(100_000_000L);
    detail.setSpecialDetailName("xuebing520");
    person.setDetail(detail);
    String yaml = YamlUtil.objectToYaml(person);
    LOGGER.info("person yaml: {}", yaml);
    person = YamlUtil.yamlToObject(yaml, Person.class);
    LOGGER.info("person: {}", person);
  }

  @Test
  public void testPropertiesToYaml() {
    Properties properties = new Properties();
    properties.put("name", "ares");
    properties.put("person.age", "18");
    properties.put("mq.replay-report.topic", 1);
    properties.put("mq.traffic-report.topic", 2);
    LOGGER.info("yaml content: \n{}", YamlUtil.propertiesToYaml(properties));
  }

}
