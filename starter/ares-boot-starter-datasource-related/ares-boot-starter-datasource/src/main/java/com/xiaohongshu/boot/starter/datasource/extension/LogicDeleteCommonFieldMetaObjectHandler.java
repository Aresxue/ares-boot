package com.xiaohongshu.boot.starter.datasource.extension;

import static com.xiaohongshu.boot.starter.datasource.constant.DataSourceConstant.CREATE_TIME;
import static com.xiaohongshu.boot.starter.datasource.constant.DataSourceConstant.DELETED;
import static com.xiaohongshu.boot.starter.datasource.constant.DataSourceConstant.UPDATE_TIME;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;

/**
 * @author: Ares
 * @time: 2024-07-02 14:31:01
 * @description: 逻辑删除通用字段元对象处理器
 * @description: Logical delete common field meta object handler
 * @version: JDK 1.8
 */
public class LogicDeleteCommonFieldMetaObjectHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    setFieldValByName(CREATE_TIME, LocalDateTime.now(), metaObject);
    setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);
    setFieldValByName(DELETED, 0, metaObject);
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);
  }

}
