package com.open.capacity.db;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidFilterConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidStatViewServletConfiguration;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidWebStatFilterConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.open.capacity.db.condition.DruidDataSourceCondition;
import com.open.capacity.db.prefix.DbPrefix;

/**
 * @author 作者 someday
 * @version 创建时间：2017年04月23日 下午20:01:06 类说明 blog:
 *          https://blog.51cto.com/13005375 code:
 *          https://gitee.com/owenwangwen/open-capacity-platform
 *          在设置了spring.datasource.enable.dynamic 等于true是开启多数据源，配合日志
 */
@Configuration
@EnableConfigurationProperties({ DruidStatProperties.class })
@Import({ DruidSpringAopConfiguration.class, DruidStatViewServletConfiguration.class,
		DruidWebStatFilterConfiguration.class, DruidFilterConfiguration.class })
@AutoConfigureBefore(value = { DruidDataSourceAutoConfigure.class, MybatisPlusAutoConfiguration.class })
@Conditional(DruidDataSourceCondition.class)
public class DataSourceAutoConfig {

//	创建数据源
	@Bean
	@ConfigurationProperties("spring.datasource.druid")
	public DataSource dataSourceCore() {
		return DruidDataSourceBuilder.create().build();
	}

}
