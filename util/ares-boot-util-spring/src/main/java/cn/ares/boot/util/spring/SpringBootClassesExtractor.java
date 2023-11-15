package cn.ares.boot.util.spring;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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
   * @description: 提取spring boot fat jar下BOOT-INF/classes/下的class文件
   * @time: 2023-11-15 10:52:34
   * @params: [jarPath, classesPath] fat jar路径，输出路径
   * @return: void
   */
  public static void extractClasses(String jarPath, String classesPath) throws IOException {
    try (JarFile jarFile = new JarFile(jarPath)) {
      jarFile.stream().filter(entry -> entry.getName().startsWith(REPACKAGED_CLASSES_LOCATION))
          .forEach(entry -> {
            try {
              extractFile(jarFile, entry, classesPath);
            } catch (Exception e) {
              LOGGER.warn("extract class: {} exception: ", entry.getName(), e);
            }
          });
    }
  }

  private static void extractFile(JarFile jarFile, JarEntry entry, String outputDir)
      throws IOException {
    File outputFile = new File(outputDir,
        entry.getName().replace(REPACKAGED_CLASSES_LOCATION, ""));
    if (entry.isDirectory()) {
      outputFile.mkdirs();
    } else {
      try (InputStream inputStream = jarFile.getInputStream(
          entry); FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
          fileOutputStream.write(buffer, 0, bytesRead);
        }
      }
    }
  }

}
