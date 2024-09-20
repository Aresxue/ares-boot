package cn.ares.boot.util.compress;

import static cn.ares.boot.util.compress.constant.CompressAlgorithm.ZSTD;

import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.common.log.JdkLoggerUtil;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 * @author: Ares
 * @time: 2024-09-18 15:44:19
 * @description: LosslessCompressUtil test
 * @version: JDK 1.8
 */
public class LosslessCompressUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(LosslessCompressUtilTest.class);
  private static final LosslessCompress losslessCompress = LosslessCompressUtil.getInstance(ZSTD);


  @Test
  public void test() {
    byte[] src = StringUtil.random(10240).getBytes();
    JdkLoggerUtil.info(LOGGER, "原字节数组长度: " + src.length);
    byte[] compressBytes = losslessCompress.compress(src);
    JdkLoggerUtil.info(LOGGER, "压缩后字节数组长度: " + compressBytes.length);
    byte[] decompressBytes = losslessCompress.decompress(compressBytes);
    JdkLoggerUtil.info(LOGGER, "解压后字符串: " + new String(decompressBytes));
  }

}
