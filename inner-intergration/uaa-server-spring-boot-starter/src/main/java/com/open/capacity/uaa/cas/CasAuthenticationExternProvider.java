package com.open.capacity.uaa.cas;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.MapUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.cas.authentication.NullStatelessTicketCache;
import org.springframework.security.cas.authentication.StatelessTicketCache;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.open.capacity.uaa.common.token.CasOauthAuthenticationToken;

/**
 * @author owen
 * @date 2018/8/5 
 * 扩展cas 认证处理类
 * blog:https://blog.51cto.com/13005375 
 * code:https://gitee.com/owenwangwen/open-capacity-platform
 */
public class CasAuthenticationExternProvider extends CasAuthenticationProvider {

	private StatelessTicketCache statelessTicketCache = new NullStatelessTicketCache();
	private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
	private ServiceProperties serviceProperties;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!supports(authentication.getClass())) {
			return null;
		}
		boolean stateless = (authentication instanceof CasOauthAuthenticationToken
				&& CasAuthenticationFilter.CAS_STATELESS_IDENTIFIER.equals(authentication.getPrincipal()));

		CasAuthenticationToken result = null;
		// ticket是否重复验证，默认不可以重复验证
		if (stateless) {
			result = this.statelessTicketCache.getByTicketId(authentication.getCredentials().toString());
		}
		// 验证票据
		if (result == null) {
			result = this.validateTicket(authentication);
			result.setDetails(authentication.getDetails());
		}
		if (stateless) {
			// _cas_stateless_ 模式用于缓存当前票据认证结果
			this.statelessTicketCache.putTicketInCache(result);
		}
		return result;
	}

	/**
	 * 票据验证
	 * 
	 * @param authentication
	 * @return
	 * @throws AuthenticationException
	 */
	private CasAuthenticationToken validateTicket(final Authentication authentication) throws AuthenticationException {
		try {
			// 发送到远端cas服务器验证
			Assertion assertion = super.getTicketValidator().validate(authentication.getCredentials().toString(),
					getService(authentication));
			// 通过验证后的令牌根据用户名加载当前用户的信息
			UserDetails userDetails = loadUserByAssertion(assertion);
			this.userDetailsChecker.check(userDetails);
			return new CasAuthenticationToken(super.getKey(), userDetails, authentication.getCredentials(),
					this.authoritiesMapper.mapAuthorities(userDetails.getAuthorities()), userDetails, assertion);
		} catch (TicketValidationException ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setServiceProperties(final ServiceProperties serviceProperties) {
		this.serviceProperties = serviceProperties;
	}

	/**
	 * 获取cas server中的service信息
	 * 
	 * @param authentication
	 * @return
	 */
	private String getService(Authentication authentication) {
		String serviceUrl;
		if (authentication.getDetails() instanceof ServiceAuthenticationDetails) {
			return ((ServiceAuthenticationDetails) authentication.getDetails()).getServiceUrl();
		} else if (authentication.getDetails() instanceof Map) {
			return MapUtils.getString(
					Optional.ofNullable(((Map) authentication.getDetails())).orElse(Maps.newHashMap()), "serviceUrl",
					this.serviceProperties.getService());
		}
		Assert.state(this.serviceProperties != null,
				"serviceProperties cannot be null unless Authentication.getDetails() implements ServiceAuthenticationDetails.");
		Assert.state(this.serviceProperties.getService() != null,
				"serviceProperties.getService() cannot be null unless Authentication.getDetails() implements ServiceAuthenticationDetails.");
		serviceUrl = this.serviceProperties.getService();
		return serviceUrl;
	}
	/**
	 * providerManager会遍历所有 SecurityConfig中注册的provider集合
	 * 根据此方法返回true或false来决定由哪个provider 去校验请求过来的authentication
	 */
	@Override
	public boolean supports(final Class<?> authentication) {
		return (CasOauthAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
