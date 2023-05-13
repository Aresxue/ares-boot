package cn.ares.boot.util.yaml;

import cn.ares.boot.util.common.StringUtil;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.parser.ParserException;

/**
 * @author: Ares
 * @time: 2021-07-06 13:59:00
 * @description: Yaml配置字符串解析
 * @description: Yaml config string parse
 * @version: JDK 1.8
 */
public class YamlConfigParse {

  private static final Logger LOGGER = LoggerFactory.getLogger(YamlConfigParse.class);

  protected static Yaml createYaml() {
    LoaderOptions options = new LoaderOptions();
    return new Yaml(new MapAppenderConstructor(options));
  }

  protected Properties parse(String configText) {
    final Properties result = new Properties();
    process((properties, map) -> result.putAll(properties), createYaml(), configText);
    return result;
  }

  protected static boolean process(BiConsumer<Properties, Map<String, Object>> biConsumer,
      Yaml yaml, String content) {
    int count = 0;
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("loading from yaml: {}", content);
    }

    for (Object object : yaml.loadAll(content)) {
      if (object != null && process(asMap(object), biConsumer)) {
        ++count;
      }
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("loaded {} document {} from YAML resource: {}", count, (count > 1 ? "s" : ""),
          content);
    }

    return count > 0;
  }

  protected static boolean process(Map<String, Object> map,
      BiConsumer<Properties, Map<String, Object>> biConsumer) {
    Properties properties = new Properties();
    properties.putAll(getFlattenedMap(map));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("merging document (no matchers set): {}", map);
    }

    biConsumer.accept(properties, map);
    return true;
  }

  protected static Map<String, Object> asMap(Object object) {
    Map<String, Object> result = new LinkedHashMap<>();
    if (!(object instanceof Map)) {
      result.put("document", object);
      return result;
    } else {
      Map<Object, Object> map = (Map) object;

      map.forEach((key, value) -> {
        if (value instanceof Map) {
          value = asMap(value);
        }

        if (key instanceof CharSequence) {
          result.put(key.toString(), value);
        } else {
          result.put("[" + key.toString() + "]", value);
        }
      });

      return result;
    }
  }

  protected static Map<String, Object> getFlattenedMap(Map<String, Object> source) {
    Map<String, Object> result = new LinkedHashMap<>();
    buildFlattenedMap(result, source, null);
    return result;
  }

  protected static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source,
      String path) {

    Iterator<Entry<String, Object>> iterator = source.entrySet().iterator();

    while (true) {
      while (iterator.hasNext()) {
        Entry<String, Object> entry = iterator.next();
        String key = entry.getKey();
        if (!StringUtil.isBlank(path)) {
          if (key.startsWith("[")) {
            key = path + key;
          } else {
            key = path + '.' + key;
          }
        }

        Object value = entry.getValue();
        if (value instanceof String) {
          result.put(key, value);
        } else if (value instanceof Map) {
          Map<String, Object> map = (Map) value;
          buildFlattenedMap(result, map, key);
        } else if (value instanceof Collection) {
          Collection<?> collection = (Collection<?>) value;
          int count = 0;

          for (Object object : collection) {
            buildFlattenedMap(result, Collections.singletonMap("[" + count++ + "]", object), key);
          }
        } else {
          result.put(key, value != null ? value.toString() : "");
        }
      }

      return;
    }
  }

  protected static class MapAppenderConstructor extends Constructor {

    MapAppenderConstructor(LoaderOptions loaderOptions) {
      super(loaderOptions);
    }

    @Override
    protected Map<Object, Object> constructMapping(MappingNode node) {
      try {
        return super.constructMapping(node);
      } catch (IllegalStateException var3) {
        throw new ParserException("While parsing MappingNode", node.getStartMark(),
            var3.getMessage(), node.getEndMark());
      }
    }

    protected Map<Object, Object> createDefaultMap() {
      final Map<Object, Object> delegate = super.createDefaultMap(16);
      return new AbstractMap<Object, Object>() {
        @Override
        public Object put(Object key, Object value) {
          if (delegate.containsKey(key)) {
            throw new IllegalStateException("Duplicate key: " + key);
          } else {
            return delegate.put(key, value);
          }
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
          return delegate.entrySet();
        }
      };
    }
  }

  protected Iterable<Object> loadAll(InputStream inputStream) {
    Yaml yaml = createYaml();
    return yaml.loadAll(inputStream);
  }

  protected YamlConfigParse() {

  }

}
