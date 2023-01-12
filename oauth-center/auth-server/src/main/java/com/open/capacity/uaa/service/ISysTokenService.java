package com.open.capacity.uaa.service;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Map;

public interface ISysTokenService {

	//通用校验
	public void preCheckClient(String clientId, String clientSecret);
	//模拟客户端模式
	public OAuth2AccessToken getClientTokenInfo(String clientId, String clientSecret);
	//模拟密码模式
	public OAuth2AccessToken getUserTokenInfo(String clientId, String clientSecret, String username, String password);
	//获取token
	public OAuth2AccessToken getTokenInfo(String access_token);
	// 自定义sso
    OAuth2AccessToken ssoSysLogin(String clientId, String clientSecret, Map<String, Object> params);
}
