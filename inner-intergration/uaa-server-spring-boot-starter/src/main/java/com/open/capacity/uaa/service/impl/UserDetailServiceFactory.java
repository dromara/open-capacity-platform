package com.open.capacity.uaa.service.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.uaa.common.util.AuthUtils;
import com.open.capacity.uaa.service.DefaultUserDetailsService;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户service工厂 
 * 插件模式
 * @author owen
 * @version 1.0
 * @date 2018/7/24
 */
@Slf4j
@Service
@EnablePluginRegistries(DefaultUserDetailsService.class)
public class UserDetailServiceFactory {
	private static final String ERROR_MSG = "找不到账号类型为 %s 的实现类";

	@Resource
	private List<DefaultUserDetailsService> userDetailsServices;

	@Autowired
	@Qualifier("defaultUserDetailsServiceRegistry")
	private PluginRegistry<DefaultUserDetailsService, String> registry;

	public DefaultUserDetailsService getService(Authentication authentication) {
		String accountType = AuthUtils.getAccountType(authentication);
		if (StrUtil.isEmpty(accountType)) {
			accountType = SecurityConstants.DEF_ACCOUNT_TYPE;
		}
		return this.getService(accountType);
	}

	public DefaultUserDetailsService getService(final String accountType) {

		return Optional.ofNullable(registry.getPluginFor(accountType))
				.orElseThrow(() -> new InternalAuthenticationServiceException(StrUtil.format(ERROR_MSG, accountType)));

	}

}
