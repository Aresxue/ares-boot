package cn.ares.boot.starter.datasource.util;

import static cn.ares.boot.util.common.constant.SymbolConstant.ASTERISK;

import cn.ares.boot.util.common.StringUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import java.util.Optional;

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
  public static <T, R, Children extends AbstractWrapper<T, R, Children>> void eqOrLike(
      AbstractWrapper<T, R, Children> abstractWrapper, boolean condition, R column, String value) {
    value = Optional.ofNullable(value).map(String::trim).orElse(null);
    if (StringUtil.isNotBlank(value)) {
      if (value.startsWith(ASTERISK) && value.endsWith(ASTERISK)) {
        abstractWrapper.like(condition, column, StringUtil.trim(value, ASTERISK));
      } else if (value.startsWith(ASTERISK)) {
        abstractWrapper.likeLeft(condition, column, StringUtil.trim(value, ASTERISK));
      } else if (value.endsWith(ASTERISK)) {
        abstractWrapper.likeRight(condition, column, StringUtil.trim(value, ASTERISK));
      } else {
        abstractWrapper.eq(condition, column, value);
      }
    }
  }


  /**
   * @author: Ares
   * @description: 根据值中是否有*决定走模糊还是精确查询
   * @time: 2023-06-27 17:03:38
   * @params: [queryWrapper, column, value] queryWrapper，列，值
   */
  public static <T, R, Children extends AbstractWrapper<T, R, Children>> void eqOrLike(
      AbstractWrapper<T, R, Children> abstractWrapper, R column, String value) {
    eqOrLike(abstractWrapper, true, column, value);
  }

}
