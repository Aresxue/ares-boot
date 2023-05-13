package cn.ares.boot.util.crypt.spi;

/**
 * @author: Ares
 * @time: 2021-11-29 15:42:00
 * @description: The default salt value is obtained, which can be overridden by the outside world
 * @description: 默认的盐值获取，可由外界覆写
 * @version: JDK 1.8
 */
public interface DefaultSaltGetService {

  /**
   * @author: Ares
   * @description: 获取默认盐值
   * @time: 2021-11-29 16:02:00
   * @params: []
   * @return: java.lang.String 盐值
   */
  String getDefaultSalt();

}
