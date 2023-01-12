package com.open.capacity;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @Description
 * @Version 1.0
 **/
public class MyBatisPlusGenerator {

	public static void main(String[] args) {
		// 1. 全局配置
		GlobalConfig config = new GlobalConfig();
		// 是否支持AR模式
		config.setActiveRecord(true)
				// 作者
				.setAuthor("owen")
				// 生成路径，最好使用绝对路径，window路径是不一样的
				.setOutputDir("F:\\code\\newocp\\tuning-center\\test-generator-starter\\src\\main\\java")
				// 文件覆盖
				.setFileOverride(true)
				// 主键策略
				.setIdType(IdType.AUTO)
				.setDateType(DateType.ONLY_DATE)
				// 设置生成的service接口的名字的首字母是否为I，默认Service是以I开头的
				.setServiceName("%sService")
				// 实体类结尾名称
				.setEntityName("%s")
				// 生成基本的resultMap
				.setBaseResultMap(true)
				// 不使用AR模式
				.setActiveRecord(false)
				// 生成基本的SQL片段
				.setBaseColumnList(true);

		// 2. 数据源配置
		DataSourceConfig dsConfig = new DataSourceConfig();
		// 设置数据库类型
		dsConfig.setDbType(DbType.MYSQL).setDriverName("com.mysql.cj.jdbc.Driver")
				.setUrl("jdbc:mysql://114.67.115.7:3306/log-center?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai")
				.setUsername("root").setPassword("520owenwangwenQ_s");

		// 3. 策略配置globalConfiguration中
		StrategyConfig stConfig = new StrategyConfig();

		// 全局大写命名
		stConfig.setCapitalMode(true)
				// 数据库表映射到实体的命名策略
				.setNaming(NamingStrategy.underline_to_camel)
				// 使用lombok
				.setEntityLombokModel(true)
				// 使用restcontroller注解
				.setRestControllerStyle(true)
				// 生成的表, 支持多表一起生成，以数组形式填写
				// TODO TODO TODO TODO
				.setInclude(
						"sys_log" );

		// 4. 包名策略配置
		PackageConfig pkConfig = new PackageConfig();
		pkConfig.setParent("com.open.capacity.log").setMapper("mapper").setService("service").setController("controller")
				.setEntity("entity").setXml("mapper");

		// 5. 整合配置
		AutoGenerator ag = new AutoGenerator();
		ag.setGlobalConfig(config).setDataSource(dsConfig).setStrategy(stConfig).setPackageInfo(pkConfig);

		// 6. 执行操作
		ag.execute();
	}
}