package cn.ares.boot.util.ognl;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import ognl.DefaultClassResolver;
import ognl.DefaultTypeConverter;
import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 * @author: Ares
 * @time: 2020-04-16 10:26:00
 * @description: Ognl util, parse expression with object
 * @description: ognl工具，根据对象解析表达式
 * @version: JDK 1.8
 */
public class OgnlUtil {

  private static final String OPEN_TOKEN = "${";
  private static final String CLOSE_TOKEN = "}";
  private static final String EMPTY = "";

  private static final Map<String, Object> EXPRESSION_CACHE = new ConcurrentHashMap<>();

  /**
   * @author: Ares
   * @description: get value by parsing $ expression and param
   * @description: 通过解析$表达式和参数获取值
   * @time: 2020-04-16 10:27:00
   * @params: [expression, param] 表达式，参数
   * @return: java.lang.String out 出参
   */
  public static String parse(String expressionText, Object param) throws OgnlException {
    return parse(expressionText, OPEN_TOKEN, CLOSE_TOKEN, param);
  }

  /**
   * @author: Ares
   * @description: get value by parsing expression and param with token
   * @description: 根据指定token通过解析表达式和参数获取值
   * @time: 2022-05-18 18:20:47
   * @params: [expression, openToken, closeToken, param] 表达式，开始token，结束token，参数
   * @return: java.lang.String out 出参
   */
  public static String parse(String expressionText, String openToken, String closeToken, Object param)
      throws OgnlException {
    return parse(expressionText, openToken, EMPTY, closeToken, EMPTY, param);
  }

  /**
   * @author: Ares
   * @description: get value by parsing expression and param with token, prefix and suffix
   * @description: 根据指定token通过解析表达式和参数获取值
   * @time: 2020-04-16 10:28:00
   * @params: [expressionText, openToken, openPrefix, closeToken, openSuffix, param]
   * 表达式，开始token，开始前缀，结束token，开始后缀，参数
   * @return: java.lang.String out 出参
   */
  public static String parse(String expressionText, String openToken, String openPrefix, String closeToken,
      String openSuffix, Object param) throws OgnlException {
    if (expressionText != null && !expressionText.isEmpty()) {
      int start = expressionText.indexOf(openToken);
      if (start == -1) {
        return expressionText;
      } else {
        char[] src = expressionText.toCharArray();
        int offset = 0;
        StringBuilder builder = new StringBuilder();

        for (StringBuilder expression = null; start > -1; start = expressionText.indexOf(openToken, offset)) {
          if (start > 0 && src[start - 1] == '\\') {
            builder.append(src, offset, start - offset - 1).append(openToken);
            offset = start + openToken.length();
          } else {
            if (expression == null) {
              expression = new StringBuilder();
            } else {
              expression.setLength(0);
            }

            builder.append(src, offset, start - offset);
            offset = start + openToken.length();

            int end;
            for (end = expressionText.indexOf(closeToken, offset); end > -1;
                end = expressionText.indexOf(closeToken, offset)) {
              if (end <= offset || src[end - 1] != '\\') {
                expression.append(src, offset, end - offset);
                break;
              }

              expression.append(src, offset, end - offset - 1).append(closeToken);
              offset = end + closeToken.length();
            }

            if (end == -1) {
              builder.append(src, start, src.length - start);
              offset = src.length;
            } else {
              String value = openPrefix + handleToken(expression.toString(), param) + openSuffix;
              builder.append(value);
              offset = end + closeToken.length();
            }
          }
        }

        if (offset < src.length) {
          builder.append(src, offset, src.length - offset);
        }

        return builder.toString();
      }
    } else {
      return EMPTY;
    }
  }

  public static String handleToken(String content, Object param) throws OgnlException {
    // build OgnlContext object
    OgnlContext context = (OgnlContext) Ognl
        .createDefaultContext(param, new DefaultMemberAccess(true), new DefaultClassResolver(),
            new DefaultTypeConverter());
    // set root
    context.setRoot(param);

    Object expression = parseExpression(content);
    Object value = Ognl.getValue(expression, context, param);
    return null == value ? EMPTY : String.valueOf(value);
  }


  private static Object parseExpression(String expression) throws OgnlException {
    Object node = EXPRESSION_CACHE.get(expression);
    if (null == node) {
      node = Ognl.parseExpression(expression);
      EXPRESSION_CACHE.put(expression, node);
    }

    return node;
  }

  static class DefaultMemberAccess implements MemberAccess {

    public boolean allowPrivateAccess;
    public boolean allowProtectedAccess;
    public boolean allowPackageProtectedAccess;

    /*===================================================================
        Constructors
      ===================================================================*/
    public DefaultMemberAccess(boolean allowAllAccess) {
      this(allowAllAccess, allowAllAccess, allowAllAccess);
    }

    public DefaultMemberAccess(boolean allowPrivateAccess, boolean allowProtectedAccess,
        boolean allowPackageProtectedAccess) {
      super();
      this.allowPrivateAccess = allowPrivateAccess;
      this.allowProtectedAccess = allowProtectedAccess;
      this.allowPackageProtectedAccess = allowPackageProtectedAccess;
    }

    /*===================================================================
        Public methods
      ===================================================================*/
    public boolean getAllowPrivateAccess() {
      return allowPrivateAccess;
    }

    public boolean getAllowProtectedAccess() {
      return allowProtectedAccess;
    }

    public boolean getAllowPackageProtectedAccess() {
      return allowPackageProtectedAccess;
    }

    /*===================================================================
        MemberAccess interface
      ===================================================================*/
    @Override
    public Object setup(Map context, Object target, Member member, String propertyName) {
      Object result = null;

      if (isAccessible(context, target, member, propertyName)) {
        AccessibleObject accessible = (AccessibleObject) member;

        if (!accessible.isAccessible()) {
          result = Boolean.FALSE;
          accessible.setAccessible(true);
        }
      }
      return result;
    }

    @Override
    public void restore(Map context, Object target, Member member, String propertyName,
        Object state) {
      if (state != null) {
        ((AccessibleObject) member).setAccessible((Boolean) state);
      }
    }

    /**
     * Returns true if the given member is accessible or can be made accessible by this object.
     */
    @Override
    public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
      int modifiers = member.getModifiers();
      boolean result = Modifier.isPublic(modifiers);

      if (!result) {
        if (Modifier.isPrivate(modifiers)) {
          result = getAllowPrivateAccess();
        } else {
          if (Modifier.isProtected(modifiers)) {
            result = getAllowProtectedAccess();
          } else {
            result = getAllowPackageProtectedAccess();
          }
        }
      }
      return result;
    }
  }

}
