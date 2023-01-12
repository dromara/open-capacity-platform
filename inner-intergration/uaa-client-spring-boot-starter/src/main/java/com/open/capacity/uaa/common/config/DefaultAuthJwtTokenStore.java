package com.open.capacity.uaa.common.config;

import java.security.KeyPair;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.uaa.common.converter.CustomJwtAccessTokenConverter;
import com.open.capacity.uaa.common.converter.CustomUserAuthenticationConverter;

/**
 * uaa server 
 * TokenStore JWT 非对称加密之加密模块
 *
 */
@Configuration
@SuppressWarnings("all")
@ConditionalOnProperty(prefix = "ocp.oauth2.token.store", name = "type", havingValue = "authJwt")
public class DefaultAuthJwtTokenStore {

	@Resource
	private KeyProperties keyProperties;

	@Lazy
	@Autowired
	private UserFeignClient userFeignClient;

	@Bean
	public TokenStore tokenStore(CustomJwtAccessTokenConverter customJwtAccessTokenConverter) {
		return new JwtTokenStore(customJwtAccessTokenConverter);
	}

	@Bean
	public CustomJwtAccessTokenConverter customJwtAccessTokenConverter() {
		final CustomJwtAccessTokenConverter customJwtAccessTokenConverter = new CustomJwtAccessTokenConverter();
		KeyPair keyPair = new KeyStoreKeyFactory(keyProperties.getKeyStore().getLocation(),
				keyProperties.getKeyStore().getSecret().toCharArray())
						.getKeyPair(keyProperties.getKeyStore().getAlias());
		customJwtAccessTokenConverter.setKeyPair(keyPair);
		DefaultAccessTokenConverter tokenConverter = (DefaultAccessTokenConverter) customJwtAccessTokenConverter
				.getAccessTokenConverter();
		CustomUserAuthenticationConverter userConverter = new CustomUserAuthenticationConverter();
		userConverter.setUserFeignClient(userFeignClient);
		tokenConverter.setUserTokenConverter(userConverter);
		return customJwtAccessTokenConverter;
	}

}