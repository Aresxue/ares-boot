package cn.ares.boot.util.spring;

import cn.ares.boot.util.common.IoUtil;
import cn.ares.boot.util.common.StringUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.jar.JarFile;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2023-11-15 10:34:49
 * @description: 提取spring boot fat jar下BOOT-INF/classes/下的class文件
 * @description: Extract the class file under boot-INF /classes/ in the spring boot fat jar
 * @version: JDK 1.8
 */
public class SpringBootClassesExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootClassesExtractor.class);

  /**
   * spring boot fat jar下BOOT-INF/classes/下的class文件 class file under boot-INF /classes/ in the
   * spring boot fat jar
   */
  private static final String REPACKAGED_CLASSES_LOCATION = "BOOT-INF/classes/";

  /**
   * @author: Ares
   * @description: 提取spring boot fat jar下BOOT-INF/classes/下的class文件输出到文件夹
   * @description: Extract the class file under boot-INF /classes/ in spring boot fat jar and output
   * it to the folder
   * @time: 2023-11-15 10:52:34
   * @params: [jarPath, classesDir] fat jar路径，输出文件夹路径
   * @return: void
   */
  public static void extractClasses(String jarPath, String classesDir) throws IOException {
    try (JarFile jarFile = new JarFile(jarPath)) {
      jarFile.stream().filter(entry -> entry.getName().startsWith(REPACKAGED_CLASSES_LOCATION))
          .forEach(entry -> {
            try {
              String relativePath = entry.getName().replace(REPACKAGED_CLASSES_LOCATION, "");
              File outputFile = new File(classesDir, relativePath);
              if (entry.isDirectory()) {
                outputFile.mkdirs();
              } else {
                try (InputStream inputStream = jarFile.getInputStream(
                    entry); FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                  IoUtil.copy(inputStream, fileOutputStream);
                }
              }
            } catch (Exception e) {
              LOGGER.warn("extract file: {} exception: ", entry.getName(), e);
            }
          });
    }
  }

  /**
   * @author: Ares
   * @description: 提取spring boot fat jar下BOOT-INF/classes/下的class文件并输出成gzip文件
   * @description: Extract the class file under boot-INF /classes/ in spring boot fat jar and output
   * it as a gzip file
   * @time: 2023-11-16 10:11:34
   * @params: [jarPath, classesGzPath] fat jar路径，输出gzip文件路径
   * @return: void
   */
  public static void extractClassesGzip(String jarPath, String classesGzPath) throws IOException {
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(new GZIPOutputStream(
        new GZIPOutputStream(Files.newOutputStream(Paths.get(classesGzPath)))))) {
      try (JarFile jarFile = new JarFile(jarPath)) {
        jarFile.stream().filter(entry -> entry.getName().startsWith(REPACKAGED_CLASSES_LOCATION))
            .forEach(entry -> {
              try {
                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                  String relativePath = entry.getName().replace(REPACKAGED_CLASSES_LOCATION, "");
                  if (StringUtil.isNotBlank(relativePath)) {
                    zipOutputStream.putNextEntry(new ZipEntry(relativePath));
                    IoUtil.copy(inputStream, zipOutputStream);
                    zipOutputStream.closeEntry();
                  }
                }
              } catch (Exception e) {
                LOGGER.warn("extract file: {} exception: ", entry.getName(), e);
              }
            });
      }
    }
  }

}
