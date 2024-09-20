package cn.ares.boot.util.compress.impl;

import cn.ares.boot.util.compress.LosslessCompress;
import cn.ares.boot.util.compress.exception.IOExceptionWrapper;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * @author: Ares
 * @time: 2024-09-18 16:52:29
 * @description: Gzip实现类
 * @description: Gzip implementation class
 * @version: JDK 1.8
 */
public class Gzip extends LosslessCompress {

  private static final int COMPRESS_BUFFER_SIZE = 8 * 1024;
  private static final int DECOMPRESS_BUFFER_SIZE = 8 * 1024;

  @Override
  public byte[] compressImpl(byte[] src, int compressionLevel) {
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(src);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        GzipCompressorOutputStream outputStream = new GzipCompressorOutputStream(stream)) {
      byte[] buffer = new byte[COMPRESS_BUFFER_SIZE];
      int len;
      while ((len = byteArrayInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, len);
      }
      return stream.toByteArray();
    } catch (IOException ioException) {
      throw new IOExceptionWrapper(ioException);
    }
  }

  @Override
  public long compressFile(String originFilePath, String compressFilePath, int compressionLevel) {
    long totalBytesRead = 0;
    try (FileInputStream fileInputStream = new FileInputStream(originFilePath);
        FileOutputStream fileOutputStream = new FileOutputStream(compressFilePath);
        GZIPOutputStream outputStream = new GZIPOutputStream(fileOutputStream)) {
      byte[] buffer = new byte[COMPRESS_BUFFER_SIZE];
      int len;
      while ((len = fileInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, len);
        totalBytesRead += len;
      }
    } catch (IOException ioException) {
      throw new IOExceptionWrapper(ioException);
    }
    return totalBytesRead;
  }

  @Override
  public byte[] decompress(byte[] compressBytes) {
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressBytes);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        GZIPInputStream inputStream = new GZIPInputStream(byteArrayInputStream)) {
      byte[] buffer = new byte[DECOMPRESS_BUFFER_SIZE];
      int len;
      while ((len = inputStream.read(buffer)) != -1) {
        stream.write(buffer, 0, len);
      }
      return stream.toByteArray();
    } catch (IOException ioException) {
      throw new IOExceptionWrapper(ioException);
    }
  }

  @Override
  public void decompressFile(String compressFilePath, String decompressFilePath) {
    try (FileInputStream fileInputStream = new FileInputStream(compressFilePath);
        GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
        FileOutputStream outputStream = new FileOutputStream(decompressFilePath)) {
      byte[] buffer = new byte[DECOMPRESS_BUFFER_SIZE];
      int len;
      while ((len = gzipInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, len);
      }
    } catch (IOException ioException) {
      throw new IOExceptionWrapper(ioException);
    }
  }

  private static class LazyHolder {

    private static final Gzip INSTANCE = new Gzip();
  }

  private Gzip() {
  }

  public static Gzip getInstance() {
    return Gzip.LazyHolder.INSTANCE;
  }

}
