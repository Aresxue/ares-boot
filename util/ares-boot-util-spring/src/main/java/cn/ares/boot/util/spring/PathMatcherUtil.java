package cn.ares.boot.util.spring;

import cn.ares.boot.util.common.ArrayUtil;
import cn.ares.boot.util.common.CollectionUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @author: Ares
 * @time: 2022-12-21 14:43:42
 * @description: PathMatcher util
 * @version: JDK 1.8
 */
public class PathMatcherUtil {

  /**
   * AntPathMatcher非完全线程安全，不能对此实例进行修改否则可能会引发线程安全问题
   * 当前实例的方法都不修改AntPathMatcher的内部状态所以是线程安全的
   */
  private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

  /**
   * @author: Ares
   * @description: 获取实例
   * @description: Get instance
   * @time: 2023-05-11 12:51:52
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
    return match(patternCollection, path, false);
  }

  /**
   * @author: Ares
   * @description: 判断路径是否匹配任一格式
   * @description: Determines whether the path matches any format
   * @time: 2023-05-11 12:52:50
   * @params: [patternCollection, path, parallel] 匹配格式集合，路径，是否并行
   * @return: boolean 是否匹配
   */
  public static boolean match(Collection<String> patternCollection, String path, boolean parallel) {
    if (CollectionUtil.isEmpty(patternCollection)) {
      return false;
    }
    if (parallel) {
      return patternCollection.parallelStream().anyMatch(pattern -> match(pattern, path));
    } else {
      return patternCollection.stream().anyMatch(pattern -> match(pattern, path));
    }
  }

  /**
   * @author: Ares
   * @description: 获取路径命中的匹配规则
   * @description: Get the pattern for the path hit
   * @time: 2024-08-06 16:25:01
   * @params: [patternCollection, path] 匹配格式集合，路径
   * @return: java.util.Optional<java.lang.String> 命中匹配规则
   */
  public static Optional<String> hitPattern(Collection<String> patternCollection, String path) {
    return hitPattern(patternCollection, path, false);
  }

  /**
   * @author: Ares
   * @description: 获取路径命中的匹配规则
   * @description: Get the pattern for the path hit
   * @time: 2024-08-06 16:25:01
   * @params: [patternCollection, path, parallel] 匹配格式集合，路径，是否并行
   * @return: java.util.Optional<java.lang.String> 命中匹配规则
   */
  public static Optional<String> hitPattern(Collection<String> patternCollection, String path,
      boolean parallel) {
    if (CollectionUtil.isEmpty(patternCollection)) {
      return Optional.empty();
    }
    if (parallel) {
      return patternCollection.parallelStream().filter(pattern -> match(pattern, path)).findFirst();
    } else {
      return patternCollection.stream().filter(pattern -> match(pattern, path)).findFirst();
    }
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
    return match(patterns, path, false);
  }

  /**
   * @author: Ares
   * @description: 判断路径是否匹配任一格式
   * @description: Determines whether the path matches any format
   * @time: 2023-05-11 12:53:38
   * @params: [patterns, path, parallel] 匹配格式数组，路径，是否并行
   * @return: boolean 是否匹配
   */
  public static boolean match(String[] patterns, String path, boolean parallel) {
    if (ArrayUtil.isEmpty(patterns)) {
      return false;
    }
    if (parallel) {
      return Arrays.stream(patterns).parallel().anyMatch(pattern -> match(pattern, path));
    } else {
      return Arrays.stream(patterns).anyMatch(pattern -> match(pattern, path));
    }
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
    return notMatch(patternCollection, path, false);
  }

  /**
   * @author: Ares
   * @description: 判断路径是否不匹配任一格式
   * @description: Determines whether the path does not match any format
   * @time: 2023-05-11 12:53:38
   * @params: [patterns, path, parallel] 匹配格式集合，路径，是否并行
   * @return: boolean 是否匹配
   */
  public static boolean notMatch(Collection<String> patternCollection, String path,
      boolean parallel) {
    if (CollectionUtil.isEmpty(patternCollection)) {
      return true;
    }
    if (parallel) {
      return patternCollection.parallelStream().noneMatch(pattern -> match(pattern, path));
    } else {
      return patternCollection.stream().noneMatch(pattern -> match(pattern, path));
    }
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
    return notMatch(patterns, path, false);
  }

  /**
   * @author: Ares
   * @description: 判断路径是否不匹配任一格式
   * @description: Determines whether the path does not match any format
   * @time: 2023-05-11 12:53:38
   * @params: [patterns, path, parallel] 匹配格式数组，路径，是否并行
   * @return: boolean 是否匹配
   */
  public static boolean notMatch(String[] patterns, String path, boolean parallel) {
    if (ArrayUtil.isEmpty(patterns)) {
      return true;
    }
    if (parallel) {
      return Arrays.stream(patterns).parallel().noneMatch(pattern -> match(pattern, path));
    } else {
      return Arrays.stream(patterns).noneMatch(pattern -> match(pattern, path));
    }
  }

  /**
   * Given a pattern and a full path, determine the pattern-mapped part.
   * <p>This method is supposed to find out which part of the path is matched
   * dynamically through an actual pattern, that is, it strips off a statically
   * defined leading path from the given full path, returning only the actually
   * pattern-matched part of the path.
   * <p>For example: For "myroot/*.html" as pattern and "myroot/myfile.html"
   * as full path, this method should return "myfile.html". The detailed
   * determination rules are specified to this PathMatcher's matching strategy.
   * <p>A simple implementation may return the given full path as-is in case
   * of an actual pattern, and the empty String in case of the pattern not
   * containing any dynamic parts (i.e. the {@code pattern} parameter being
   * a static path that wouldn't qualify as an actual {@link #isPattern pattern}).
   * A sophisticated implementation will differentiate between the static parts
   * and the dynamic parts of the given path pattern.
   * @param pattern the path pattern
   * @param path the full path to introspect
   * @return the pattern-mapped part of the given {@code path}
   * (never {@code null})
   */
  public static String extractPathWithinPattern(String pattern, String path) {
    return PATH_MATCHER.extractPathWithinPattern(pattern, path);
  }

  /**
   * Given a pattern and a full path, extract the URI template variables. URI template
   * variables are expressed through curly brackets ('{' and '}').
   * <p>For example: For pattern "/hotels/{hotel}" and path "/hotels/1", this method will
   * return a map containing "hotel" &rarr; "1".
   * @param pattern the path pattern, possibly containing URI templates
   * @param path the full path to extract template variables from
   * @return a map, containing variable names as keys; variables values as values
   */
  public static Map<String, String> extractUriTemplateVariables(String pattern, String path) {
    return PATH_MATCHER.extractUriTemplateVariables(pattern, path);
  }

}
