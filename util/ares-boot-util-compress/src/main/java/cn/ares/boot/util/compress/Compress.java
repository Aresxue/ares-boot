package cn.ares.boot.util.compress;

import java.nio.charset.Charset;

/**
 * @author: Ares
 * @time: 2022-10-19 10:36:23
 * @description: 压缩算法
 * @description: Compress algorithm
 * @version: JDK 1.8
 */
// TODO 压缩算法整合且支持注解在controller方法上使用gzip压缩算法
public interface Compress {

  /**
   * @author: Ares
   * @description: Compresses an array of bytes with the specified compression level
   * @description: 以指定压缩级别压缩字节数组
   * @time: 2022-10-19 17:14:07
   * @params: [src, compressionLevel] 源字节数组，压缩级别
   * @return: byte[] 压缩后字节数组
   */
  byte[] compressImpl(byte[] src, int compressionLevel);

  /**
   * @author: Ares
   * @description: 以指定压缩级别压缩字符串
   * @description: Compresses the string with the specified compression level
   * @time: 2024-09-18 15:37:37
   * @params: [src, compressionLevel] 源字符串，压缩级别
   * @return: byte[] 压缩后字节数组
   */
  default byte[] compress(byte[] src, int compressionLevel) {
    return compressImpl(src, compressionLevel);
  }

  /**
   * @author: Ares
   * @description: Compresses a file to the specified file path at the specified compression level
   * @description: 以指定压缩级别压缩文件到指定文件路径
   * @time: 2022-10-19 17:16:27
   * @params: [originFilePath, compressFilePath, compressionLevel] 原始文件，压缩后文件，压缩级别
   * @return: long 压缩字节数
   */
  long compressFile(String originFilePath, String compressFilePath, int compressionLevel);

  /**
   * @author: Ares
   * @description: Decompress the byte array
   * @description: 解压字节数组
   * @time: 2022-10-19 17:18:38
   * @params: [compressBytes] 压缩后字节数组
   * @return: byte[] 解压后字节数组
   */
  byte[] decompress(byte[] compressBytes);

  /**
   * @author: Ares
   * @description: Decompress the file to the specified file path
   * @description: 解压文件到指定文件路径
   * @time: 2022-10-19 17:21:12
   * @params: [compressFilePath, decompressFilePath] 压缩后文件路径，解压文件路径
   */
  void decompressFile(String compressFilePath, String decompressFilePath);

  /**
   * @author: Ares
   * @description: 获取默认压缩级别
   * @time: 2022-10-19 17:44:09
   * @return: int 默认压缩级别
   */
  default int getDefaultCompressionLevel() {
    return 0;
  }

  /**
   * @author: Ares
   * @description: 以默认压缩级别压缩字节数组
   * @description: Compresses the byte array with the default compression level
   * @time: 2024-09-18 15:38:25
   * @params: [src] 源字符串
   * @return: byte[] 压缩后字节数组
   */
  default byte[] compress(byte[] src) {
    return compress(src, getDefaultCompressionLevel());
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
  default long compressFile(String originFilePath, String compressFilePath) {
    return compressFile(originFilePath, compressFilePath, getDefaultCompressionLevel());
  }

  /**
   * @author: Ares
   * @description: 以默认压缩级别压缩字符串
   * @description: Compresses the string with the default compression level
   * @time: 2024-09-18 15:39:14
   * @params: [src] 源字符串
   * @return: java.lang.String 压缩后字符串
   */
  default String compress(String src) {
    byte[] result = null == src ? null : src.getBytes(Charset.defaultCharset());
    if (null == result) {
      return null;
    }
    return new String(result, Charset.defaultCharset());
  }

}
