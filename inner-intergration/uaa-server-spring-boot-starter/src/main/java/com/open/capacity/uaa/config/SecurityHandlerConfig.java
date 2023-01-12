package com.open.capacity.uaa.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.open.capacity.common.exception.RemoteCallException;
import com.open.capacity.uaa.common.exception.DefaultOAuth2Exception;
import com.open.capacity.uaa.common.exception.ForbiddenException;
import com.open.capacity.uaa.common.exception.InvalidException;
import com.open.capacity.uaa.common.exception.MethodNotAllowedException;
import com.open.capacity.uaa.common.exception.RemoteCallAuthException;
import com.open.capacity.uaa.common.exception.ServerErrorException;
import com.open.capacity.uaa.common.exception.UnauthorizedException;
import com.open.capacity.uaa.handler.OauthLogoutHandler;
import com.open.capacity.uaa.handler.OauthLogoutSuccessHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import lombok.extern.slf4j.Slf4j;

/**
 *  认证错误处理
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@Configuration
@SuppressWarnings("all")
public class SecurityHandlerConfig {
	@Bean
	public LogoutHandler logoutHandler() {
		return new OauthLogoutHandler();
	}

	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new OauthLogoutSuccessHandler();
	}

	@Bean
	public WebResponseExceptionTranslator webResponseExceptionTranslator() {
		return new DefaultWebResponseExceptionTranslator() {
			private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

			@Override
			public ResponseEntity<OAuth2Exception> translate(Exception e) {

				Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);

				Exception ase = (AuthenticationException) throwableAnalyzer
						.getFirstThrowableOfType(AuthenticationException.class, causeChain);
				if (ase != null) {
					return handleOAuth2Exception(new UnauthorizedException(e.getMessage(), e));
				}

				ase = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class,
						causeChain);
				if (ase != null) {
					return handleOAuth2Exception(new ForbiddenException(ase.getMessage(), ase));
				}

				ase = (InvalidGrantException) throwableAnalyzer.getFirstThrowableOfType(InvalidGrantException.class,
						causeChain);
				if (ase != null) {
					return handleOAuth2Exception(new InvalidException(ase.getMessage(), ase));
				}

				ase = (HttpRequestMethodNotSupportedException) throwableAnalyzer
						.getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
				if (ase != null) {
					return handleOAuth2Exception(new MethodNotAllowedException(ase.getMessage(), ase));
				}

				ase = (OAuth2Exception) throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);

				if (ase != null) {
					return handleOAuth2Exception((OAuth2Exception) ase);
				}
	 
				ase = (RemoteCallException) throwableAnalyzer.getFirstThrowableOfType(RemoteCallException.class,
						causeChain);
				if (ase != null) {
					return handleOAuth2Exception(new RemoteCallAuthException(ase.getMessage(), ase));
				}

				return handleOAuth2Exception(
						new ServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e));

			}

			private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) {

				int status = e.getHttpErrorCode();
				HttpHeaders headers = new HttpHeaders();
				headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
				headers.set(HttpHeaders.PRAGMA, "no-cache");
				if (status == HttpStatus.UNAUTHORIZED.value() || (e instanceof InsufficientScopeException)) {
					headers.set(HttpHeaders.WWW_AUTHENTICATE,
							String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, e.getSummary()));
				}
				// 客户端异常直接返回客户端,不然无法解析
				if (e instanceof ClientAuthenticationException) {
					return new ResponseEntity<>(new DefaultOAuth2Exception(e.getMessage(), e.getMessage() , status), headers, HttpStatus.valueOf(status));
				}
				return new ResponseEntity<>(new DefaultOAuth2Exception(e.getMessage(), e.getOAuth2ErrorCode() , status), headers,
						HttpStatus.valueOf(status));

			}
		};
	}

	/**
	 * 登陆成功
	 */
	@Bean
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return new SavedRequestAwareAuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				super.onAuthenticationSuccess(request, response, authentication);
			}
		};
	}
}
