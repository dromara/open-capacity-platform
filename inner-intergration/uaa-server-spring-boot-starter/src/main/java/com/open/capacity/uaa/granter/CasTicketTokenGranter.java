package com.open.capacity.uaa.granter;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import com.open.capacity.uaa.common.token.CasOauthAuthenticationToken;

/**
 * @author owen
 * @date 2018/8/5
 * oauth: ticket票据模式
 */
@SuppressWarnings("all")
public class CasTicketTokenGranter extends AbstractTokenGranter {

	private static final String GRANT_TYPE = "cas_ticket";
	private static final String CAS_IDENTIFIER = "cas_identifier" ;
	private static final String TICKET = "ticket";
	private final AuthenticationManager authenticationManager;

	public CasTicketTokenGranter(AuthenticationManager authenticationManager,
			AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory) {
		this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
	}

	protected CasTicketTokenGranter(AuthenticationManager authenticationManager,
			AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory, String grantType) {
		super(tokenServices, clientDetailsService, requestFactory, grantType);
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		// CAS特定主体,标识ticket是否可以重复利用 ，默认可以重复利用
		String casStatefulIdentifier =  MapUtils.getString(parameters,CAS_IDENTIFIER,CasAuthenticationFilter.CAS_STATEFUL_IDENTIFIER) ;
		String ticket = parameters.get(TICKET);
		if (ticket == null) {
			throw new InvalidRequestException("A cas ticket must be supplied.");
		}
		Authentication userAuth = new CasOauthAuthenticationToken(casStatefulIdentifier, ticket);
		((AbstractAuthenticationToken) userAuth).setDetails(parameters);
		try {
			//启用根据CasOauthAuthenticationToken类型启用cas provider认证
			userAuth = authenticationManager.authenticate(userAuth);
		} catch (AccountStatusException ase) {
			throw new InvalidGrantException(ase.getMessage());
		} catch (BadCredentialsException e) {
			throw new InvalidGrantException(e.getMessage());
		}
		if (userAuth == null || !userAuth.isAuthenticated()) {
			throw new InvalidGrantException("Could not authenticate ticket: " + ticket);
		}
		//构建oauth token 信息
		OAuth2Request storedOAuthRequest = getRequestFactory().createOAuth2Request(client, tokenRequest);
		return new OAuth2Authentication(storedOAuthRequest, userAuth);
	}
}
