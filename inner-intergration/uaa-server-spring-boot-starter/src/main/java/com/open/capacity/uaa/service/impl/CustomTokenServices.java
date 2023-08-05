package com.open.capacity.uaa.service.impl;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.context.TenantContextHolder;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.properties.AuthProperties;
import com.open.capacity.common.utils.SpringUtil;

/**
 * 重写 DefaultTokenServices，实现登录同应用同账号互踢
 *
 * @author someday
 * @date 2018/1/28
 */
@SuppressWarnings("all")
public class CustomTokenServices extends DefaultTokenServices {
	private TokenStore tokenStore;
	private TokenEnhancer accessTokenEnhancer;
	private AuthenticationManager authenticationManager;
	private boolean supportRefreshToken = false;
	private boolean reuseRefreshToken = true;

	/**
	 * 是否登录同应用同账号互踢
	 */
	private final AuthProperties authProperties;

	public CustomTokenServices(AuthProperties auth) {
		this.authProperties = auth;
	}

	@Override
	@Transactional
	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
		OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);
		OAuth2RefreshToken refreshToken = null;
		if (existingAccessToken != null) {
			if (authProperties.getIsSingleLogin()) {
				kickOutUser(authentication, existingAccessToken);
			} else if (existingAccessToken.isExpired()) {
				if (existingAccessToken.getRefreshToken() != null) {
					refreshToken = existingAccessToken.getRefreshToken();
					// The token store could remove the refresh token when the
					// access token is removed, but we want to
					// be sure...
					tokenStore.removeRefreshToken(refreshToken);
				}
				tokenStore.removeAccessToken(existingAccessToken);
			} else if (authProperties.getIsShareToken()) {
				// oidc每次授权都刷新id_token
				existingAccessToken = refreshIdToken(existingAccessToken, authentication);
				// Re-store the access token in case the authentication has changed
				tokenStore.storeAccessToken(existingAccessToken, authentication);
				return existingAccessToken;
			}
		}

		// Only create a new refresh token if there wasn't an existing one
		// associated with an expired access token.
		// Clients might be holding existing refresh tokens, so we re-use it in
		// the case that the old access token
		// expired.
		if (refreshToken == null) {
			refreshToken = createRefreshToken(authentication);
		}
		// But the refresh token itself might need to be re-issued if it has
		// expired.
		else if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
			ExpiringOAuth2RefreshToken expiring = (ExpiringOAuth2RefreshToken) refreshToken;
			if (System.currentTimeMillis() > expiring.getExpiration().getTime()) {
				refreshToken = createRefreshToken(authentication);
			}
		}

		OAuth2AccessToken accessToken = createAccessToken(authentication, refreshToken);
		tokenStore.storeAccessToken(accessToken, authentication);
		// In case it was modified
		refreshToken = accessToken.getRefreshToken();
		if (refreshToken != null) {
			tokenStore.storeRefreshToken(refreshToken, authentication);
		}
		return accessToken;

	}

	/**
	 * 获取用户名
	 * 
	 * @param authentication
	 * @return
	 */
	private String getLoginUserName(OAuth2Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String username = "";
		if (principal instanceof String) {
			username = (String) principal;
		} else if (principal instanceof LoginAppUser) {
			username = ((LoginAppUser) authentication.getPrincipal()).getUsername();
		}
		return username;
	}

	/**
	 * 踢出用户
	 * 
	 * @param authentication
	 * @param existingAccessToken
	 */
	private void kickOutUser(OAuth2Authentication authentication, OAuth2AccessToken existingAccessToken) {
		// 发送websocket通知
		SimpMessagingTemplate simpMessagingTemplate = SpringUtil.getBean(SimpMessagingTemplate.class);
		simpMessagingTemplate.convertAndSendToUser(existingAccessToken.getValue(), "/remind", "当前用户在别处登录");
		if (existingAccessToken.getRefreshToken() != null) {
			tokenStore.removeRefreshToken(existingAccessToken.getRefreshToken());
		}
		tokenStore.removeAccessToken(existingAccessToken);
	}

	/**
	 * oidc每次授权都刷新id_token
	 * 
	 * @param token          已存在的token
	 * @param authentication 认证信息
	 */
	private OAuth2AccessToken refreshIdToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		Set<String> responseTypes = authentication.getOAuth2Request().getResponseTypes();
		if (accessTokenEnhancer != null && responseTypes.contains(SecurityConstants.ID_TOKEN)) {
			return accessTokenEnhancer.enhance(token, authentication);
		}
		return token;
	}

	private OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
		if (!isSupportRefreshToken(authentication.getOAuth2Request())) {
			return null;
		}
		int validitySeconds = getRefreshTokenValiditySeconds(authentication.getOAuth2Request());
		String value = UUID.randomUUID().toString();
		if (validitySeconds > 0) {
			return new DefaultExpiringOAuth2RefreshToken(value,
					new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
		}
		return new DefaultOAuth2RefreshToken(value);
	}

	private OAuth2AccessToken createAccessToken(OAuth2Authentication authentication, OAuth2RefreshToken refreshToken) {
		DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
		int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
		if (validitySeconds > 0) {
			token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
		}
		token.setRefreshToken(refreshToken);
		token.setScope(authentication.getOAuth2Request().getScope());

		return accessTokenEnhancer != null ? accessTokenEnhancer.enhance(token, authentication) : token;
	}

	@Override
	@Transactional(noRollbackFor = { InvalidTokenException.class, InvalidGrantException.class })
	public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
			throws AuthenticationException {
		if (!supportRefreshToken) {
			throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
		}

		OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refreshTokenValue);
		if (refreshToken == null) {
			throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
		}

		OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
		if (this.authenticationManager != null && !authentication.isClientOnly()) {
			AbstractAuthenticationToken userAuthentication = (AbstractAuthenticationToken) authentication
					.getUserAuthentication();
			Object usesrDetails = userAuthentication.getDetails();
			// The client has already been authenticated, but the user authentication might
			// be old now, so give it a
			// chance to re-authenticate.
			Authentication user = new PreAuthenticatedAuthenticationToken(userAuthentication, "",
					authentication.getAuthorities());
			user = authenticationManager.authenticate(user);
			// 保存账号类型
			((PreAuthenticatedAuthenticationToken) user).setDetails(usesrDetails);
			Object details = authentication.getDetails();
			authentication = new OAuth2Authentication(authentication.getOAuth2Request(), user);
			authentication.setDetails(details);
		}
		String clientId = authentication.getOAuth2Request().getClientId();
		if (clientId == null || !clientId.equals(tokenRequest.getClientId())) {
			throw new InvalidGrantException("Wrong client for this refresh token: " + refreshTokenValue);
		}

		// clear out any access tokens already associated with the refresh
		// token.
		tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);

		if (isExpired(refreshToken)) {
			tokenStore.removeRefreshToken(refreshToken);
			throw new InvalidTokenException("Invalid refresh token (expired): " + refreshToken);
		}

		authentication = createRefreshedAuthentication(authentication, tokenRequest);
		if (!reuseRefreshToken) {
			tokenStore.removeRefreshToken(refreshToken);
			refreshToken = createRefreshToken(authentication);
		}

		OAuth2AccessToken accessToken = createAccessToken(authentication, refreshToken);
		tokenStore.storeAccessToken(accessToken, authentication);
		if (!reuseRefreshToken) {
			tokenStore.storeRefreshToken(accessToken.getRefreshToken(), authentication);
		}
		return accessToken;
	}

	private OAuth2Authentication createRefreshedAuthentication(OAuth2Authentication authentication,
			TokenRequest request) {
		OAuth2Authentication narrowed;
		Set<String> scope = request.getScope();
		OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(request);
		if (scope != null && !scope.isEmpty()) {
			Set<String> originalScope = clientAuth.getScope();
			if (originalScope == null || !originalScope.containsAll(scope)) {
				throw new InvalidScopeException(
						"Unable to narrow the scope of the client authentication to " + scope + ".", originalScope);
			} else {
				clientAuth = clientAuth.narrowScope(scope);
			}
		}
		narrowed = new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
		return narrowed;
	}

	@Override
	public void setTokenStore(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
		super.setTokenStore(tokenStore);
	}

	@Override
	public void setTokenEnhancer(TokenEnhancer accessTokenEnhancer) {
		this.accessTokenEnhancer = accessTokenEnhancer;
		super.setTokenEnhancer(accessTokenEnhancer);
	}

	@Override
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		super.setAuthenticationManager(authenticationManager);
	}

	/**
	 * Whether to support the refresh token.
	 *
	 * @param supportRefreshToken Whether to support the refresh token.
	 */
	@Override
	public void setSupportRefreshToken(boolean supportRefreshToken) {
		this.supportRefreshToken = supportRefreshToken;
		super.setSupportRefreshToken(supportRefreshToken);
	}

	/**
	 * Whether to reuse refresh tokens (until expired).
	 *
	 * @param reuseRefreshToken Whether to reuse refresh tokens (until expired).
	 */
	@Override
	public void setReuseRefreshToken(boolean reuseRefreshToken) {
		this.reuseRefreshToken = reuseRefreshToken;
		super.setReuseRefreshToken(reuseRefreshToken);
	}
}
