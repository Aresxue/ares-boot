package cn.ares.boot.util.compress;

import static cn.ares.boot.util.compress.constant.CompressAlgorithm.GZIP;
import static cn.ares.boot.util.compress.constant.CompressAlgorithm.ZSTD;

import cn.ares.boot.util.common.MapUtil;
import cn.ares.boot.util.compress.constant.CompressAlgorithm;
import cn.ares.boot.util.compress.impl.Gzip;
import cn.ares.boot.util.compress.impl.Zstd;
import java.util.Map;

/**
 * @author: Ares
 * @time: 2024-09-18 15:04:38
 * @description: 无损压缩算法工具
 * @description: Lossless compress algorithm util
 * @version: JDK 1.8
 */
public class LosslessCompressUtil {

  private static final String LOSS_LESS_COMPRESS_IMPL = "ares.compress.lossless.impl";
  private static final Map<CompressAlgorithm, LosslessCompress> LOSSLESS_COMPRESS_MAP = MapUtil.newConcurrentMap();

  private static LosslessCompress losslessCompress;

  static {
    LOSSLESS_COMPRESS_MAP.put(GZIP, Gzip.getInstance());
    LOSSLESS_COMPRESS_MAP.put(ZSTD, Zstd.getInstance());

    setLosslessCompressInstance();
  }

  private static void setLosslessCompressInstance() {
    String compressAlgorithmName = System.getProperty(LOSS_LESS_COMPRESS_IMPL, ZSTD.getName());
    CompressAlgorithm compressAlgorithm = CompressAlgorithm.getCompressAlgorithm(
        compressAlgorithmName);
    losslessCompress = getInstance(compressAlgorithm);
  }

  /**
   * @author: Ares
   * @description: 以指定压缩级别压缩字符串
   * @description: Compresses the string with the specified compression level
   * @time: 2024-09-18 15:37:37
   * @params: [src, compressionLevel] 源字符串，压缩级别
   * @return: byte[] 压缩后字节数组
   */
  public static byte[] compress(byte[] src, int compressionLevel) {
    return losslessCompress.compress(src, compressionLevel);
  }

  /**
   * @author: Ares
   * @description: Compresses a file to the specified file path at the specified compression level
   * @description: 以指定压缩级别压缩文件到指定文件路径
   * @time: 2022-10-19 17:16:27
   * @params: [originFilePath, compressFilePath, compressionLevel] 原始文件，压缩后文件，压缩级别
   * @return: long 压缩字节数
   */
  public static long compressFile(String originFilePath, String compressFilePath,
      int compressionLevel) {
    return losslessCompress.compressFile(originFilePath, compressFilePath, compressionLevel);
  }

  /**
   * @author: Ares
   * @description: Decompress the byte array
   * @description: 解压字节数组
   * @time: 2022-10-19 17:18:38
   * @params: [compressBytes] 压缩后字节数组
   * @return: byte[] 解压后字节数组
   */
  public static byte[] decompress(byte[] compressBytes) {
    return losslessCompress.decompress(compressBytes);
  }

  /**
   * @author: Ares
   * @description: Decompress the file to the specified file path
   * @description: 解压文件到指定文件路径
   * @time: 2022-10-19 17:21:12
   * @params: [compressFilePath, decompressFilePath] 压缩后文件路径，解压文件路径
   */
  public static void decompressFile(String compressFilePath, String decompressFilePath) {
    losslessCompress.decompressFile(compressFilePath, decompressFilePath);
  }

  /**
   * @author: Ares
   * @description: 以默认压缩级别压缩字节数组
   * @description: Compresses the byte array with the default compression level
   * @time: 2024-09-18 15:38:25
   * @params: [src] 源字符串
   * @return: byte[] 压缩后字节数组
   */
  public static byte[] compress(byte[] src) {
    return losslessCompress.compress(src);
  }

  /**
   * @author: Ares
   * @description: 以默认压缩级别压缩文件输出到指定文件路径
   * @description: Compresses the file to the specified file path with the default compression
   * level
   * @time: 2024-09-18 15:38:43
   * @params: [originFilePath, compressFilePath] 原始文件，压缩后文件
   * @return: long 压缩字节数
   */
  public static long compressFile(String originFilePath, String compressFilePath) {
    return losslessCompress.compressFile(originFilePath, compressFilePath);
  }

  /**
   * @author: Ares
   * @description: 以默认压缩级别压缩字符串
   * @description: Compresses the string with the default compression level
   * @time: 2024-09-18 15:39:14
   * @params: [src] 源字符串
   * @return: java.lang.String 压缩后字符串
   */
  public String compress(String src) {
    return losslessCompress.compress(src);
  }

  /**
   * @author: Ares
   * @description: 获取指定的无损压缩算法实例
   * @description: Get the specified lossless compress algorithm instance
   * @time: 2024-09-18 15:12:25
   * @params: [compressAlgorithm] 压缩算法
   * @return: cn.ares.boot.util.compress.LosslessCompress 无损压缩算法实例
   */
  public static LosslessCompress getInstance(CompressAlgorithm compressAlgorithm) {
    if (null == compressAlgorithm) {
      throw new RuntimeException("Compress algorithm is null");
    }
    if (compressAlgorithm.isLoss()) {
      throw new RuntimeException("Current compress algorithm is loss algorithm");
    }
    return LOSSLESS_COMPRESS_MAP.get(compressAlgorithm);
  }

  public static boolean registerAlgorithm(CompressAlgorithm algorithm, LosslessCompress compress) {
    if (null == algorithm) {
      return false;
    }
    LOSSLESS_COMPRESS_MAP.putIfAbsent(algorithm, compress);
    setLosslessCompressInstance();
    return true;
  }

  public static void setLosslessCompress(LosslessCompress losslessCompress) {
    LosslessCompressUtil.losslessCompress = losslessCompress;
  }

}
