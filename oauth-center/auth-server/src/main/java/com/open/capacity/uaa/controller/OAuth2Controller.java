package com.open.capacity.uaa.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.ResponseUtil;
import com.open.capacity.common.utils.StringUtil;
import com.open.capacity.common.utils.UUIDUtils;
import com.open.capacity.common.utils.UserUtils;
import com.open.capacity.uaa.common.util.AuthUtils;
import com.open.capacity.uaa.service.ISysTokenService;
import com.xkcoding.http.HttpUtil;
import com.xkcoding.http.support.httpclient.HttpClientImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;

/**
 * @author 作者 owen
 * @version 创建时间：2018年4月28日 下午2:18:54 类说明
 */
@Slf4j
@RestController
@Api(tags = "OAuth API")
@SuppressWarnings("all")
public class OAuth2Controller {

	@Autowired
	private ISysTokenService sysTokenService;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@RequestMapping("/oauth/render")
	@SneakyThrows
	public void render(HttpServletResponse response) {
		AuthRequest authRequest = getAuthRequest();
		response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
	}

	@RequestMapping("/oauth/callback")
	@SneakyThrows
	public Object callback(AuthCallback callback) {
		AuthRequest authRequest = getAuthRequest();
		return authRequest.login(callback);
	}
	private AuthRequest getAuthRequest() {
		
		AuthConfig authConfig = new AuthConfig();
		authConfig.setClientId("");
		authConfig.setClientSecret("");
		authConfig.setRedirectUri("http://localhost:8000/api-auth/oauth/callback");
		HttpUtil.setHttp(new HttpClientImpl());
		return new AuthGiteeRequest(authConfig);
	}

	@ApiOperation(value = "clientId获取token")
	@PostMapping("/oauth/client/token")
	public void getClientTokenInfo() {

		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		HttpServletResponse response = servletRequestAttributes.getResponse();
		try {
			String clientId = request.getHeader("client_id");
			String clientSecret = request.getHeader("client_secret");
			OAuth2AccessToken oAuth2AccessToken = sysTokenService.getClientTokenInfo(clientId, clientSecret);
			ResponseUtil.renderJson(response, oAuth2AccessToken);
		} catch (Exception e) {
			Map<String, String> rsp = new HashMap<>();
			rsp.put(CommonConstant.STATUS, HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());
			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());
		}
	}

	@ApiOperation(value = "用户名密码获取token")
	@PostMapping("/oauth/user/token")
	public void getUserTokenInfo(
			@ApiParam(required = true, name = "username", value = "账号") @RequestParam(value = "username") String username,
			@ApiParam(required = true, name = "password", value = "密码") @RequestParam(value = "password") String password) {

		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		HttpServletResponse response = servletRequestAttributes.getResponse();
		try {
			String clientId = request.getHeader("client_id");
			String clientSecret = request.getHeader("client_secret");
			OAuth2AccessToken oAuth2AccessToken = sysTokenService.getUserTokenInfo(clientId, clientSecret, username,
					password);
			ResponseUtil.renderJson(response, oAuth2AccessToken);
		} catch (Exception e) {
			Map<String, String> rsp = new HashMap<>();
			rsp.put(CommonConstant.STATUS, HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());
			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());
		}
	}

	@ApiOperation(value = "获取token信息")
	@PostMapping(value = "/oauth/get/token", params = "access_token")
	public OAuth2AccessToken getTokenInfo(String access_token) {
		return sysTokenService.getTokenInfo(access_token);

	}

	/**
	 * 当前登陆用户信息
	 * security获取当前登录用户的方法是SecurityContextHolder.getContext().getAuthentication()
	 * 这里的实现类是org.springframework.security.oauth2.provider.OAuth2Authentication
	 * 
	 * @return
	 */
	@ApiOperation(value = "当前登陆用户信息")
	@GetMapping(value = { "/oauth/userinfo" }, produces = "application/json") // 获取用户信息。/auth/user
	public Map<String, Object> getCurrentUserDetail(HttpServletRequest request) {
		Map<String, Object> userInfo = new HashMap<>();
		try {
			SysUser sysUser = UserUtils.getCurrentUser(request, false);
			userInfo.put(CommonConstant.STATUS, CommonConstant.SUCCESS);
			LoginAppUser loginUser = AuthUtils.getLoginAppUser();
			userInfo.put("user", loginUser);
			userInfo.put("username", loginUser.getUsername());
			userInfo.put("permissions", loginUser.getPermissions());
		} catch (Exception e) {
		}
		return userInfo;

	}

	/**
	 * 单点登录获取uuid（密码）
	 * 
	 * @param userName
	 * @return
	 */
	@GetMapping("/oauth/ssoBeforeLogin")
	public ResponseEntity ssoBeforeLogin(@RequestParam String userName, HttpServletRequest request) {
		try {
			if (StringUtil.isEmpty(userName)) {
				return ResponseEntity.failed("账号不能为空");
			}
			String uuid = UUIDUtils.getGUID32();
			redisTemplate.opsForValue().set(StringUtil.SSO_LOGIN_USER + userName + "_" + uuid, uuid);
			redisTemplate.expire(StringUtil.SSO_LOGIN_USER + userName + "_" + uuid, 60, TimeUnit.SECONDS); // 有效时间1分钟
			return ResponseEntity.succeed(uuid, "请在一分钟内登录系统");
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 自定义sso
	 * 
	 * @param params   client_id client_secret username password
	 * @param request
	 * @param response
	 */
	@PostMapping("/oauth/ssoSysLogin")
	public void ssoSysLogin(@RequestParam Map<String, Object> params, HttpServletRequest request,
			HttpServletResponse response) {
		String clientId = request.getParameter("client_id");
		String clientSecret = request.getParameter("client_secret");
		try {
			OAuth2AccessToken oAuth2AccessToken = sysTokenService.ssoSysLogin(clientId, clientSecret, params);
			ResponseUtil.renderJson(response, oAuth2AccessToken);
		} catch (Exception e) {
			Map<String, String> rsp = new HashMap<>();
			rsp.put(CommonConstant.STATUS, HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());
			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());
		}
	}
}
