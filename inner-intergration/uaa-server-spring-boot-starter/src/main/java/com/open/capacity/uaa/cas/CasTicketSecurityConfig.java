package com.open.capacity.uaa.cas;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;

import com.open.capacity.uaa.service.ICasOauthUserDetailService;
import com.open.capacity.uaa.service.impl.CasOauthUserDetailServiceImpl;

/**
 * @author owen
 * @date 2018/8/5 
 * 配置 ticket provider认证处理方法
 * blog:https://blog.51cto.com/13005375 
 * code:https://gitee.com/owenwangwen/open-capacity-platform 
 */
@Configuration
public class CasTicketSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	@Autowired(required = false)
	private CasProperties casProperties ;

	@Autowired
	private CasAuthenticationExternProvider casAuthenticationExternProvider;

	@Override
	public void configure(HttpSecurity http) {
		// mobile provider
		http.authenticationProvider(casAuthenticationExternProvider);
	}

	/**
	 * 加密方式进行存储PGT信息
	 * @return
	 */
	@Bean("pgtStorage")
	public ProxyGrantingTicketStorageImpl proxyGrantingTicketStorageImpl() {
		return new ProxyGrantingTicketStorageImpl();
	}

	/**
	 * cas认证
	 * @param thUserDetailsService
	 * @return
	 */
	@Bean("casAuthenticationProvider")
	public CasAuthenticationExternProvider casAuthenticationProvider(UserDetailsService userDetailsService) {
		CasAuthenticationExternProvider authenticationProvider = new CasAuthenticationExternProvider();
		authenticationProvider.setKey(casProperties.getKey());
		// 认证用户信息配置
		ICasOauthUserDetailService casOauthUserDetailService = new CasOauthUserDetailServiceImpl(userDetailsService);
		authenticationProvider.setAuthenticationUserDetailsService(casOauthUserDetailService);
		Cas20ProxyTicketValidator ticketValidator = new Cas20ProxyTicketValidator(casProperties.getServiceUrlPrefix());
		ticketValidator.setAcceptAnyProxy(true);// 允许所有代理回调链接
		ticketValidator.setProxyGrantingTicketStorage(proxyGrantingTicketStorageImpl());
		authenticationProvider.setTicketValidator(ticketValidator);
		authenticationProvider.setServiceProperties(serviceProperties());
		return authenticationProvider;
	}

	/**
	 * 取不到前端传的信息兜底配置url
	 * @return
	 */
	@Bean
	public ServiceProperties serviceProperties() {
		ServiceProperties serviceProperties = new ServiceProperties();
		serviceProperties.setService(casProperties.getCallbackUrl());
		serviceProperties.setAuthenticateAllArtifacts(true);
		return serviceProperties;
	}

}
