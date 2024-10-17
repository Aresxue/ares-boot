package com.xiaohongshu.boot.starter.datasource.extension;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import java.util.List;
import org.apache.ibatis.session.Configuration;

/**
 * @author: Ares
 * @time: 2024-07-02 14:53:07
 * @description: Boot extension sql injector
 * @version: JDK 1.8
 */
public class ExtensionSqlInjector extends DefaultSqlInjector {

  @Override
  public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass,
      TableInfo tableInfo) {
    List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
    methodList.add(
        new InsertBatchSomeColumn(fieldInfo -> fieldInfo.getFieldFill() != FieldFill.UPDATE));
    methodList.add(
        new AlwaysUpdateSomeColumnById(fieldInfo -> fieldInfo.getFieldFill() != FieldFill.INSERT));
    return methodList;
  }

}
