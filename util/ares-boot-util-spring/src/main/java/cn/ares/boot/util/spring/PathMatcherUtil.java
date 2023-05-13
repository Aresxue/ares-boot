package cn.ares.boot.util.spring;

import cn.ares.boot.util.common.ArrayUtil;
import cn.ares.boot.util.common.CollectionUtil;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @author: Ares
 * @time: 2022-12-21 14:43:42
 * @description: PathMatcher util
 * @version: JDK 1.8
 */
public class PathMatcherUtil {

  private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

  /**
   * @author: Ares
   * @description: 获取实例
   * @description: Get instance
   * @time: 2023-05-11 12:51:52
   * @params: []
   * @return: org.springframework.util.PathMatcher
   */
  public static PathMatcher getInstance() {
    return PATH_MATCHER;
  }

  /**
   * @author: Ares
   * @description: 判断路径是否匹配格式
   * @description: Determines whether the path matches the format
   * @time: 2023-05-11 12:52:13
   * @params: [pattern, path] 匹配格式，路径
   * @return: boolean 是否匹配
   */
  public static boolean match(String pattern, String path) {
    return PATH_MATCHER.match(pattern, path);
  }

  /**
   * @author: Ares
   * @description: 判断路径是否匹配任一格式
   * @description: Determines whether the path matches any format
   * @time: 2023-05-11 12:52:50
   * @params: [patternCollection, path] 匹配格式集合，路径
   * @return: boolean 是否匹配
   */
  public static boolean match(Collection<String> patternCollection, String path) {
    if (CollectionUtil.isEmpty(patternCollection)) {
      return false;
    }
    return patternCollection.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
  }

  /**
   * @author: Ares
   * @description: 判断路径是否匹配任一格式
   * @description: Determines whether the path matches any format
   * @time: 2023-05-11 12:53:38
   * @params: [patterns, path] 匹配格式数组，路径
   * @return: boolean 是否匹配
   */
  public static boolean match(String[] patterns, String path) {
    if (ArrayUtil.isEmpty(patterns)) {
      return false;
    }
    return Arrays.stream(patterns)
        .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
  }

  /**
   * @author: Ares
   * @description: 判断路径是否不匹配任一格式
   * @description: Determines whether the path does not match any format
   * @time: 2023-05-11 12:53:38
   * @params: [patterns, path] 匹配格式集合，路径
   * @return: boolean 是否匹配
   */
  public static boolean notMatch(Collection<String> patternCollection, String path) {
    if (CollectionUtil.isEmpty(patternCollection)) {
      return true;
    }
    return patternCollection.stream().noneMatch(pattern -> PATH_MATCHER.match(pattern, path));
  }

  /**
   * @author: Ares
   * @description: 判断路径是否不匹配任一格式
   * @description: Determines whether the path does not match any format
   * @time: 2023-05-11 12:53:38
   * @params: [patterns, path] 匹配格式数组，路径
   * @return: boolean 是否匹配
   */
  public static boolean notMatch(String[] patterns, String path) {
    if (ArrayUtil.isEmpty(patterns)) {
      return true;
    }
    return Arrays.stream(patterns).noneMatch(pattern -> PATH_MATCHER.match(pattern, path));
  }

}
