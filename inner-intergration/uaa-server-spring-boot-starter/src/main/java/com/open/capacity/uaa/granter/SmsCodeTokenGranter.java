package com.open.capacity.uaa.granter;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import com.open.capacity.uaa.common.token.SmsCodeAuthenticationToken;
import com.open.capacity.uaa.service.IValidateCodeService;

public class SmsCodeTokenGranter extends AbstractTokenGranter {

	private static final String GRANT_TYPE = "mobile_sms";

	private final AuthenticationManager authenticationManager;

	private final IValidateCodeService validateCodeService;

	public SmsCodeTokenGranter(AuthenticationManager authenticationManager, IValidateCodeService validateCodeService,
			AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory) {
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.validateCodeService = validateCodeService;
		this.authenticationManager = authenticationManager;

	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

		Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
		// 客户端提交的用户名
		String deviceId = parameters.get("deviceId");
		// 客户端提交的验证码
		String validCode = parameters.get("validCode");
		//校验验证码
		validateCodeService.validate(deviceId, validCode);
		Authentication userAuth = new SmsCodeAuthenticationToken(deviceId);
		// 当然该参数传null也行
		((AbstractAuthenticationToken) userAuth).setDetails(parameters);
		userAuth = authenticationManager.authenticate(userAuth);
		if (userAuth == null || !userAuth.isAuthenticated()) {
	            throw new InvalidGrantException("Could not authenticate mobile: " + deviceId);
	    }
		OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
		return new OAuth2Authentication(storedOAuth2Request, userAuth);
	}

}