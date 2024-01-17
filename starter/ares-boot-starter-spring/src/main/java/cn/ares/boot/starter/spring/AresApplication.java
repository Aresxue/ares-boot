package cn.ares.boot.starter.spring;

import cn.ares.boot.starter.spring.spi.AresApplicationExtensionService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import org.springframework.boot.SpringApplication;

/**
 * @author: Ares
 * @time: 2022-05-25 12:12:11
 * @description: The startup method of the boot application, adding the extension of pre- and
 * post-processing
 * @description: 框架应用的启动方式, 加入前后处理的扩展
 * @version: JDK 1.8
 */
public class AresApplication {

  private static final List<AresApplicationExtensionService> EXTENSION_SERVICE_LIST = new ArrayList<>();

  static {
    ServiceLoader<AresApplicationExtensionService> serviceLoader = ServiceLoader.load(
        AresApplicationExtensionService.class);
    for (AresApplicationExtensionService extensionService : serviceLoader) {
      EXTENSION_SERVICE_LIST.add(extensionService);
    }
    EXTENSION_SERVICE_LIST.sort(Comparator.comparingInt((AresApplicationExtensionService::getOrder)));
  }

  public static void run(Class<?> primarySource, String[] args, Runnable... afterRunnableArr) {
    for (AresApplicationExtensionService extensionService : EXTENSION_SERVICE_LIST) {
      extensionService.handleBeforeRun(primarySource, args);
    }

    SpringApplication.run(primarySource, args);
    // Handle after run
    for (Runnable runnable : afterRunnableArr) {
      runnable.run();
    }

    for (int i = EXTENSION_SERVICE_LIST.size() - 1; i >= 0; i--) {
      EXTENSION_SERVICE_LIST.get(i).handleAfterRun(primarySource, args);
    }
  }

}
