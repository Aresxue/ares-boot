package cn.ares.boot.util.crypt.constant;

import cn.ares.boot.util.common.MapUtil;
import java.util.Map;
import java.util.Optional;

/**
 * @author: Ares
 * @time: 2021-11-22 21:02:00
 * @description: Crypt algorithm
 * @version: JDK 1.8
 */
public enum CryptAlgorithm {
  /**
   * Crypt algorithm
   */
  AES256("aes256", true),
  MD5("md5", false),
  SHA1("sha1", false),
  SHA256("sha256", false),
  SM3("sm3", false),
  SM4("sm4", true),
  DES("des", true),
  RSA("rsa", true),
  HMAC_SM3("hmac-sm3", false);

  private static final Map<String, CryptAlgorithm> CACHED = MapUtil.newMap(values().length);

  static {
    for (CryptAlgorithm algorithm : values()) {
      CACHED.put(algorithm.getName(), algorithm);
    }
  }

  private final String name;
  private final boolean reverse;

  CryptAlgorithm(String name, boolean reverse) {
    this.name = name;
    this.reverse = reverse;
  }

  public String getName() {
    return name;
  }

  public boolean isReverse() {
    return reverse;
  }

  public static CryptAlgorithm getCryptAlgorithm(String cryptAlgorithmName) {
    return Optional.ofNullable(CACHED.get(cryptAlgorithmName))
        .orElseThrow(() -> new RuntimeException(cryptAlgorithmName + " not impl"));
  }

}
