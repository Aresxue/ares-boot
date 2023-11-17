package cn.ares.boot.util.spring;

import static cn.ares.boot.util.common.constant.StringConstant.CLASS_SUFFIX;
import static cn.ares.boot.util.common.constant.StringConstant.JAR_SUFFIX;

import cn.ares.boot.util.common.IoUtil;
import cn.ares.boot.util.common.StringUtil;
import cn.ares.boot.util.common.file.FileUtil;
import cn.ares.boot.util.common.function.BiConsumerWithException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarFile;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Ares
 * @time: 2023-11-15 10:34:49
 * @description: 提取spring boot fat jar下业务class文件
 * @description: Extract the service class file from the spring boot fat jar
 * @version: JDK 1.8
 */
public class SpringBootClassesExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootClassesExtractor.class);

  /**
   * spring boot fat jar下BOOT-INF/classes/下的class文件
   * class file under BOOT-INF/classes/ in the springboot fat jar
   */
  private static final String REPACKAGED_CLASSES_LOCATION = "BOOT-INF/classes/";
  /**
   * spring boot fat jar下BOOT-INF/lib/下的class文件
   * class file under BOOT-INF/lib/ in the spring boot fat jar
   */
  private static final String LIBRARY_LOCATION = "BOOT-INF/lib/";


  /**
   * @author: Ares
   * @description: 提取spring boot fat jar下业务class文件输出到文件夹
   * @description: Extract the service class file from the spring boot fat jar and export it to the folder
   * @time: 2023-11-15 10:52:34
   * @params: [jarPath, bizPackageName, classesDir] fat jar路径，业务包名，输出文件夹路径
   * @return: void
   */
  public static void extractClasses(String jarPath, String bizPackageName, String classesDir)
      throws IOException {
    extractClasses(jarPath, bizPackageName, Collections.emptySet(), classesDir);
  }

  /**
   * @author: Ares
   * @description: 提取spring boot fat jar下业务class文件输出到文件夹
   * @description: Extract the service class file from the spring boot fat jar and export it to the folder
   * @time: 2023-11-15 10:52:34
   * @params: [jarPath, bizPackageName, excludePackageNameSet, classesDir]
   * @params: fat jar路径，业务包名，排除在外的包名，输出文件夹路径
   * @return: void
   */
  public static void extractClasses(String jarPath, String bizPackageName,
      Set<String> excludePackageNameSet, String classesDir) throws IOException {
    iterableClass(jarPath, bizPackageName, excludePackageNameSet,
        (entryName, inputStream) -> {
          File outputFile = new File(classesDir, entryName);
          try (FileOutputStream fileOutputStream = FileUtil.openOutputStream(outputFile)) {
            IoUtil.copy(inputStream, fileOutputStream);
          }
        });
  }

  /**
   * @author: Ares
   * @description: 提取spring boot fat jar下业务class文件并输出成gzip文件
   * @description: Extract the service class file in the spring boot fat jar and output it as a gzip file
   * @time: 2023-11-16 20:11:34
   * @params: [jarPath, bizPackageName, classesGzPath] fat jar路径，业务包名，输出gzip文件路径
   * @return: void
   */
  public static void extractClassesGzip(String jarPath, String bizPackageName, String classesGzPath)
      throws IOException {
    extractClassesGzip(classesGzPath, bizPackageName, Collections.emptySet(), jarPath);
  }

  /**
   * @author: Ares
   * @description: 提取spring boot fat jar下业务class文件并输出成gzip文件
   * @description: Extract the service class file in the spring boot fat jar and output it as a gzip file
   * @time: 2023-11-16 10:11:34
   * @params: [jarPath, bizPackageName, excludePackageNameSet, classesGzPath]
   * @params: fat jar路径，业务包名，排除在外的包名，输出gzip文件路径
   * @return: void
   */
  public static void extractClassesGzip(String jarPath, String bizPackageName,
      Set<String> excludePackageNameSet, String classesGzPath) throws IOException {
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(new GZIPOutputStream(
        new GZIPOutputStream(FileUtil.openOutputStream(new File(classesGzPath)))))) {
      extractClassesGzip(jarPath, bizPackageName, excludePackageNameSet, zipOutputStream);
    }
  }

  private static void extractClassesGzip(String jarPath, String bizPackageName,
      Set<String> excludePackageNameSet, ZipOutputStream zipOutputStream) throws IOException {
    iterableClass(jarPath, bizPackageName, excludePackageNameSet,
        (entryName, inputStream) -> {
          zipOutputStream.putNextEntry(new ZipEntry(entryName));
          IoUtil.copy(inputStream, zipOutputStream);
          zipOutputStream.closeEntry();
        });
  }


  /**
   * @author: Ares
   * @description: 遍历所有class（包括jar包中的）
   * @time: 2023-11-17 11:04:18
   * @params: [jarFile, bizPackageName, excludePackageNameSet, inputStreamBiConsumer, jarConsumer]
   * @params: fat jar路径，业务包名，排除在外的包名，输入流消费者，jar消费者
   * @return: void
   */
  public static void iterableClass(String jarPath, String bizPackageName,
      Set<String> excludePackageNameSet,
      BiConsumerWithException<String, InputStream> inputStreamConsumer) throws IOException {
    try (JarFile jarFile = new JarFile(jarPath)) {
      jarFile.stream().forEach(entry -> {
        String entryName = entry.getName();
        try {
          if (entryName.endsWith(CLASS_SUFFIX)) {
            // 普通jar其实用不到这段逻辑但不想做判断增加代码量无脑做一次好了
            entryName = entryName.replaceAll(REPACKAGED_CLASSES_LOCATION, "");
            String className = entryName.replaceAll("/", ".");
            if (StringUtil.isNotBlank(className) && className.startsWith(bizPackageName)
                && excludePackageNameSet.stream().noneMatch(className::startsWith)) {
              inputStreamConsumer.accept(entryName, jarFile.getInputStream(entry));
            }
          } else if (entryName.endsWith(JAR_SUFFIX)) {
            // 普通jar走不到这段逻辑不会jar包嵌入jar包
            entryName = entryName.replaceAll(LIBRARY_LOCATION, "");
            InputStream inputStream = jarFile.getInputStream(entry);
            File tempJarFile = FileUtil.writeTempFile(inputStream, entryName);
            iterableClass(tempJarFile.getAbsolutePath(), bizPackageName, excludePackageNameSet,
                inputStreamConsumer);
            CompletableFuture.runAsync(tempJarFile::delete);
          }
        } catch (Exception e) {
          LOGGER.warn("extract file: {} exception: ", entryName, e);
        }
      });
    }
  }


}
