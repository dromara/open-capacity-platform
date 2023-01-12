package com.open.capacity.uaa.common.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.uaa.common.converter.CustomJwtAccessTokenConverter;
import com.open.capacity.uaa.common.converter.CustomUserAuthenticationConverter;

/**
 * uaa client TokenStore 
 * 使用 JWT RSA 非对称加密
 *
 */
@Configuration
@SuppressWarnings("all")
@ConditionalOnProperty(prefix = "ocp.oauth2.token.store", name = "type", havingValue = "resJwt")
public class DefaultResJwtTokenStore {

	@Autowired
	private UserFeignClient userFeignClient;
	

	@Bean
	public CustomJwtAccessTokenConverter customJwtAccessTokenConverter(){
		CustomJwtAccessTokenConverter customJwtAccessTokenConverter = new CustomJwtAccessTokenConverter();
		DefaultAccessTokenConverter tokenConverter = (DefaultAccessTokenConverter) customJwtAccessTokenConverter.getAccessTokenConverter();
		CustomUserAuthenticationConverter userConverter = new CustomUserAuthenticationConverter();
		userConverter.setUserFeignClient(userFeignClient);
		tokenConverter.setUserTokenConverter(userConverter);
		return customJwtAccessTokenConverter ;
	}
	
	@Bean
	public TokenStore tokenStore(CustomJwtAccessTokenConverter customJwtAccessTokenConverter) {
		return new JwtTokenStore(customJwtAccessTokenConverter);
	}

}