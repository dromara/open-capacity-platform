package com.open.capacity.uaa.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.open.capacity.uaa.common.constants.IdTokenClaimNames;
import com.open.capacity.uaa.common.service.IClientService;
import com.open.capacity.uaa.common.util.AuthUtils;
import com.open.capacity.uaa.granter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.face.FaceRecognitionV3Template;
import com.open.capacity.common.model.Client;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.common.properties.TokenStoreProperties;
import com.open.capacity.common.utils.WebUtils;
import com.open.capacity.uaa.service.IValidateCodeService;
import com.open.capacity.uaa.service.impl.CustomTokenServices;
import com.open.capacity.uaa.service.impl.UserDetailServiceFactory;
import com.open.capacity.uaa.service.impl.UserDetailsByNameServiceFactoryWrapper;
import com.open.capacity.uaa.utils.OidcIdTokenBuilder;

import cn.hutool.core.util.StrUtil;

/**
 * token授权模式配置类
 * 
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51 blog: https://blog.51cto.com/13005375
 *          code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
@SuppressWarnings("all")
public class TokenGranterConfig {
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Resource
	private UserDetailServiceFactory userDetailsServiceFactory;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenStore tokenStore;

	@Lazy
	@Autowired
	private TokenEnhancer tokenEnhancer;

	@Autowired
	private IValidateCodeService validateCodeService;

	@Autowired
	private RandomValueAuthorizationCodeServices authorizationCodeServices;

	@Autowired(required = false)
	private FaceRecognitionV3Template faceRecognitionV3Template;

	private boolean reuseRefreshToken = true;

	private AuthorizationServerTokenServices tokenServices;

	private TokenGranter tokenGranter;

	@Resource
	private SecurityProperties securityProperties;

	/**
	 * 授权模式
	 */
	@Bean
	@ConditionalOnMissingBean
	public TokenGranter tokenGranter(DefaultTokenServices tokenServices) {
		if (tokenGranter == null) {
			tokenGranter = new TokenGranter() {
				private CompositeTokenGranter delegate;

				@Override
				public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
					if (delegate == null) {
						delegate = new CompositeTokenGranter(getAllTokenGranters(tokenServices));
					}
					return delegate.grant(grantType, tokenRequest);
				}
			};
		}
		return tokenGranter;
	}

	/**
	 * 所有授权模式：默认的5种模式 + 自定义的模式
	 */
	protected List<TokenGranter> getAllTokenGranters(DefaultTokenServices tokenServices) {
		AuthorizationCodeServices authorizationCodeServices = authorizationCodeServices();
		OAuth2RequestFactory requestFactory = requestFactory();
		// 获取默认的授权模式
		List<TokenGranter> tokenGranters = getDefaultTokenGranters(tokenServices, authorizationCodeServices,
				requestFactory);
		if (authenticationManager != null) {
			// 添加密码加图形验证码模式
			tokenGranters.add(new PwdImgCodeTokenGranter(authenticationManager, tokenServices, clientDetailsService,
					requestFactory, validateCodeService));
			// 添加密码smkey模式
			tokenGranters.add(new PwdSmKeyTokenGranter(authenticationManager, tokenServices, clientDetailsService,
					requestFactory, validateCodeService));
			// 添加密码加谷歌动态令牌校验
			tokenGranters.add(new GoogleCodeTokenGranter(authenticationManager, tokenServices, clientDetailsService,
					requestFactory, validateCodeService));
			// 添加openId模式
			tokenGranters.add(
					new OpenIdTokenGranter(authenticationManager, tokenServices, clientDetailsService, requestFactory));
			// 添加手机号加密码授权模式
			tokenGranters.add(new MobilePwdTokenGranter(authenticationManager, tokenServices, clientDetailsService,
					requestFactory));
			// 添加手机号加短信验证码授权模式
			tokenGranters.add(new SmsCodeTokenGranter(authenticationManager,validateCodeService, tokenServices, clientDetailsService,
								requestFactory));
			// 添加cas授权模式
			tokenGranters.add(new CasTicketTokenGranter(authenticationManager, tokenServices, clientDetailsService,
					requestFactory));
			// 添加人脸识别授权模式
			tokenGranters.add(new FaceIdTokenGranter(faceRecognitionV3Template, authenticationManager, tokenServices,
					clientDetailsService, requestFactory));
			// 自定义单点登录sso
			tokenGranters
					.add(new SsoPwdGranter(authenticationManager, tokenServices, clientDetailsService, requestFactory));
		}
		return tokenGranters;
	}

	/**
	 * 默认的授权模式
	 */
	private List<TokenGranter> getDefaultTokenGranters(AuthorizationServerTokenServices tokenServices,
			AuthorizationCodeServices authorizationCodeServices, OAuth2RequestFactory requestFactory) {
		List<TokenGranter> tokenGranters = new ArrayList<>();
		// 添加授权码模式
		tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices,
				clientDetailsService, requestFactory));
		// 添加刷新令牌的模式
		tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetailsService, requestFactory));
		// 添加简易授权模式
		tokenGranters.add(new ImplicitTokenGranter(tokenServices, clientDetailsService, requestFactory));
		// 添加客户端模式
		tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, requestFactory));
		if (authenticationManager != null) {
			// 添加密码模式
			tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices,
					clientDetailsService, requestFactory));
		}
		return tokenGranters;
	}

	private AuthorizationCodeServices authorizationCodeServices() {
		if (authorizationCodeServices == null) {
			authorizationCodeServices = new InMemoryAuthorizationCodeServices();
		}
		return authorizationCodeServices;
	}

	private OAuth2RequestFactory requestFactory() {
		return new DefaultOAuth2RequestFactory(clientDetailsService);
	}

	@Bean
	@ConditionalOnMissingBean
	protected DefaultTokenServices createDefaultTokenServices() {
		DefaultTokenServices tokenServices = new CustomTokenServices(securityProperties.getAuth());
		tokenServices.setTokenStore(tokenStore);
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setReuseRefreshToken(reuseRefreshToken);
		tokenServices.setClientDetailsService(clientDetailsService);
		tokenServices.setTokenEnhancer(tokenEnhancer);
		addUserDetailsService(tokenServices);
		return tokenServices;
	}

	@Bean
	public TokenEnhancer tokenEnhancer(@Autowired(required = false) KeyProperties keyProperties,
			IClientService clientService, TokenStoreProperties tokenStoreProperties) {
		return (accessToken, authentication) -> {

			if (SecurityConstants.CLIENT_CREDENTIALS.equals(authentication.getOAuth2Request().getGrantType())) {
				return accessToken;
			}

			Set<String> responseTypes = authentication.getOAuth2Request().getResponseTypes();
			Map<String, Object> additionalInfo = new HashMap<>(3);
			String accountType = AuthUtils.getAccountType(authentication.getUserAuthentication());
			if (StrUtil.isNotEmpty(accountType)) {
				additionalInfo.put(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME, accountType);
			}
			if (responseTypes.contains(SecurityConstants.ID_TOKEN) || "jwt".equals(tokenStoreProperties.getType())) {
				Object principal = authentication.getPrincipal();
				// 增加id参数
				if (principal instanceof SysUser) {
					SysUser user = (SysUser) principal;
					if (responseTypes.contains(SecurityConstants.ID_TOKEN)) {
						// 生成id_token
						setIdToken(additionalInfo, authentication, keyProperties, clientService, user);
					}
					if ("jwt".equals(tokenStoreProperties.getType())) {
						additionalInfo.put("id", user.getId());
					}
				}
			}
			additionalInfo.put(SecurityConstants.CLIENT_IP, WebUtils.getIP());
			additionalInfo.put(CommonConstant.TENANT_ID_PARAM, authentication.getOAuth2Request().getClientId());
			additionalInfo.put("license", "made by ocp ");

			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
			return accessToken;
		};
	}

	/**
	 * 生成id_token
	 * 
	 * @param additionalInfo 存储token附加信息对象
	 * @param authentication 授权对象
	 * @param keyProperties  密钥
	 * @param clientService  应用service
	 */
	private void setIdToken(Map<String, Object> additionalInfo, OAuth2Authentication authentication,
			KeyProperties keyProperties, IClientService clientService, SysUser user) {
		String clientId = authentication.getOAuth2Request().getClientId();
		Client client = clientService.loadClientByClientId(clientId);
		if (client.getSupportIdToken()) {
			String nonce = authentication.getOAuth2Request().getRequestParameters().get(IdTokenClaimNames.NONCE);
			long now = System.currentTimeMillis();
			long expiresAt = System.currentTimeMillis() + client.getIdTokenValiditySeconds() * 1000;
			String idToken = OidcIdTokenBuilder.builder(keyProperties).issuer(SecurityConstants.ISS).issuedAt(now)
					.expiresAt(expiresAt).subject(String.valueOf(user.getId())).name(user.getNickname())
					.loginName(user.getUsername()).picture(user.getHeadImgUrl()).audience(clientId).nonce(nonce)
					.build();

			additionalInfo.put(SecurityConstants.ID_TOKEN, idToken);
		}
	}

	/**
	 * 新增前后端用户处理
	 * @param tokenServices
	 */
	private void addUserDetailsService(DefaultTokenServices tokenServices) {
		if (this.userDetailsServiceFactory != null) {
			PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
			provider.setPreAuthenticatedUserDetailsService(
					new UserDetailsByNameServiceFactoryWrapper<>(this.userDetailsServiceFactory));
			tokenServices.setAuthenticationManager(new ProviderManager(Collections.singletonList(provider)));
		}
	}
}
