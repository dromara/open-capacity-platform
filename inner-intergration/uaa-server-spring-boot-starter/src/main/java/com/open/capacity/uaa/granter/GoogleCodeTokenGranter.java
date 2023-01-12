package com.open.capacity.uaa.granter;

import com.open.capacity.uaa.service.IValidateCodeService;
import org.springframework.security.authentication.*;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * password添加谷歌动态令牌校验
 *
 * @author owen
 * @date 2018/7/11
 */
@SuppressWarnings("all")
public class GoogleCodeTokenGranter extends ResourceOwnerPasswordTokenGranter {
	private static final String GRANT_TYPE = "password_goole";

	private final IValidateCodeService validateCodeService;

	public GoogleCodeTokenGranter(AuthenticationManager authenticationManager,
			AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory, IValidateCodeService validateCodeService) {
		super(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.validateCodeService = validateCodeService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		String deviceId = parameters.get("deviceId");
		String validCode = parameters.get("validCode");
		// 校验二维码动态口令
		validateCodeService.validateDynamicToken(deviceId, validCode);

		return super.getOAuth2Authentication(client, tokenRequest);
	}
}
