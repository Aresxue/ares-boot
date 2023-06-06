package cn.ares.boot.util.json;

import cn.ares.boot.util.json.entity.Person;
import cn.ares.boot.util.json.serializer.ToEmptyStringNullKeySerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2022-06-08 15:16:47
 * @description: JsonUtil test
 * @version: JDK 1.8
 */
public class JsonUtilTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtilTest.class);

  @Test
  public void test() throws Exception {
    String jsonStr = "{\"name\":\"kele\",\"age\":18,\"love\":\"kele\",\"fatherName\":\"ares\"}";
    Person person = JsonUtil.parseObject(jsonStr, Person.class);
    LOGGER.info("person: {}", person);
    LOGGER.info("str: {}", JsonUtil.toJsonString(person));
    LOGGER.info("str: {}", JsonUtil.getJsonMapper(false, true).writeValueAsString(person));
    JsonUtil.configInclude("always");
    JsonUtil.configInclude("non_null");
    LOGGER.info("str: {}", JsonUtil.toJsonString(person));

    ObjectMapper objectMapper = JsonUtil.getJsonMapper();
    objectMapper.getSerializerProvider().setNullKeySerializer(new ToEmptyStringNullKeySerializer());
    Map<String, Map<String, String>> map = new HashMap<>();
    Map<String, String> innerMap = new HashMap<>();
    map.put("innerMap", innerMap);
    map.put("innerMap2", null);
    map.put(null, innerMap);
    LOGGER.info(JsonUtil.toJsonString(objectMapper, map));

    jsonStr = "{\"doubleValue\":\"123.00\"}";
    person = JsonUtil.parseObject(jsonStr, Person.class);
    LOGGER.info(JsonUtil.toJsonString(person));
  }

  @Test
  public void testBuildJavaType() {

  }

}
