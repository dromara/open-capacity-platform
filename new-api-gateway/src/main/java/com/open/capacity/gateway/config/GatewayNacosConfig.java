package com.open.capacity.gateway.config;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alibaba.nacos.api.PropertyKeyConst;

import lombok.Data;

/**
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.nacos.discovery")
public class GatewayNacosConfig {
	
	private String serverAddr ;
	private String namespace ;
	private String group ;
	private String username ;
	private String password ;
	private String dataId = "gateway.config" ;
	private long timeout  =3000  ;

	public Properties  getNacosProperties() {
		Properties properties = new Properties();
		
		properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
		properties.put(PropertyKeyConst.NAMESPACE, namespace);
		properties.put(PropertyKeyConst.USERNAME, username);
		properties.put(PropertyKeyConst.PASSWORD, password);
		
		return properties ;
		
	}
	
	
}
