package cn.ares.boot.util.common;

import cn.ares.boot.util.common.entity.CronExpression;
import cn.ares.boot.util.common.log.JdkLoggerUtil;
import cn.ares.boot.util.common.structure.MapObject;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.TimeZone;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author: Ares
 * @time: 2023-12-23 21:36:13
 * @description: InvokeUtil test
 * @version: JDK 1.8
 */
public class InvokeUtilTest {

  private static final Logger LOGGER = JdkLoggerUtil.getLogger(InvokeUtilTest.class);

  public static void main(String[] args) throws Throwable {
    Method method = CronExpression.class.getDeclaredMethod("getCronExpression");
    Function<CronExpression, String> virtualMethodFunction = InvokeUtil.generateFunction(method);
    CronExpression cronExpression = new CronExpression("0 0 0 * * ?");
    JdkLoggerUtil.info(LOGGER, virtualMethodFunction.apply(cronExpression));

    method = CronExpression.class.getDeclaredMethod("findMinIncrement");
    Function<CronExpression, Long> privateVirtualMethodFunction = InvokeUtil.generateFunction(
        method);
    JdkLoggerUtil.info(LOGGER, privateVirtualMethodFunction.apply(cronExpression));

    method = MapObject.class.getDeclaredMethod("getInteger", String.class);
    BiFunction<MapObject, String, Integer> biFunction = InvokeUtil.generateBiFunction(method);
    MapObject mapObject = new MapObject();
    JdkLoggerUtil.info(LOGGER, biFunction.apply(mapObject, "ares"));

    // 静态方法自身是null所以要生成比实例方法少一个参数的Function
    method = StringUtil.class.getDeclaredMethod("atoi", String.class);
    Function<String, Integer> statucMethodFunction = InvokeUtil.generateFunction(method);
    JdkLoggerUtil.info(LOGGER, statucMethodFunction.apply("123"));

    method = CronExpression.class.getDeclaredMethod("setTimeZone", TimeZone.class);
    MethodHandle methodHandle = InvokeUtil.findMethodHandle(method);
    methodHandle.invokeExact((Object) cronExpression, (Object) TimeZone.getDefault());
  }

}
