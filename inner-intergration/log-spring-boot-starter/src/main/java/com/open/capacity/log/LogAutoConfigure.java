package com.open.capacity.log;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.structlog4j.StructLog4J;
import com.github.structlog4j.json.JsonFormatter;
import com.open.capacity.log.properties.AuditLogProperties;
import com.open.capacity.log.properties.LogDbProperties;
import com.open.capacity.log.properties.TraceProperties;
import com.zaxxer.hikari.HikariConfig;

import cn.hutool.core.date.SystemClock;

/**
 * 日志自动配置
 *
 * @author someday
 * @date 2019/8/13 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@ComponentScan
@ConditionalOnClass(WebMvcConfigurer.class)
@EnableConfigurationProperties({ TraceProperties.class, AuditLogProperties.class })
public class LogAutoConfigure {

	/*
	 * 初始化StructLog4J配置
	 */
	@PostConstruct
	public void init() {
		StructLog4J.setFormatter(JsonFormatter.getInstance());
		StructLog4J.setMandatoryContextSupplier(() -> new Object[] { "logTime", SystemClock.nowDate() });

	}

	/**
	 * 日志数据库配置
	 */
	@Configuration
	@ConditionalOnClass(HikariConfig.class)
	@EnableConfigurationProperties(LogDbProperties.class)
	public static class LogDbAutoConfigure {
	}

}
