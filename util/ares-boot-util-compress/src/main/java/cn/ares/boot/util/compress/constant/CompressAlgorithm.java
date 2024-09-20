package cn.ares.boot.util.compress.constant;

import cn.ares.boot.util.common.MapUtil;
import java.util.Map;
import java.util.Optional;

/**
 * @author: Ares
 * @time: 2022-10-10 17:47:35
 * @description: Compress algorithm
 * @description: 压缩算法
 * @version: JDK 1.8
 */
public enum CompressAlgorithm {
  /*
  detail
   */
  ZIP("zip", false),
  GZIP("gzip", false),
  ZSTD("zstd", false),
  LZ4("lz4", false),
  SNAPPY("snappy", false),
  ;

  private final String name;
  /**
   * Loss data 是否损失数据
   */
  private final boolean loss;

  private static final Map<String, CompressAlgorithm> CACHED = MapUtil.newMap(values().length);

  static {
    for (CompressAlgorithm algorithm : values()) {
      CACHED.put(algorithm.getName(), algorithm);
    }
  }

  CompressAlgorithm(String name, boolean loss) {
    this.name = name;
    this.loss = loss;
  }

  public String getName() {
    return name;
  }

  public boolean isLoss() {
    return loss;
  }

  public static CompressAlgorithm getCompressAlgorithm(String compressAlgorithmName) {
    return Optional.ofNullable(CACHED.get(compressAlgorithmName))
        .orElseThrow(() -> new RuntimeException(compressAlgorithmName + " not impl"));
  }

}
