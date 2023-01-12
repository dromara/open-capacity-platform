package com.open.capacity.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.capacity.common.constant.ConfigConstants;
import com.open.capacity.gateway.filter.DeflectionIntanceFilter;
import com.open.capacity.gateway.filter.GrayVersionIsolationFilter;
import com.open.capacity.gateway.filter.IpHashLoadBalancerClientFilter;

/**
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
public class GatewayAutoConfig {
	
	/**
	 * 偏向性路由
	 * @param clientFactory
	 * @param properties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean({ DeflectionIntanceFilter.class })
	public DeflectionIntanceFilter deflectionRouteFilter(LoadBalancerClientFactory clientFactory,
			GatewayLoadBalancerProperties properties) {
		return new DeflectionIntanceFilter(clientFactory, properties);
	}
	

	/**
	 * 一致性hash路由
	 * @param clientFactory
	 * @param properties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean({ IpHashLoadBalancerClientFilter.class })
	public IpHashLoadBalancerClientFilter ipHashLoadBalancerClientFilter(LoadBalancerClientFactory clientFactory,
			GatewayLoadBalancerProperties properties) {
		return new IpHashLoadBalancerClientFilter(clientFactory, properties);
	}
	
	/**
	 * 灰度路由
	 * @param clientFactory
	 * @param properties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean({ GrayVersionIsolationFilter.class })
	@ConditionalOnProperty(prefix = ConfigConstants.CONFIG_GATEWAY_ISOLATION, name = "enabled", havingValue = "true")
	public GrayVersionIsolationFilter grayReactiveLoadBalancerClientFilter(LoadBalancerClientFactory clientFactory,
			GatewayLoadBalancerProperties properties) {
		return new GrayVersionIsolationFilter(clientFactory, properties);
	}
	
	
}
