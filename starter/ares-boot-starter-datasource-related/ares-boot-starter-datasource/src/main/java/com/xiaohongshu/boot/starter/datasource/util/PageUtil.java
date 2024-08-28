package cn.ares.boot.starter.datasource.util;

import cn.ares.boot.base.model.page.PageRequest;
import cn.ares.boot.base.model.page.Pageable;
import cn.ares.boot.util.common.CollectionUtil;
import cn.ares.boot.util.spring.BeanCopyUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author: Ares
 * @time: 2022-09-09 15:06:42
 * @description: 分页工具类
 * @description: Page util
 * @version: JDK 1.8
 */
public class PageUtil {

  /**
   * @author: Ares
   * @description: 分页请求转换为mybatis-plus分页对象
   * @time: 2024-08-28 17:40:17
   * @params: [request] 分页请求
   * @return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<DO> mybatis-plus分页对象
   */
  public static <DO> Page<DO> convert(PageRequest request) {
    Page<DO> page = Page.of(request.getCurrent(), request.getSize());
    page.addOrder(PageConvert.INSTANCE.sort2Order(request.getSortFieldList()));
    return page;
  }

  /**
   * @author: Ares
   * @description: 从入参中提取分页对象
   * @time: 2024-08-28 17:40:50
   * @params: [request] 入参
   * @return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<DO> mybatis-plus分页对象
   */
  public static <T extends PageRequest, DO> Page<DO> extraPage(T request) {
    return convert(extraPageRequest(request));
  }

  /**
   * @author: Ares
   * @description: 从入参中提取分页对象
   * @time: 2024-08-28 17:41:45
   * @params: [request] 入参
   * @return: cn.ares.boot.base.model.page.PageRequest 分页对象
   */
  public static <T extends PageRequest> PageRequest extraPageRequest(T request) {
    return new PageRequest(request.getCurrent(), request.getSize(), request.getSortFieldList());
  }

  /**
   * @author: Ares
   * @description: 从mybatis-plus分页对象转换为分页响应对象
   * @time: 2024-08-28 17:42:45
   * @params: [page, mapping] mybatis-plus分页对象，映射函数
   * @return: cn.ares.boot.base.model.page.Pageable<BO> 分页响应对象
   */
  public static <BO, DO> Pageable<BO> convert(Page<DO> page,
      Function<? super DO, ? extends BO> mapping) {
    Pageable pageable = PageConvert.INSTANCE.target2Source(page);
    if (null != mapping && CollectionUtil.isNotEmpty(pageable.getRecordList())) {
      pageable.convert(mapping);
    }
    return pageable;
  }

  /**
   * @author: Ares
   * @description: 从业务分页响应对象转换为视图分页响应对象
   * @time: 2024-08-28 17:43:18
   * @params: [pageable, targetClass] 业务分页响应对象，视图类
   * @return: cn.ares.boot.base.model.page.Pageable<VO> 视图分页响应对象
   */
  public static <BO, VO> Pageable<VO> convertBoToVo(Pageable<BO> pageable,
      Class<VO> targetClass) {
    return convertBoToVo(pageable, targetClass, true);
  }

  /**
   * @author: Ares
   * @description: 从业务分页响应对象转换为视图分页响应对象
   * @time: 2024-08-28 17:43:18
   * @params: [pageable, targetClass, copyData] 业务分页响应对象，视图类，是否拷贝数据
   * @return: cn.ares.boot.base.model.page.Pageable<VO> 视图分页响应对象
   */
  public static <BO, VO> Pageable<VO> convertBoToVo(Pageable<BO> pageable, Class<VO> targetClass,
      boolean copyData) {
    return convertBoToVo(pageable, targetClass, copyData, null);
  }

  /**
   * @author: Ares
   * @description: 从业务分页响应对象转换为视图分页响应对象
   * @time: 2024-08-28 17:43:18
   * @params: [pageable, targetClass, copyData, biConsumer] 业务分页响应对象，视图类，是否拷贝数据，额外数据处理
   * @return: cn.ares.boot.base.model.page.Pageable<VO> 视图分页响应对象
   */
  public static <BO, VO> Pageable<VO> convertBoToVo(Pageable<BO> pageable, Class<VO> targetClass,
      boolean copyData, BiConsumer<BO, VO> biConsumer) {
    Pageable<VO> result = new Pageable<>();
    result.setCurrent(pageable.getCurrent());
    result.setSize(pageable.getSize());
    result.setTotal(pageable.getTotal());
    if (copyData) {
      List<BO> list = pageable.getRecordList();
      if (null == list) {
        result.setRecordList(new ArrayList<>());
      } else {
        if (null == biConsumer) {
          result.setRecordList(BeanCopyUtil.copyList(list, targetClass));
        } else {
          result.setRecordList(BeanCopyUtil.copyList(list, targetClass, biConsumer));
        }
      }
    }
    return result;
  }

}
