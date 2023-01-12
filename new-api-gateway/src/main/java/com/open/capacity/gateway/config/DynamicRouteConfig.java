package com.open.capacity.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.gateway.route.DynamicRouteDefinitionRepository;
import com.open.capacity.gateway.route.GatewayNacosRouteListener;
import com.open.capacity.gateway.route.RouteAliasListener;

/**
 * 动态路由配置
 *
 * @author owen
 * @date 2018/10/7
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class DynamicRouteConfig {
	@Autowired
	private ApplicationEventPublisher publisher;

	/**路由数据的写入**/
	@Autowired
	private RouteDefinitionWriter routeDefinitionWriter;

	/**
	 * 基于内存方式新增
	 * @return
	 */
	@Bean
	public DynamicRouteDefinitionRepository dynamicRouteDefinitionRepository() {
		return new DynamicRouteDefinitionRepository(routeDefinitionWriter, publisher);
	}

	/**
	 * Nacos实现方式动态路由
	 */
	@Configuration
	@EnableConfigurationProperties({ GatewayNacosConfig.class })
	public class NacosDynRouteConfig {

		@Autowired
		private DynamicRouteDefinitionRepository dynamicRouteDefinitionRepository;
		@Autowired
		private GatewayNacosConfig gatewayNacosConfig;
		@Autowired
		private ObjectMapper objectMapper;

		@Bean
		@Conditional(NacosCondition.class)
		public GatewayNacosRouteListener gatewayNacosRouteListener() {
			return new GatewayNacosRouteListener(dynamicRouteDefinitionRepository, gatewayNacosConfig, objectMapper);
		}

	}

	/**
	 * nacos实现路由别名设置
	 * @author 
	 *
	 */
	@Configuration
	@EnableConfigurationProperties({ RouteAliasConfig.class })
	public class NacosRouteAliasConfig {

		@Autowired
		private RouteAliasConfig routeAliasConfig;
		@Autowired
		private ObjectMapper objectMapper;
		

		@Bean
		@Conditional(NacosCondition.class)
		public RouteAliasListener routeAliasListener() {
			return new RouteAliasListener(routeAliasConfig, objectMapper);
		}
	}
	
	public static class NacosCondition implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return context.getEnvironment().containsProperty("spring.cloud.nacos.discovery.server-addr")
					&& context.getEnvironment().containsProperty("spring.cloud.nacos.discovery.namespace")
					&& context.getEnvironment().containsProperty("spring.cloud.nacos.discovery.group") ;
		}

	}

}
