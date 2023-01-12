package com.open.capacity.uaa.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.compress.utils.Lists;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.context.SysUserContextHolder;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.SpringUtil;
import com.open.capacity.common.utils.TokenUtil;
import com.open.capacity.redis.repository.RedisRepository;
import com.open.capacity.uaa.common.token.CustomWebAuthenticationDetails;
import com.open.capacity.uaa.common.token.SsoSysAuthenticationToken;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证授权相关工具类
 *
 * @author someday
 * @date 2018/5/13
 */
@Slf4j
@UtilityClass
@SuppressWarnings("all")
public class AuthUtils {

	/**
	 * 获取requet(head/param)中的token
	 *
	 * @param request
	 * @return
	 */
	public String extractToken(HttpServletRequest request) {
		String token = extractHeaderToken(request);
		if (token == null) {
			token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
			if (token == null) {
				log.debug("Token not found in request parameters.  Not an OAuth2 request.");
			}
		}
		return token;
	}

	/**
	 * 解析head中的token
	 *
	 * @param request
	 * @return
	 */
	private String extractHeaderToken(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaders(CommonConstant.TOKEN_HEADER);
		while (headers.hasMoreElements()) {
			String value = headers.nextElement();
			if ((value.startsWith(OAuth2AccessToken.BEARER_TYPE))) {
				String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
				int commaIndex = authHeaderValue.indexOf(',');
				if (commaIndex > 0) {
					authHeaderValue = authHeaderValue.substring(0, commaIndex);
				}
				return authHeaderValue;
			}
		}
		return null;
	}

	/**
	 * 校验accessToken
	 */
	public SysUser checkAccessToken(HttpServletRequest request) {
		String accessToken = extractToken(request);
		return checkAccessToken(accessToken);
	}

	public SysUser checkAccessToken(String accessTokenValue) {
		TokenStore tokenStore = SpringUtil.getBean(TokenStore.class);
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
		if (accessToken == null || accessToken.getValue() == null) {
			throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
		} else if (accessToken.isExpired()) {
			tokenStore.removeAccessToken(accessToken);
			throw new InvalidTokenException("Access token expired: " + accessTokenValue);
		}
		OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
		if (result == null) {
			throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
		}
		return setContext(result);
	}

	/**
	 * 用户信息赋值 context 对象
	 */
	public SysUser setContext(Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication);
		SysUser user = getUser(authentication);
		SysUserContextHolder.setUser(user);
		return user;
	}

	/**
	 * *从header 请求中的clientId:clientSecret
	 */
	public String[] extractClient(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith(CommonConstant.BASIC_)) {
			throw new UnapprovedClientAuthenticationException("请求头中client信息为空");
		}
		return extractHeaderClient(header);
	}

	/**
	 * 从header 请求中的clientId:clientSecret
	 *
	 * @param header header中的参数
	 */
	public String[] extractHeaderClient(String header) {
		byte[] base64Client = header.substring(CommonConstant.BASIC_.length()).getBytes(StandardCharsets.UTF_8);
		byte[] decoded = Base64.getDecoder().decode(base64Client);
		String clientStr = new String(decoded, StandardCharsets.UTF_8);
		String[] clientArr = clientStr.split(":");
		if (clientArr.length != 2) {
			throw new RuntimeException("Invalid basic authentication token");
		}
		return clientArr;
	}

	/**
	 * 获取登陆的用户名
	 */
	public String getUsername(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String username = null;
		if (principal instanceof SysUser) {
			username = ((SysUser) principal).getUsername();
		} else if (principal instanceof String) {
			username = (String) principal;
		}
		return username;
	}

	/**
	 * 获取登陆的用户对象
	 */
	public SysUser getUser(Authentication authentication) {
		SysUser user = null;
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			Object principal = authentication.getPrincipal();
			// 客户端模式只返回一个clientId
			if (principal instanceof SysUser) {
				user = (SysUser) principal;
			}
		}
		return user;
	}

	/**
	 * 获取登陆的帐户类型
	 */
	public String getAccountType(Authentication authentication) {
		String accountType = null;
		if (authentication != null) {
			Object details = authentication.getDetails();
			if (details != null) {
				if (details instanceof CustomWebAuthenticationDetails) {
					CustomWebAuthenticationDetails detailsObj = (CustomWebAuthenticationDetails) details;
					accountType = detailsObj.getAccountType();
				} else if (details instanceof WebAuthenticationDetails) {
					accountType = SecurityConstants.DEF_ACCOUNT_TYPE;
				} else if (details instanceof OAuth2AuthenticationDetails) {
					accountType = SecurityConstants.DEF_ACCOUNT_TYPE;
				} else if (details instanceof SsoSysAuthenticationToken) {
                    accountType = SecurityConstants.DEF_ACCOUNT_TYPE;
                } else {
					Map<String, String> detailsMap = (Map<String, String>) details;
					if (detailsMap != null) {
						accountType = detailsMap.get(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME);
					}
				}
			}
		}
		return accountType;
	}
	/**
	 * 获取登陆的 LoginAppUser
	 *
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public LoginAppUser getLoginAppUser() {

		// 当OAuth2AuthenticationProcessingFilter设置当前登录时，直接返回
		// 强认证时处理
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof OAuth2Authentication) {
			OAuth2Authentication oAuth2Auth = (OAuth2Authentication) authentication;
			authentication = oAuth2Auth.getUserAuthentication();

			if (authentication instanceof UsernamePasswordAuthenticationToken) {
				UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;

				if (authenticationToken.getPrincipal() instanceof LoginAppUser) {
					return (LoginAppUser) authenticationToken.getPrincipal();
				} else if (authenticationToken.getPrincipal() instanceof Map) {

					LoginAppUser loginAppUser = BeanUtil.mapToBean((Map) authenticationToken.getPrincipal(),
							LoginAppUser.class, true);
					List<SysRole> roles = Lists.newArrayList();
					if (CollectionUtil.isNotEmpty(loginAppUser.getRoles())) {
						for (Iterator<SysRole> it = loginAppUser.getRoles().iterator(); it.hasNext();) {
							SysRole role = BeanUtil.mapToBean((Map) it.next(), SysRole.class, false);
							roles.add(role);
						}
					}
					loginAppUser.setRoles(roles);
					return loginAppUser;
				}
			} else if (authentication instanceof PreAuthenticatedAuthenticationToken) {
				// 刷新token方式
				PreAuthenticatedAuthenticationToken authenticationToken = (PreAuthenticatedAuthenticationToken) authentication;
				return (LoginAppUser) authenticationToken.getPrincipal();
			}
		}
		// 弱认证处理，当内部服务，不带token时，内部服务
		String accessToken = TokenUtil.getToken();
		if (accessToken != null) {
			RedisRepository redisRepository = SpringUtil.getBean(RedisRepository.class);
			LoginAppUser loginAppUser = (LoginAppUser) redisRepository.get(CommonConstant.TOKEN + ":" + accessToken);
			if (loginAppUser != null) {
				return loginAppUser;
			}
		}

		return null;
	}
}
