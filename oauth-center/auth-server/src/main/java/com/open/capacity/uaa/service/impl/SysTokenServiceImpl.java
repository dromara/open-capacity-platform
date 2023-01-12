package com.open.capacity.uaa.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.utils.SpringUtil;
import com.open.capacity.common.utils.StringUtil;
import com.open.capacity.uaa.common.token.SsoSysAuthenticationToken;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import com.open.capacity.uaa.service.ISysTokenService;

import javax.annotation.Resource;

@Service
public class SysTokenServiceImpl implements ISysTokenService {

	@Autowired
	private RedisClientDetailsService redisClientDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Lazy
	@Autowired
	private TokenGranter tokenGranter;
	@Lazy
	@Autowired
	private TokenStore tokenStore;
	@Lazy
	@Resource
	private DefaultTokenServices tokenServices;

	@Override
	public void preCheckClient(String clientId, String clientSecret) {
		if (clientId == null || "".equals(clientId)) {
			throw new UnapprovedClientAuthenticationException("请求参数中无clientId信息");
		}

		if (clientSecret == null || "".equals(clientSecret)) {
			throw new UnapprovedClientAuthenticationException("请求参数中无clientSecret信息");
		}
	}

	public OAuth2AccessToken getClientTokenInfo(String clientId, String clientSecret) {

		OAuth2AccessToken oauth2AccessToken = null;
		this.preCheckClient(clientId, clientSecret);
		ClientDetails clientDetails = redisClientDetailsService.loadClientByClientId(clientId);

		if (clientDetails == null) {
			throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
		} else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
			throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
		}

		Map<String, String> map = new HashMap<>();
		map.put("client_secret", clientSecret);
		map.put("client_id", clientId);
		map.put("grant_type", "client_credentials");

		TokenRequest tokenRequest = new TokenRequest(map, clientId, clientDetails.getScope(), "client_credentials");

		oauth2AccessToken = tokenGranter.grant("client_credentials", tokenRequest);

		return oauth2AccessToken;

	}

	public OAuth2AccessToken getUserTokenInfo(String clientId, String clientSecret, String username, String password) {

		OAuth2AccessToken oauth2AccessToken = null;
		this.preCheckClient(clientId, clientSecret);
		ClientDetails clientDetails = redisClientDetailsService.loadClientByClientId(clientId);

		if (clientDetails == null) {
			throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
		} else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
			throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
		}

		Map<String, String> map = new HashMap<>();
		map.put("client_secret", clientSecret);
		map.put("client_id", clientId);
		map.put("grant_type", "password");
		map.put("username", username);
		map.put("password", password);

		TokenRequest tokenRequest = new TokenRequest(map, clientId, clientDetails.getScope(), "password");

		oauth2AccessToken = tokenGranter.grant("password", tokenRequest);

		return oauth2AccessToken;
	}

	@Override
	public OAuth2AccessToken getTokenInfo(String access_token) {
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(access_token);
		return accessToken;

	}

	// 自定义sso
	@Override
	public OAuth2AccessToken ssoSysLogin(String clientId, String clientSecret, Map<String, Object> params) {
		String username = String.valueOf(params.get("txt3")), password = String.valueOf(params.get("txt4"));
		if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
			throw new BusinessException("用户名或密码错误");
		}
		this.preCheckClient(clientId, clientSecret);
		ClientDetails clientDetails = redisClientDetailsService.loadClientByClientId(clientId);
		if (clientDetails == null) {
			throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
		} else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
			throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
		}
		TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(),
				"customer");  // client_credentials
		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
		String principal = username;
		SsoSysAuthenticationToken token = new SsoSysAuthenticationToken(null, principal, params);
		AuthenticationManager authenticationManager = SpringUtil.getBean(AuthenticationManager.class);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
		OAuth2AccessToken oAuth2AccessToken = tokenServices.createAccessToken(oAuth2Authentication);
		oAuth2Authentication.setAuthenticated(true);
		return oAuth2AccessToken;
	}

}
