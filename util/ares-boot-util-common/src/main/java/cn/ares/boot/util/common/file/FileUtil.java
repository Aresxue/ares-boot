package cn.ares.boot.util.common.file;

import cn.ares.boot.util.common.IoUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author: Ares
 * @time: 2023-11-16 14:01:51
 * @description: 文件工具
 * @description: File util
 * @version: JDK 1.8
 */
public class FileUtil {

  private static final String TEMP_PATH = System.getProperty("java.io.tmpdir");

  /**
   * @author: Ares
   * @description: 文件是否存在
   * @description: Whether the file exists
   * @time: 2023-11-16 17:25:28
   * @params: [filePath] 文件路径
   * @return: boolean 是否存在
   */
  public static boolean exists(String filePath) {
    return new File(filePath).exists();
  }

  /**
   * @author: Ares
   * @description: 文件在指定时间内没有更新
   * @description: The file was not updated within the specified time
   * @time: 2023-11-16 17:27:09
   * @params: [file, duration] 文件，时间间隔
   * @return: boolean 是否更新
   */
  public static boolean notModified(File file, Duration duration) {
    return System.currentTimeMillis() - file.lastModified() > duration.toMillis();
  }

  /**
   * @author: Ares
   * @description: 将输入流写入临时文件
   * @time: 2023-11-16 20:43:37
   * @params: [inputStream, relativePath] 输入流，临时文件
   * @return: java.io.File 临时文件
   */
  public static File writeTempFile(InputStream inputStream, String relativePath)
      throws IOException {
    File tempFile = new File(getTempDirectory(), relativePath);
    if (!tempFile.getParentFile().exists()) {
      createParentDirectories(tempFile);
    }
    copyInputStreamToFile(inputStream, tempFile);
    return tempFile;
  }


  /**
   * Writes a byte array to a file creating the file if it does not exist.
   * <p>
   * NOTE: As from v1.3, the parent directories of the file will be created if they do not exist.
   * </p>
   *
   * @param file the file to write to
   * @param data the content to write to the file
   * @throws IOException in case of an I/O error
   */
  public static void writeByteArrayToFile(final File file, final byte[] data) throws IOException {
    writeByteArrayToFile(file, data, false);
  }

  /**
   * Writes a byte array to a file creating the file if it does not exist.
   *
   * @param file   the file to write to
   * @param data   the content to write to the file
   * @param append if {@code true}, then bytes will be added to the end of the file rather than
   *               overwriting
   * @throws IOException in case of an I/O error
   */
  public static void writeByteArrayToFile(final File file, final byte[] data, final boolean append)
      throws IOException {
    writeByteArrayToFile(file, data, 0, data.length, append);
  }

  /**
   * Writes {@code len} bytes from the specified byte array starting at offset {@code off} to a
   * file, creating the file if it does not exist.
   *
   * @param file   the file to write to
   * @param data   the content to write to the file
   * @param off    the start offset in the data
   * @param len    the number of bytes to write
   * @param append if {@code true}, then bytes will be added to the end of the file rather than
   *               overwriting
   * @throws IOException in case of an I/O error
   */
  public static void writeByteArrayToFile(final File file, final byte[] data, final int off,
      final int len,
      final boolean append) throws IOException {
    try (OutputStream out = openOutputStream(file, append)) {
      out.write(data, off, len);
    }
  }

  /**
   * Opens a {@link FileOutputStream} for the specified file, checking and creating the parent
   * directory if it does not exist.
   * <p>
   * At the end of the method either the stream will be successfully opened, or an exception will
   * have been thrown.
   * </p>
   * <p>
   * The parent directory will be created if it does not exist. The file will be created if it does
   * not exist. An exception is thrown if the file object exists but is a directory. An exception is
   * thrown if the file exists but cannot be written to. An exception is thrown if the parent
   * directory cannot be created.
   * </p>
   *
   * @param file the file to open for output, must not be {@code null}
   * @return a new {@link FileOutputStream} for the specified file
   * @throws NullPointerException     if the file object is {@code null}.
   * @throws IllegalArgumentException if the file object is a directory
   * @throws IllegalArgumentException if the file is not writable.
   * @throws IOException              if the directories could not be created.
   */
  public static FileOutputStream openOutputStream(final File file) throws IOException {
    return openOutputStream(file, false);
  }

