package com.open.capacity.uaa.loginsso;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.utils.StringUtil;
import com.open.capacity.redis.repository.RedisRepository;
import com.open.capacity.uaa.common.token.SsoSysAuthenticationToken;
import com.open.capacity.uaa.service.impl.UserDetailServiceFactory;

/**
 * @author xhq
 * @title SsoAuthenticationProvider 自定义单点登录
 * @create 9:13 2022/9/9
 */
@Component
public class SsoAuthenticationProvider implements AuthenticationProvider {

	private static final String ACCOUNT_TYPE = SecurityConstants.DEF_ACCOUNT_TYPE;

	@Autowired
	private RedisRepository redisRepository;

	@Resource
	private UserDetailServiceFactory userDetailsServiceFactory;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		SsoSysAuthenticationToken token = (SsoSysAuthenticationToken) authentication;
		String username = (String) token.getPrincipal();
		// 校验传过来的密码是否跟redis中相同
		String password = String.valueOf(token.getParams().get("password")); // 单点登录传过来的密码
		String value = String.valueOf(redisRepository.get(StringUtil.SSO_LOGIN_USER + username + "_" + password)); // A系统调用平台生成的UUID
		if (StringUtil.isEmpty(value) || StringUtil.isEmpty(password) || !value.equals(password)) { // 比对是否相同
			redisRepository.del(StringUtil.SSO_LOGIN_USER + username + "_" + password); // 删除随机数，防止其他人冒用链接登录系统
			throw new AuthenticationCredentialsNotFoundException("用户名或密码错误");
		}
		UserDetails userDetails = userDetailsServiceFactory.getService(ACCOUNT_TYPE).loadUserByUsername(username);
		if (userDetails == null) {
			redisRepository.del(StringUtil.SSO_LOGIN_USER + username + "_" + password); // 删除随机数，防止其他人冒用链接登录系统
			throw new AuthenticationCredentialsNotFoundException("用户名或密码错误");
		} else if (!userDetails.isEnabled()) {
			redisRepository.del(StringUtil.SSO_LOGIN_USER + username + "_" + password); // 删除随机数，防止其他人冒用链接登录系统
			throw new DisabledException("用户已禁用");
		}
		redisRepository.del(StringUtil.SSO_LOGIN_USER + username + "_" + password); // 删除随机数，防止其他人冒用链接登录系统
		LoginAppUser loginAppUser = (LoginAppUser) userDetails;
		loginAppUser.setPassword(password);
		userDetails = (UserDetails) loginAppUser;
		SsoSysAuthenticationToken authenticationResult = new SsoSysAuthenticationToken(userDetails.getAuthorities(),
				userDetails);
		// 需要把未认证中的一些信息copy到已认证的token中
		authenticationResult.setDetails(token);
		return authenticationResult;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return SsoSysAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
