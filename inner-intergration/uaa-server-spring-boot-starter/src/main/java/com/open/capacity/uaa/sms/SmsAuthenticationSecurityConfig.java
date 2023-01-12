package com.open.capacity.uaa.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

import com.open.capacity.uaa.service.impl.UserDetailServiceFactory;

/**
 * 人脸识别的相关处理配置
 * @author someday
 */
@Component
public class SmsAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	@Autowired
	private UserDetailServiceFactory userDetailsServiceFactory;
	@Override
	public void configure(HttpSecurity http) {
		// sms provider
		SmsAuthenticationProvider provider = new SmsAuthenticationProvider();
		provider.setUserDetailsServiceFactory(userDetailsServiceFactory);
		http.authenticationProvider(provider);
	}
}
