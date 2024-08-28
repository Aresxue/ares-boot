package com.xiaohongshu.boot.starter.datasource.util;

import cn.ares.boot.base.model.page.Pageable;
import cn.ares.boot.base.model.page.SortField;
import cn.ares.boot.util.mapstruct.BaseConvert;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

/**
 * @author: Ares
 * @time: 2024-08-28 17:31:18
 * @description: 分页对象转换
 * @version: JDK 1.8
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PageConvert extends BaseConvert<Pageable, Page> {

  PageConvert INSTANCE = Mappers.getMapper(PageConvert.class);

  @Override
  @Mapping(target = "orders", expression = "java(sort2Order(source.getSortFieldList()))")
  Page source2Target(Pageable source);

  @Override
  @InheritInverseConfiguration(name = "source2Target")
  @Mapping(target = "sortFieldList", expression = "java(order2Sort(target.orders()))")
  Pageable target2Source(Page target);

  default List<OrderItem> sort2Order(List<SortField> sortFieldList) {
    if (CollectionUtils.isEmpty(sortFieldList)) {
      return Collections.emptyList();
    }
    return sortFieldList.stream().map(sortField -> {
      OrderItem orderItem = new OrderItem();
      orderItem.setColumn(sortField.getColumn());
      orderItem.setAsc(sortField.isAsc());
      return orderItem;
    }).collect(Collectors.toList());
  }

  default List<SortField> order2Sort(List<OrderItem> orderItemList) {
    if (CollectionUtils.isEmpty(orderItemList)) {
      return Collections.emptyList();
    }
    return orderItemList.stream().map(orderItem -> {
      SortField sortField = new SortField();
      sortField.setColumn(orderItem.getColumn());
      sortField.setAsc(orderItem.isAsc());
      return sortField;
    }).collect(Collectors.toList());
  }

}
