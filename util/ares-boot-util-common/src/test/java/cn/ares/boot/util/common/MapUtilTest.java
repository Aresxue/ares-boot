package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2022-11-28 22:53:35
 * @description: MapUtil test
 * @version: JDK 1.8
 */
public class MapUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(MapUtilTest.class);

  public static void main(String[] args) {
    Map<String, String> sourceMap = new HashMap<>();
    sourceMap.put("name", "ares");
    sourceMap.put("test", null);
    sourceMap.put(null, "value");
    JdkLoggerUtil.info(LOGGER, MapUtil.trimValue(sourceMap));

    Map<String, String> map = MapUtil.newLinkedHashMap("name", "ares", "name1", "ares1", "name2",
        "ares2", "test", null, null, "value", "1");
    JdkLoggerUtil.info(LOGGER, map);
    JdkLoggerUtil.info(LOGGER, map.get(null));
    map.put(null, "null");
    JdkLoggerUtil.info(LOGGER, map.get(null));

    Map<String, Integer> testMap = new LinkedHashMap<>();
    for (int i = 1; i <= 11; i++) {
      testMap.put("Key" + i, i);
    }
    int numOfParts = 3;
    // 将map分成3部分
    List<Map<String, Integer>> splitMaps = MapUtil.split(testMap, numOfParts);

    JdkLoggerUtil.info(LOGGER, "original Map: " + testMap);
    JdkLoggerUtil.info(LOGGER, "split into " + numOfParts + " parts:");
    for (int i = 0; i < splitMaps.size(); i++) {
      JdkLoggerUtil.info(LOGGER, ("part " + (i + 1) + ": " + splitMaps.get(i)));
    }
  }

}
