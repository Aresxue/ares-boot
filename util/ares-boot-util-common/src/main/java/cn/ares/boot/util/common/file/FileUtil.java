package cn.ares.boot.util.common.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author: Ares
 * @time: 2023-11-16 14:01:51
 * @description: 文件工具
 * @description: File util
 * @version: JDK 1.8
 */
public class FileUtil {


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

}