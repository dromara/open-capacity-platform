package com.open.capacity.jpush;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.open.capacity.jpush.JPushProperties.JPushSlaveClientConfig;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;

/**
 * 
 */
@Configuration
@ConditionalOnClass(JPushClient.class)
@EnableConfigurationProperties({ JPushProperties.class })
public class JPushAutoConfiguration {

	
	@Bean
	@ConditionalOnMissingBean
	public ClientConfig jPushClientConfig()   {
		return ClientConfig.getInstance();  
	}
	
	@Bean
	public JPushClient jPushClient(JPushProperties properties, ClientConfig jPushClientConfig)   {
		// not set proxy
		if(Objects.isNull(properties.getProxy())) {
			return new JPushClient(properties.getAppKey(), properties.getMasterSecret(), properties.getProxy(), jPushClientConfig);  
		}
		return new JPushClient(properties.getAppKey(), properties.getMasterSecret());  
	}
	
	@Bean
	public JPushTemplate jPushTemplate(JPushClient jPushClient, JPushProperties properties, ClientConfig jPushClientConfig)   {
		List<JPushClientExt> clients = new ArrayList<JPushClientExt>();
		if(!CollectionUtils.isEmpty(properties.getSlaves())) {
			for (JPushSlaveClientConfig clientConfig : properties.getSlaves()) {
				if(Objects.isNull(clientConfig.getProxy())) {
					clients.add(new JPushClientExt(clientConfig.getAppId(), clientConfig.getAppKey(), clientConfig.getAppSecret(), clientConfig.getProxy(), jPushClientConfig));
				} else {
					clients.add(new JPushClientExt(clientConfig.getAppId(), clientConfig.getAppKey(), clientConfig.getAppSecret()));  
				}
			}
		}
		return new JPushTemplate(jPushClient, clients, properties.isProduction());  
	}

}
