package com.open.capacity.uaa.baidu;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.open.capacity.uaa.common.token.BaiduAuthenticationToken;

/**
 * 自定义认证处理器
 * @Author: owen
 * @Date: 2021/05/31/00:39
 * blog:https://blog.51cto.com/13005375 
 * code:https://gitee.com/owenwangwen/open-capacity-platform
 */
public class BaiduAuthenticationProvider implements AuthenticationProvider {
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String code = authentication.getName();
		// 调用微信登陆接口登陆成功自动生产token
		// 登陆成功后返回的openId
		String openId = "openId";
		return new BaiduAuthenticationToken(code, openId, Collections.emptyList());

	}

	@Override
	public boolean supports(Class<?> aClass) {
		/**
		 * providerManager会遍历所有 SecurityConfig中注册的provider集合
		 * 根据此方法返回true或false来决定由哪个provider 去校验请求过来的authentication
		 */
		return (BaiduAuthenticationToken.class.isAssignableFrom(aClass));

	}

}