  /**
   * Opens a {@link FileOutputStream} for the specified file, checking and creating the parent
   * directory if it does not exist.
   * <p>
   * At the end of the method either the stream will be successfully opened, or an exception will
   * have been thrown.
   * </p>
   * <p>
   * The parent directory will be created if it does not exist. The file will be created if it does
   * not exist. An exception is thrown if the file object exists but is a directory. An exception is
   * thrown if the file exists but cannot be written to. An exception is thrown if the parent
   * directory cannot be created.
   * </p>
   *
   * @param file   the file to open for output, must not be {@code null}
   * @param append if {@code true}, then bytes will be added to the end of the file rather than
   *               overwriting
   * @return a new {@link FileOutputStream} for the specified file
   * @throws NullPointerException     if the file object is {@code null}.
   * @throws IllegalArgumentException if the file object is a directory
   * @throws IllegalArgumentException if the file is not writable.
   * @throws IOException              if the directories could not be created.
   */
  public static FileOutputStream openOutputStream(final File file, final boolean append)
      throws IOException {
    Objects.requireNonNull(file, "file");
    if (file.exists()) {
      requireFile(file, "file");
      requireCanWrite(file, "file");
    } else {
      createParentDirectories(file);
    }
    return new FileOutputStream(file, append);
  }

  /**
   * Requires that the given {@code File} is a file.
   *
   * @param file The {@code File} to check.
   * @param name The parameter name to use in the exception message.
   * @return the given file.
   * @throws NullPointerException     if the given {@code File} is {@code null}.
   * @throws IllegalArgumentException if the given {@code File} does not exist or is not a
   *                                  directory.
   */
  private static File requireFile(final File file, final String name) {
    Objects.requireNonNull(file, name);
    if (!file.isFile()) {
      throw new IllegalArgumentException("Parameter '" + name + "' is not a file: " + file);
    }
    return file;
  }

  /**
   * Throws an {@link IllegalArgumentException} if the file is not writable. This provides a more
   * precise exception message than plain access denied.
   *
   * @param file The file to test.
   * @param name The parameter name to use in the exception message.
   * @throws NullPointerException     if the given {@code File} is {@code null}.
   * @throws IllegalArgumentException if the file is not writable.
   */
  private static void requireCanWrite(final File file, final String name) {
    Objects.requireNonNull(file, "file");
    if (!file.canWrite()) {
      throw new IllegalArgumentException(
          "File parameter '" + name + " is not writable: '" + file + "'");
    }
  }

  /**
   * Creates all parent directories for a File object.
   *
   * @param file the File that may need parents, may be null.
   * @return The parent directory, or {@code null} if the given file does not name a parent
   * @throws IOException if the directory was not created along with all its parent directories.
   * @throws IOException if the given file object is not null and not a directory.
   */
  public static File createParentDirectories(final File file) throws IOException {
    return mkdirs(getParentFile(file));
  }

  /**
   * Calls {@link File#mkdirs()} and throws an exception on failure.
   *
   * @param directory the receiver for {@code mkdirs()}, may be null.
   * @return the given file, may be null.
   * @throws IOException       if the directory was not created along with all its parent
   *                           directories.
   * @throws IOException       if the given file object is not a directory.
   * @throws SecurityException See {@link File#mkdirs()}.
   * @see File#mkdirs()
   */
  private static File mkdirs(final File directory) throws IOException {
    if ((directory != null) && (!directory.mkdirs() && !directory.isDirectory())) {
      throw new IOException("Cannot create directory '" + directory + "'.");
    }
    return directory;
  }

