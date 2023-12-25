package cn.ares.boot.util.common;

import cn.ares.boot.util.common.entity.CronExpression;
import cn.ares.boot.util.common.structure.MapObject;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author: Ares
 * @time: 2023-12-23 21:36:13
 * @description: InvokeUtil test
 * @version: JDK 1.8
 */
public class InvokeUtilTest {

  public static void main(String[] args)
      throws NoSuchMethodException, ParseException, NoSuchFieldException {
    Method method = CronExpression.class.getDeclaredMethod("getCronExpression");
    Function<CronExpression, String> virtualMethodFunction = InvokeUtil.generateFunction(method);
    CronExpression cronExpression = new CronExpression("0 0 0 * * ?");
    System.out.println(virtualMethodFunction.apply(cronExpression));

    method = CronExpression.class.getDeclaredMethod("findMinIncrement");
    Function<CronExpression, Long> privateVirtualMethodFunction = InvokeUtil.generateFunction(
        method);
    System.out.println(privateVirtualMethodFunction.apply(cronExpression));

    method = MapObject.class.getDeclaredMethod("getInteger", String.class);
    BiFunction<MapObject, String, Integer> biFunction = InvokeUtil.generateBiFunction(method);
    MapObject mapObject = new MapObject();
    System.out.println(biFunction.apply(mapObject, "ares"));

    // 静态方法自身是null所以要生成比实例方法少一个参数的Function
    method = StringUtil.class.getDeclaredMethod("atoi", String.class);
    Function<String, Integer> statucMethodFunction = InvokeUtil.generateFunction(method);
    System.out.println(statucMethodFunction.apply("123"));
  }

}
