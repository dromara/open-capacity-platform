package com.open.capacity.gateway.config;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alibaba.nacos.api.PropertyKeyConst;

import lombok.Data;

/**
 * 
 * @author someday
 * @date 2018/7/25 路由别名配置
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.nacos.discovery")
public class RouteAliasConfig {
	
	private String serverAddr ;
	private String namespace ;
	private String group ;
	private String username ;
	private String password ;
	private String dataId = "route.alias.config" ;
	private long timeout  = 6000  ;

	private static final Map<String, String> routeAlias = new ConcurrentHashMap<String, String>();
	/**
	 * nacos相关配置
	 * @return
	 */
	public Properties  getNacosProperties() {
		Properties properties = new Properties();
		
		properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
		properties.put(PropertyKeyConst.NAMESPACE, namespace);
		properties.put(PropertyKeyConst.USERNAME, username);
		properties.put(PropertyKeyConst.PASSWORD, password);
		return properties ;
		
	}
	/**
	 * 全局别名配置
	 * @return
	 */
	public static Map<String,String> getRouteAlias() {
		return routeAlias;
	}
}
