package com.open.capacity.uaa.handler;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.uaa.common.util.AuthUtils;
import com.open.capacity.uaa.utils.UsernameHolder;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author someday
 * @date 2018/10/17
 * 
 */
@Slf4j
@SuppressWarnings("all")
public class OauthLogoutHandler implements LogoutHandler {
	@Resource
	private TokenStore tokenStore;

	@Resource
	private SecurityProperties securityProperties;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		deleteCookies(request, response);

		deleteAuthentication(request, response, authentication);
	}

	private void deleteCookies(HttpServletRequest request, HttpServletResponse response) {
		// 将子系统的cookie删掉
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
		}
	}

	private void deleteAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		Assert.notNull(tokenStore, "tokenStore must be set");
		String token = request.getParameter(CommonConstant.TOKEN);
		if (StrUtil.isEmpty(token)) {
			token = AuthUtils.extractToken(request);
		}
		if (StrUtil.isNotEmpty(token)) {
			if (securityProperties.getAuth().getUnifiedLogout()) {
				OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(token);
				UsernameHolder.setContext(oAuth2Authentication.getName());
			}

			OAuth2AccessToken existingAccessToken = tokenStore.readAccessToken(token);
			OAuth2RefreshToken refreshToken;
			if (existingAccessToken != null) {
				if (existingAccessToken.getRefreshToken() != null) {
					log.info("remove refreshToken!", existingAccessToken.getRefreshToken());
					refreshToken = existingAccessToken.getRefreshToken();
					tokenStore.removeRefreshToken(refreshToken);
				}
				log.info("remove existingAccessToken!", existingAccessToken);
				tokenStore.removeAccessToken(existingAccessToken);
			}
		}
	}
}
