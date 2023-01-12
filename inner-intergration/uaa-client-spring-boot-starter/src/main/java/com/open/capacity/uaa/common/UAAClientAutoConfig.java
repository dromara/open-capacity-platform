package com.open.capacity.uaa.common;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.properties.BlackListProperties;
import com.open.capacity.common.properties.ExceptionNoticeProperties;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.common.properties.TokenStoreProperties;
import com.open.capacity.uaa.common.authorize.AuthorizeConfigManager;
import com.open.capacity.uaa.common.config.DefaultSecurityHandlerConfig;

/**
 * 鉴权自动配置
 * 
 * @author someday
 * @version 1.0
 * @date 2018/7/24 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@ComponentScan
@Configuration
@EnableConfigurationProperties({ BlackListProperties.class,SecurityProperties.class,TokenStoreProperties.class , KeyProperties.class })
public class UAAClientAutoConfig {

	@Configuration
	@EnableResourceServer
	@Import(DefaultSecurityHandlerConfig.class)
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
		@Autowired
		private TokenStore tokenStore;

		@Resource
		private AuthenticationEntryPoint authenticationEntryPoint;

		@Resource
		private OAuth2WebSecurityExpressionHandler expressionHandler;

		@Resource
		private OAuth2AccessDeniedHandler oAuth2AccessDeniedHandler;

		@Resource
		private TokenExtractor tokenExtractor;

		@Autowired
		private AuthorizeConfigManager authorizeConfigManager;

		/**
		 * 添加特定于资源服务器的属性
		 */
		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.tokenStore(tokenStore)
					// 标记以指示在这些资源上仅允许基于令牌的身份验证
					.stateless(true)
					// 认证异常流程处理返回
					.authenticationEntryPoint(authenticationEntryPoint).expressionHandler(expressionHandler)
					// 鉴权失败且主叫方已要求特定的内容类型响应
					.accessDeniedHandler(oAuth2AccessDeniedHandler)
					// 自定义token验证字段：先从access_token中取再从header中取
					.tokenExtractor(tokenExtractor);
		}

		/**
		 * 使用此配置安全资源的访问规则。
		 */
		@Override
		public void configure(HttpSecurity httpSecurity) throws Exception {

			httpSecurity.requestMatcher(
					/**
					 * 认证中心资源服务器二合一专用处理 判断来源请求是否包含oauth2授权信息
					 */
					new RequestMatcher() {
						private AntPathMatcher antPathMatcher = new AntPathMatcher();

						@Override
						public boolean matches(HttpServletRequest request) {
							// 请求参数中包含access_token参数
							if (request.getParameter(OAuth2AccessToken.ACCESS_TOKEN) != null) {
								return true;
							}
							// 头部的Authorization值以Bearer开头
							String auth = request.getHeader(CommonConstant.TOKEN_HEADER);
							if (auth != null) {
								if (auth.startsWith(OAuth2AccessToken.BEARER_TYPE)) {
									return true;
								}
							}
							return false;
						}
					}).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and().httpBasic()
					.disable().headers().xssProtection().and()
					.contentSecurityPolicy("form-action 'self'; report-uri /report; report-to csp-violation-report")
					// 支持iframe嵌入
					.and().frameOptions().disable()
					// CRSF禁用，因为不使用session，解决跨域
					.and().csrf().disable();

			// 门面设计url权限控制，默认是认证就通过，可以重写实现个性化
			authorizeConfigManager.config(httpSecurity.authorizeRequests());

		}

	}
}
