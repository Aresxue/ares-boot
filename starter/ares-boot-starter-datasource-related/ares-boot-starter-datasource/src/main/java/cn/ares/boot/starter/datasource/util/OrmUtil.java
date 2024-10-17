package cn.ares.boot.starter.datasource.util;

import static cn.ares.boot.util.common.constant.SymbolConstant.PER_CENT;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.ASTERISK;

import cn.ares.boot.util.common.StringUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;

/**
 * @author: Ares
 * @time: 2023-06-27 16:52:37
 * @description: Orm util
 * @version: JDK 1.8
 */
public class OrmUtil {

  /**
   * @author: Ares
   * @description: 根据值中是否有*决定走模糊还是精确查询
   * @time: 2023-06-27 17:03:38
   * @params: [abstractWrapper, condition, column, value] queryWrapper，条件，列，值
   */
  public static <T, R, Children extends AbstractWrapper<T, R, Children>> AbstractWrapper<T, R, Children> eqOrLike(
      AbstractWrapper<T, R, Children> abstractWrapper, boolean condition, R column, String value) {
    if (StringUtil.isNotBlank(value)) {
      if (value.contains(ASTERISK)) {
        // 将*转为%
        String sqlValue = StringUtil.replace(value, ASTERISK, PER_CENT);
        if (value.startsWith(ASTERISK)) {
          if (value.endsWith(ASTERISK)) {
            // 会在左右都拼上%，在解析和执行like查询时，一般会优化掉连续的%，所以多个%不会影响实际的查询性能
            // will be spliced on both sides with %, in parsing and executing like queries, continuous % will generally be optimized, so multiple % will not affect the actual query performance
            // *abc* -> %abc% -> %%abc%%
            return abstractWrapper.like(condition, column, sqlValue);
          } else {
            // 会在左拼上%，在解析和执行like查询时，一般会优化掉连续的%，所以多个%不会影响实际的查询性能
            // will be spliced on the left with %, in parsing and executing like queries, continuous % will generally be optimized, so multiple % will not affect the actual query performance
            // *abc -> %abc -> %%abc
            return abstractWrapper.likeLeft(condition, column, sqlValue);
          }
        } else if (value.endsWith(ASTERISK)) {
          // 会在右拼上%，在解析和执行like查询时，一般会优化掉连续的%，所以多个%不会影响实际的查询性能
          // will be spliced on the right with %, in parsing and executing like queries, continuous % will generally be optimized, so multiple % will not affect the actual query performance
          // abc* -> abc% -> abc%%
          return abstractWrapper.likeRight(condition, column, sqlValue);
        }
        // 会在右拼上%，可能会影响实际的查询性能
        // will be spliced on the right with %, which may affect the actual query performance
        // ab*c -> ab%c -> ab%c%
        return abstractWrapper.likeRight(condition, column, sqlValue);
      } else {
        return abstractWrapper.eq(condition, column, value);
      }
    }
    return abstractWrapper;
  }

  /**
   * @author: Ares
   * @description: 根据值中是否有*决定走模糊还是精确查询
   * @time: 2023-06-27 17:03:38
   * @params: [queryWrapper, column, value] queryWrapper，列，值
   */
  public static <T, R, Children extends AbstractWrapper<T, R, Children>> AbstractWrapper<T, R, Children> eqOrLike(
      AbstractWrapper<T, R, Children> abstractWrapper, R column, String value) {
    return eqOrLike(abstractWrapper, true, column, value);
  }

}
