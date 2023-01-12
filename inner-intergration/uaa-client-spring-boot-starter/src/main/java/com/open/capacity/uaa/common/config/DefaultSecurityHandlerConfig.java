package com.open.capacity.uaa.common.config;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.utils.MessageSourceUtil;
import com.open.capacity.common.utils.ResponseUtil;
import com.open.capacity.common.utils.SpringUtil;

/**
 * @author someday
 * @date 2018/7/25 9:36
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@SuppressWarnings("all")
public class DefaultSecurityHandlerConfig {
	@Resource
	private ObjectMapper objectMapper;

	/**
	 * 未登录，返回401
	 * 
	 * @return
	 */
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new AuthenticationEntryPoint() {

			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException authException) throws IOException, ServletException {
				String msg = authException.getMessage();
				if (authException instanceof InsufficientAuthenticationException) {
					Throwable cause = authException.getCause();
					if (cause instanceof InvalidTokenException) {
						msg = MessageSourceUtil.getAccessor().getMessage(
								"AbstractUserDetailsAuthenticationProvider.loginExpired", authException.getMessage());
					} else {
						msg = MessageSourceUtil.getAccessor().getMessage(
								"AbstractUserDetailsAuthenticationProvider.credentialsExpired",
								authException.getMessage());
					}
				}
				if (authException instanceof CredentialsExpiredException) {
					msg = MessageSourceUtil.getAccessor().getMessage(
							"AbstractUserDetailsAuthenticationProvider.credentialsExpired", authException.getMessage());
				}

				if (authException instanceof UsernameNotFoundException) {
					msg = MessageSourceUtil.getAccessor().getMessage(
							"AbstractUserDetailsAuthenticationProvider.noopBindAccount", authException.getMessage());
				}

				if (authException instanceof BadCredentialsException) {
					msg = MessageSourceUtil.getAccessor().getMessage(
							"AbstractUserDetailsAuthenticationProvider.badClientCredentials",
							authException.getMessage());
				}
				ResponseUtil.responseFailed(objectMapper, response, msg);
			}
		};
	}

	/**
	 * 登陆失败
	 * 
	 * @return
	 */
	@Bean
	public AuthenticationFailureHandler loginFailureHandler() {
		return new AuthenticationFailureHandler() {
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				String msg = null;
				if (exception instanceof BadCredentialsException) {
					msg = MessageSourceUtil.getAccessor().getMessage(
							"AbstractUserDetailsAuthenticationProvider.badClientCredentials", exception.getMessage());
				} else {
					msg = exception.getMessage();
				}
				ResponseUtil.responseFailed(SpringUtil.getBean(ObjectMapper.class), response, msg);
			}
		};

	}

	/**
	 * oauth2 方法级别的安全表达式
	 * @param applicationContext
	 * @return
	 */
	@Bean
	public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(
			ApplicationContext applicationContext) {
		OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
		expressionHandler.setApplicationContext(applicationContext);
		return expressionHandler;
	}

	/**
	 * 处理spring security oauth 处理失败返回消息格式
	 */
	@Bean
	public OAuth2AccessDeniedHandler oAuth2AccessDeniedHandler() {
		return new OAuth2AccessDeniedHandler() {

			@Override
			public void handle(HttpServletRequest request, HttpServletResponse response,
					AccessDeniedException authException) throws IOException, ServletException {
				ResponseUtil.responseFailed(objectMapper, response, authException.getMessage());
			}
		};
	}
}
