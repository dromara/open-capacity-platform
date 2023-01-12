package com.open.capacity.db.metrics;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory;

import io.prometheus.client.CollectorRegistry;

/**
 * 基于Prometheus监控平台的HikariDataSource监控
 * @author owen
 */
@Configuration
@ConditionalOnBean( HikariDataSource.class )
@ConditionalOnClass({ HikariDataSource.class, CollectorRegistry.class })
public class HikaricpMetricsConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(value = MetricsTrackerFactory.class)
	public MetricsTrackerFactory duridFilterRegistrationBean() {
		MetricsTrackerFactory metricsTrackerFactory = new PrometheusMetricsTrackerFactory();
		return metricsTrackerFactory;
	}
	
}
