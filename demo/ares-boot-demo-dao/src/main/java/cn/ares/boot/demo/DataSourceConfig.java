package cn.ares.boot.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Ares
 * @time: 2024-07-19 11:48:18
 * @description: 数据源配置类
 * @version: JDK 1.8
 */
@Configuration
@MapperScan(basePackages = {"cn.ares.boot.demo.**.mapper", "cn.ares.boot.demo.**.dao"})
public class DataSourceConfig {

}
