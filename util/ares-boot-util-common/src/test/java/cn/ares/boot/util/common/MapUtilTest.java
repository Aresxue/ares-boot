package cn.ares.boot.util.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Ares
 * @time: 2022-11-28 22:53:35
 * @description: MapUtil test
 * @version: JDK 1.8
 */
public class MapUtilTest {

  public static void main(String[] args) {
    Map<String, String> sourceMap = new HashMap<>();
    sourceMap.put("name", "ares");
    sourceMap.put("test", null);
    sourceMap.put(null, "value");
    System.out.println(MapUtil.trimValue(sourceMap));

    Map<String, String> map = MapUtil.newMap("name", "ares", "test", null, null, "value");
    System.out.println(map);
    System.out.println(map.get(null));
    map.put(null, "null");
    System.out.println(map.get(null));
  }

}
