package cn.ares.boot.util.compress.impl;

import cn.ares.boot.util.compress.LosslessCompress;
import cn.ares.boot.util.compress.exception.IOExceptionWrapper;
import com.github.luben.zstd.ZstdInputStream;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author: Ares
 * @time: 2024-09-18 16:34:37
 * @description: Zstd实现类
 * @description: Zstd implementation class
 * @version: JDK 1.8
 */
public class Zstd extends LosslessCompress {

  private static final int COMPRESS_BYTE_BATCH_SIZE = 8 * 1024 * 1024;
  private static final int DEFAULT_COMPRESSION_LEVEL = 3;
  private static final String READ_MODE = "r";
  private static final String READ_WRITE_MODE = "rw";

  @Override
  public byte[] compressImpl(byte[] src, int compressionLevel) {
    return com.github.luben.zstd.Zstd.compress(src, compressionLevel);
  }

  @Override
  public long compressFile(String originFilePath, String compressFilePath, int compressionLevel) {
    try {
      File file = new File(originFilePath);
      File compressFile = new File(compressFilePath);
      long numBytes = 0L;
      ByteBuffer inBuffer = ByteBuffer.allocateDirect(COMPRESS_BYTE_BATCH_SIZE);
      ByteBuffer compressedBuffer = ByteBuffer.allocateDirect(COMPRESS_BYTE_BATCH_SIZE);
      try (RandomAccessFile inputFile = new RandomAccessFile(file, READ_MODE);
          RandomAccessFile outFile = new RandomAccessFile(compressFile, READ_WRITE_MODE);
          FileChannel inChannel = inputFile.getChannel(); FileChannel outChannel = outFile.getChannel()) {
        inBuffer.clear();
        while (inChannel.read(inBuffer) > 0) {
          inBuffer.flip();
          compressedBuffer.clear();
          // 将inBuffer的0-inBuffer.limit()压缩到compressedBuffer的0-compressedBuffer.capacity()
          long compressedSize = com.github.luben.zstd.Zstd.compressDirectByteBuffer(
              compressedBuffer, 0, compressedBuffer.capacity(), inBuffer, 0, inBuffer.limit(),
              compressionLevel);
          numBytes += compressedSize;
          compressedBuffer.position((int) compressedSize);
          compressedBuffer.flip();
          outChannel.write(compressedBuffer);
          inBuffer.clear();
        }
      }

      return numBytes;
    } catch (IOException ioException) {
      throw new IOExceptionWrapper(ioException);
    }
  }

  @Override
  public byte[] decompress(byte[] compressBytes) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressBytes)) {
      decompress(inputStream, outputStream);
      return outputStream.toByteArray();
    } catch (IOException ioException) {
      throw new IOExceptionWrapper(ioException);
    }
  }

  private static void decompress(InputStream inputStream, OutputStream outputStream)
      throws IOException {
    byte[] buffer = new byte[COMPRESS_BYTE_BATCH_SIZE];

    try (ZstdInputStream inStream = new ZstdInputStream(inputStream)) {
      while (true) {
        // zs中重写了read方法，该方法包含解压过程，将0-buffer.length读入buffer
        int count = inStream.read(buffer, 0, buffer.length);
        if (count == -1) {
          break;
        }
        // 将buffer中的0-count写入文件输出流
        outputStream.write(buffer, 0, count);
      }
      outputStream.flush();
    }
  }

  @Override
  public void decompressFile(String compressFilePath, String decompressFilePath) {
    File compressFile = new File(compressFilePath);

    try (FileOutputStream outputStream = new FileOutputStream(decompressFilePath)) {
      decompress(Files.newInputStream(Paths.get(compressFile.getPath())), outputStream);
    } catch (IOException ioException) {
      throw new IOExceptionWrapper(ioException);
    }
  }

  @Override
  public int getDefaultCompressionLevel() {
    return DEFAULT_COMPRESSION_LEVEL;
  }

  private static class LazyHolder {

    private static final Zstd INSTANCE = new Zstd();
  }

  private Zstd() {
  }

  public static Zstd getInstance() {
    return Zstd.LazyHolder.INSTANCE;
  }

}
