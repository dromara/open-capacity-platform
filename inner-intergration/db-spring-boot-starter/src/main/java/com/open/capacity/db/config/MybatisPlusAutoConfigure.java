package com.open.capacity.db.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.open.capacity.common.properties.TenantProperties;
import com.open.capacity.db.handler.DateMetaObjectHandler;
import com.open.capacity.db.interceptor.CustomSlowQueryInterceptor;
import com.open.capacity.db.interceptor.CustomTenantInterceptor;
import com.open.capacity.db.interceptor.CustomerBigResultInterceptor;
import com.open.capacity.db.properties.MybatisPlusAutoFillProperties;
import com.open.capacity.db.properties.MybatisPlusBigQueryProperties;
import com.open.capacity.db.properties.MybatisPlusSlowSqlProperties;

/**
 * MyBatis-plus配置
 * @author zlt
 * @version 1.0
 * @date 2018/5/6
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@EnableConfigurationProperties({ MybatisPlusAutoFillProperties.class, MybatisPlusBigQueryProperties.class , MybatisPlusSlowSqlProperties.class })
public class MybatisPlusAutoConfigure {
	@Autowired
	private TenantLineHandler tenantLineHandler;

	@Autowired
	private TenantProperties tenantProperties;

	@Autowired
	private MybatisPlusAutoFillProperties autoFillProperties;

	@Autowired
	private MybatisPlusBigQueryProperties bigQueryProperties;

	@Autowired
	private MybatisPlusSlowSqlProperties slowSqlProperties;
	
	static {
		System.setProperty("pagehelper.banner", "false");
	}
	/**
	 * 分页插件，自动识别数据库类型
	 */
	@Bean
	public MybatisPlusInterceptor paginationInterceptor() {
		MybatisPlusInterceptor mpInterceptor = new MybatisPlusInterceptor();
		boolean enableTenant = tenantProperties.getEnable();
		// 是否开启多租户隔离
		if (enableTenant) {
			CustomTenantInterceptor tenantInterceptor = new CustomTenantInterceptor(tenantLineHandler,
					tenantProperties.getIgnoreSqls());
			mpInterceptor.addInnerInterceptor(tenantInterceptor);
		}
		mpInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
		return mpInterceptor;
	}

	/**
	 * SQL执行时间监控插件
	 * @return CustomSlowQueryInterceptor
	 */
	@Bean
	public CustomSlowQueryInterceptor customLogInterceptor() {
		return new CustomSlowQueryInterceptor(slowSqlProperties.getEnabled(), slowSqlProperties.getSlowSqlThresholdMs());
	}
	
	/**
	 * 查询大结果集监控插件
	 * @return CustomerBigResultInterceptor
	 */
	@Bean
	public CustomerBigResultInterceptor customerBigResultInterceptor() {
		return new CustomerBigResultInterceptor(bigQueryProperties.getEnabled(), bigQueryProperties.getSize());
	}

	/**
	 * 自定义填充公共字段
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "ocp.mybatis-plus.auto-fill", name = "enabled", havingValue = "true", matchIfMissing = true)
	public MetaObjectHandler metaObjectHandler() {
		return new DateMetaObjectHandler(autoFillProperties);
	}

}
