package cn.ares.boot.util.yaml;

import cn.ares.boot.util.common.MapUtil;
import cn.ares.boot.util.common.PropertyUtil;
import cn.ares.boot.util.common.StringUtil;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.yaml.snakeyaml.Yaml;

/**
 * @author: Ares
 * @time: 2021-06-03 15:39:00
 * @description: Yaml util
 * @version: JDK 1.8
 */
public class YamlUtil {

  private static final String YAML_SEPARATOR = ": ";

  private static final YamlConfigParse YAML_CONFIG_PARSE = new YamlConfigParse();
  private static final Yaml DEFAULT_YAML = YamlConfigParse.createYaml();

  /**
   * @author: Ares
   * @description: properties转yaml文本
   * @time: 2021-06-04 15:27
   * @params: [properties] 请求参数
   * @return: java.lang.String 响应参数
   */
  public static String propertiesToYaml(Properties properties) {
    return DEFAULT_YAML.dumpAsMap(PropertyUtil.convertToNestedMap(properties));
  }

  /**
   * @author: Ares
   * @description: 解析输入流为映射
   * @description: Parse the input stream as a map
   * @time: 2023-05-08 15:01:09
   * @params: [inputStream] 输入流
   * @return: java.util.Map<java.lang.String, java.lang.String>
   */
  public static Map<String, String> mapParse(InputStream inputStream) {
    Map<String, String> resultMap = MapUtil.newHashMap(16);
    Map<String, Object> map = DEFAULT_YAML.loadAs(inputStream, Map.class);
    for (String key : map.keySet()) {
      if (!(map.get(key) instanceof Map)) {
        resultMap.put(key, String.valueOf(map.get(key)));
      }
    }
    return resultMap;
  }

  /**
   * @author: Ares
   * @description: 解析配置文本为映射
   * @description: Parse the configuration text as a map
   * @time: 2023-05-08 15:00:24
   * @params: [configText] 配置文本
   * @return: java.util.Map<java.lang.String, java.lang.String>
   */
  public static Map<String, String> mapParse(String configText) {
    if (StringUtil.isEmpty(configText)) {
      return Collections.emptyMap();
    }
    return mapParse(StringUtil.parseStringToStream(configText));
  }

  /**
   * @author: Ares
   * @description: 解析输入流为属性
   * @description: Parse the input stream into properties
   * @time: 2023-05-08 15:01:39
   * @params: [inputStream] 输入流
   * @return: java.util.Properties 属性
   */
  public static Properties parse(InputStream inputStream) {
    if (null != inputStream) {
      String configText = StringUtil.parseSteamToString(inputStream);
      return parse(configText);
    }
    return new Properties();
  }

  /**
   * @author: Ares
   * @description: 解析配置文本为属性
   * @description: Parse the input stream into properties
   * @time: 2023-05-08 15:01:39
   * @params: [configText] 配置文本
   * @return: java.util.Properties 属性
   */
  public static Properties parse(String configText) {
    return YAML_CONFIG_PARSE.parse(configText);
  }

  public static Iterable<Object> loadAll(InputStream inputStream) {
    return YAML_CONFIG_PARSE.loadAll(inputStream);
  }

  /**
   * @author: Ares
   * @description: 把yaml字符串转为对象
   * @description: Turn the yaml string into an object
   * @time: 2023-05-08 15:03:29
   * @params: [yaml, clazz] yaml字符串，类
   * @return: T 对象
   */
  public static <T> T yamlToObject(String yaml, Class<T> clazz) {
    return DEFAULT_YAML.loadAs(StringUtil.parseStringToStream(yaml), clazz);
  }

  /**
   * @author: Ares
   * @description: 对象转为yaml字符串
   * @description: Object to yaml string
   * @time: 2023-05-08 15:02:59
   * @params: [object] 对象
   * @return: java.lang.String yaml字符串
   */
  public static String objectToYaml(Object object) {
    StringWriter writer = new StringWriter();
    DEFAULT_YAML.dump(object, writer);
    return writer.toString();
  }

}