  /**
   * Gets the parent of the given file. The given file may be bull and a file's parent may as well
   * be null.
   *
   * @param file The file to query.
   * @return The parent file or {@code null}.
   */
  private static File getParentFile(final File file) {
    return file == null ? null : file.getParentFile();
  }

  /**
   * Returns a {@link File} representing the system temporary directory.
   *
   * @return the system temporary directory.
   */
  public static File getTempDirectory() {
    return new File(TEMP_PATH);
  }

  /**
   * Copies bytes from an {@link InputStream} {@code source} to a file {@code destination}. The
   * directories up to {@code destination} will be created if they don't already exist.
   * {@code destination} will be overwritten if it already exists.
   * <p>
   * <em>The {@code source} stream is closed.</em>
   * </p>
   * <p>
   * See {@link #copyToFile(InputStream, File)} for a method that does not close the input stream.
   * </p>
   *
   * @param source      the {@code InputStream} to copy bytes from, must not be {@code null}, will
   *                    be closed
   * @param destination the non-directory {@code File} to write bytes to (possibly overwriting),
   *                    must not be {@code null}
   * @throws IOException if {@code destination} is a directory
   * @throws IOException if {@code destination} cannot be written
   * @throws IOException if {@code destination} needs creating but can't be
   * @throws IOException if an IO error occurs during copying
   */
  public static void copyInputStreamToFile(final InputStream source, final File destination)
      throws IOException {
    try (InputStream inputStream = source) {
      copyToFile(inputStream, destination);
    }
  }

  /**
   * Copies bytes from an {@link InputStream} source to a {@link File} destination. The directories
   * up to {@code destination} will be created if they don't already exist. {@code destination} will
   * be overwritten if it already exists. The {@code source} stream is left open, e.g. for use with
   * {@link java.util.zip.ZipInputStream ZipInputStream}. See
   * {@link #copyInputStreamToFile(InputStream, File)} for a method that closes the input stream.
   *
   * @param inputStream the {@code InputStream} to copy bytes from, must not be {@code null}
   * @param file        the non-directory {@code File} to write bytes to (possibly overwriting),
   *                    must not be {@code null}
   * @throws NullPointerException     if the InputStream is {@code null}.
   * @throws NullPointerException     if the File is {@code null}.
   * @throws IllegalArgumentException if the file object is a directory.
   * @throws IllegalArgumentException if the file is not writable.
   * @throws IOException              if the directories could not be created.
   * @since 2.5
   */
  public static void copyToFile(final InputStream inputStream, final File file) throws IOException {
    try (OutputStream out = openOutputStream(file)) {
      IoUtil.copy(inputStream, out);
    }
  }

  /**
   * @author: Ares
   * @description: 解压gzip文件夹
   * @description: Decompress gzip folder
   * @time: 2023-11-22 20:51:56
   * @params: [inputFilePath, outputFolder] gzip文件路径，输出目录
   * @return: void
   */
  public static void decompressGzipFolder(String inputFilePath, String outputFolder)
      throws IOException {
    try (FileInputStream fileInputStream = new FileInputStream(inputFilePath);
        GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
        ZipInputStream zipInputStream = new ZipInputStream(gzipInputStream)) {

      ZipEntry zipEntry = zipInputStream.getNextEntry();
      while (zipEntry != null) {
        String entryName = zipEntry.getName();
        String entryPath = outputFolder + File.separator + entryName;

        if (zipEntry.isDirectory()) {
          // 如果是文件夹，创建文件夹
          // If it is a folder, create a folder
          new File(entryPath).mkdirs();
        } else {
          // 如果是文件，创建文件并写入数据
          // If it is a file, create the file and write the data
          copyInputStreamToFile(zipInputStream, new File(entryPath));
        }

        zipEntry = zipInputStream.getNextEntry();
      }
    }
  }

}