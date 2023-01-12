package com.open.capacity.uaa.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.open.capacity.common.feign.AsycUserService;
import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.uaa.common.store.CustomerJwtTokenStore;

/**
 * 资源服务器 TokenStore 配置类，使用 JWT HMAC 对称加密
 *
 * @author someday
 * @date 2018/8/20 9:25
 */
@Configuration
@SuppressWarnings("all")
@ConditionalOnProperty(prefix = "ocp.oauth2.token.store", name = "type", havingValue = "jwt")
public class DefaultJwtTokenStoreConfig {
	
	@Lazy
	@Autowired
	private UserFeignClient userFeignClient;
	
	@Bean
	public JwtTokenStore jwtTokenStore(){
		return new CustomerJwtTokenStore( jwtAccessTokenConverter() ,userFeignClient ) ;
	}
	
	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter(){
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("ocp");
		return accessTokenConverter ;
	}
}
