package cn.ares.boot.util.crypt.spi;

/**
 * @author: Ares
 * @time: 2021-11-29 15:42:00
 * @description: The default public and private key acquisition can be overridden by the outside world
 * @description: 默认的公私钥获取，可由外界覆写
 * @version: JDK 1.8
 */
public interface DefaultKeyGetService {

  /**
   * @author: Ares
   * @description: 获取默认公钥
   * @time: 2021-11-29 16:02:00
   * @params: []
   * @return: java.lang.String 公钥
   */
  String getDefaultPublicKey();


  /**
   * @author: Ares
   * @description: 获取默认私钥
   * @time: 2021-11-29 16:02:00
   * @params: []
   * @return: java.lang.String 公钥
   */
  String getDefaultPrivateKey();

}
