package com.xiaohongshu.boot.starter.datasource.extension;

import static com.xiaohongshu.boot.starter.datasource.constant.DataSourceConstant.LIMIT_ONE;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import java.util.Collection;
import org.apache.ibatis.annotations.Param;

/**
 * @author: Ares
 * @time: 2024-07-02 14:50:48
 * @description: 扩展基础Mapper
 * @description: Extension base mapper
 * @version: JDK 1.8
 */
public interface ExtensionBaseMapper<T> extends BaseMapper<T> {

  /**
   * @author: Ares
   * @description: 批量插入
   * @time: 2024-07-02 14:51:20
   * @params: [entityList] 对象列表
   * @return: int 影响行数
   */
  int insertBatchSomeColumn(Collection<T> entityList);

  /**
   * @author: Ares
   * @description: 根据逐渐更新固定字段
   * @description: Update fixed fields according to primary key
   * @time: 2024-07-02 14:54:05
   * @params: [entity] 实体对象
   * @return: int 影响行数
   */
  int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) T entity);

  /**
   * @author: Ares
   * @description: 查询一条记录
   * @description: Query one record
   * @time: 2024-07-02 14:54:28
   * @params: [queryWrapper] 查询条件
   * @return: T 查询结果
   */
  default <R, Children extends AbstractWrapper<T, R, Children>> T selectLimitOne(
      @Param(Constants.WRAPPER) AbstractWrapper<T, R, Children> queryWrapper) {
    queryWrapper.last(LIMIT_ONE);
    return selectOne(queryWrapper);
  }

}
