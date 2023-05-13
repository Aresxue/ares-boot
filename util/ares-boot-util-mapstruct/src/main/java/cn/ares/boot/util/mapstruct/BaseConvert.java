package cn.ares.boot.util.mapstruct;

import java.util.List;
import java.util.stream.Stream;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;

/**
 * @author: Ares
 * @time: 2021-04-06 20:39:00
 * @description: Base convert
 * @version: JDK 1.8
 */
public interface BaseConvert<SOURCE, TARGET> {

  /**
   * @author: Ares
   * @description: 从源对象映射同名属性到目标对象
   * @description: Mapping properties of the same name from the source object to the target object
   * @time: 2022-06-08 15:29:23
   * @params: [source] 源对象
   * @return: TARGET 目标对象
   */
  TARGET source2Target(SOURCE source);

  /**
   * @author: Ares
   * @description: 从目标对象映射同名属性到源对象
   * @description: Mapping properties of the same name from the target object to the source object
   * @time: 2022-06-08 15:29:23
   * @params: [target] 目标对象
   * @return: SOURCE 源对象
   */
  @InheritInverseConfiguration(name = "source2Target")
  SOURCE target2Source(TARGET target);

  /**
   * @author: Ares
   * @description: 对目标对象集合中的对象映射同名属性到源对象中的对象
   * @description: Maps properties of the same name from objects in the target object collection to
   * objects in the source object
   * @time: 2022-06-08 15:30:49
   * @params: [sourceList] 源对象集合
   * @return: java.util.List<TARGET> 目标对象集合
   */
  @InheritConfiguration(name = "source2Target")
  List<TARGET> sources2Targets(List<SOURCE> sourceList);

  /**
   * @author: Ares
   * @description: 对源对象集合中的对象映射同名属性到目标对象中的对象
   * @description: Maps properties of the same name from objects in the source object collection to
   * objects in the target object
   * @time: 2022-06-08 15:30:49
   * @params: [targetList] 目标对象集合
   * @return: java.util.List<SOURCE> 源对象集合
   */
  @InheritConfiguration(name = "target2Source")
  List<SOURCE> targets2Sources(List<TARGET> targetList);


  /**
   * @author: Ares
   * @description: 把集合流映射同名属性到目标对象集合
   * @description: Map the collection stream to the property of the same name to the target object
   * collection
   * @time: 2022-06-08 15:33:03
   * @params: [stream] 集合流
   * @return: java.util.List<TARGET> 目标对象集合
   */
  @InheritConfiguration(name = "source2Target")
  List<TARGET> sourcesToTargets(Stream<SOURCE> stream);

  /**
   * @author: Ares
   * @description: 把集合流映射同名属性到源对象集合
   * @description: Map the collection stream to the property of the same name to the source object
   * collection
   * @time: 2022-06-08 15:33:03
   * @params: [stream] 集合流
   * @return: java.util.List<SOURCE> 源对象集合
   */
  @InheritConfiguration(name = "target2Source")
  List<SOURCE> targetsToSources(Stream<TARGET> stream);

}
