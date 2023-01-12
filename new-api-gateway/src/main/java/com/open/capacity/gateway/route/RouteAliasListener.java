package com.open.capacity.gateway.route;

import java.util.Map;
import java.util.concurrent.Executor;

import org.springframework.boot.CommandLineRunner;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.gateway.config.RouteAliasConfig;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author someday
 * @date 2018/7/25 路由别名监听
 */
@Slf4j
public class RouteAliasListener implements CommandLineRunner {

	private RouteAliasConfig routeAliasConfig;

	private ObjectMapper objectMapper;

	public RouteAliasListener() {
	}

	public RouteAliasListener( RouteAliasConfig routeAliasConfig, ObjectMapper objectMapper) {
		super();
		this.routeAliasConfig = routeAliasConfig;
		this.objectMapper = objectMapper;
	}

	@Override
	public void run(String... args) throws Exception {
		this.routeAliasListener();
	}

	/**
	 * 动态路由别名监听
	 */
	private void routeAliasListener() {
		if (ObjectUtil.isNotNull(this.routeAliasConfig)) {
			try {
				if( ObjectUtil.isNotNull(this.routeAliasConfig.getGroup())) {
				//创建配置实例
				ConfigService configService = NacosFactory
						.createConfigService(this.routeAliasConfig.getNacosProperties());
				//拉取最新配置内容
				String content = configService.getConfig(this.routeAliasConfig.getDataId(),
						this.routeAliasConfig.getGroup(), this.routeAliasConfig.getTimeout());
				// 路由配置
				if(content!=null) {
					log.info("【网关启动】读取路由别名配置项：{}", content);
					RouteAliasListener.this.setRouteAlias(content);
				}
				configService.addListener(this.routeAliasConfig.getDataId(), this.routeAliasConfig.getGroup(),
						new Listener() {
							//读取最新配置，第一次没有相关配置时，需要重启生效读取后续的变化
							@Override
							public void receiveConfigInfo(String configInfo) {
								log.info("【网关更新】读取Nacos路由别名配置项：{}", content);
								RouteAliasListener.this.setRouteAlias(configInfo);
							}
							@Override
							public Executor getExecutor() {
								return null;
							}
						});
				}
			} catch (NacosException e) {
				log.error("【启动路由别名失败】 ：{}", e.getErrMsg());
			}
		}
	}

	/**
	 * @param content 配置中心取值
	 */
	@SuppressWarnings("unchecked")
	private void setRouteAlias(String content) {
		try {
			Map<String,String> routes =   this.objectMapper.readValue(content, Map.class);
			RouteAliasConfig.getRouteAlias().putAll(routes);
		} catch (Exception e) {
			log.error("【设置路由别名失败】 ：{}", e.getMessage());
		}
	}

}
