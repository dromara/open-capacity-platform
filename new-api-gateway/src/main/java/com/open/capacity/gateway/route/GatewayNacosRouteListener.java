package com.open.capacity.gateway.route;

import java.util.concurrent.Executor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.gateway.config.GatewayNacosConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GatewayNacosRouteListener implements CommandLineRunner {

	private DynamicRouteDefinitionRepository dynamicRouteDefinitionRepository;

	private GatewayNacosConfig gatewayNacosConfig;

	private ObjectMapper objectMapper;

	public GatewayNacosRouteListener() {
	}

	public GatewayNacosRouteListener(DynamicRouteDefinitionRepository dynamicRouteDefinitionRepository,
			GatewayNacosConfig gatewayNacosConfig, ObjectMapper objectMapper) {
		super();
		this.dynamicRouteDefinitionRepository = dynamicRouteDefinitionRepository;
		this.gatewayNacosConfig = gatewayNacosConfig;
		this.objectMapper = objectMapper;
	}

	@Override
	public void run(String... args) throws Exception {
		this.nacosDynmaicRouteListener();
	}

	// 动态路由监听
	private void nacosDynmaicRouteListener() {
		if (this.gatewayNacosConfig != null) {
			try {
				if(this.gatewayNacosConfig.getGroup()!=null) {
				//创建配置实例
				ConfigService configService = NacosFactory
						.createConfigService(this.gatewayNacosConfig.getNacosProperties());
				//拉取最新配置内容
				String content = configService.getConfig(this.gatewayNacosConfig.getDataId(),
						this.gatewayNacosConfig.getGroup(), this.gatewayNacosConfig.getTimeout());
				// 路由配置
				if(content!=null) {
					log.info("【网关启动】读取Nacos网关配置项：{}", content);
					GatewayNacosRouteListener.this.setRoute(content);
				}
				configService.addListener(this.gatewayNacosConfig.getDataId(), this.gatewayNacosConfig.getGroup(),
						new Listener() {
							//读取后续的变化后触发
							@Override
							public void receiveConfigInfo(String configInfo) {
								log.info("【网关更新】读取Nacos网关配置项：{}", content);
								GatewayNacosRouteListener.this.setRoute(configInfo);
							}
							@Override
							public Executor getExecutor() {
								return null;
							}
						});
				}
			} catch (NacosException e) {
				log.error("【启动动态路由失败】 ：{}", e.getErrMsg());
			}
		}
	}
	// 定义路由处理
	private void setRoute(String content) {
		try {
			RouteDefinition[] routes = this.objectMapper.readValue(content, RouteDefinition[].class);
			for (RouteDefinition route : routes) {
				dynamicRouteDefinitionRepository.update(route);
			}
		} catch (Exception e) {
			log.error("【设置动态路由失败】 ：{}", e.getMessage());
		}
	}

}
