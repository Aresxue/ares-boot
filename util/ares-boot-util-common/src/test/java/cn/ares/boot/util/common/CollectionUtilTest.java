package cn.ares.boot.util.common;

import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2023-12-07 11:57:29
 * @description: CollectionUtil test
 * @version: JDK 1.8
 */
public class CollectionUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(CollectionUtilTest.class);

  public static void main(String[] args) {
    List<Integer> list = CollectionUtil.asList(1, 2);
    JdkLoggerUtil.info(LOGGER, list);
    JdkLoggerUtil.info(LOGGER, CollectionUtil.asList(list, 2, 3));
    JdkLoggerUtil.info(LOGGER, CollectionUtil.asList(null, 2, 3));

    Set<Integer> set = CollectionUtil.asSet(1, 2);
    JdkLoggerUtil.info(LOGGER, set);
    JdkLoggerUtil.info(LOGGER, CollectionUtil.asLinkedHashSet(set, 2, 3));
    JdkLoggerUtil.info(LOGGER, String.valueOf(CollectionUtil.asLinkedHashSet(null, 2, 3)));

    JdkLoggerUtil.info(LOGGER, CollectionUtil.asSet(set, 2, 3));
    JdkLoggerUtil.info(LOGGER, CollectionUtil.asSet(null, 2, 3));
  }

}
