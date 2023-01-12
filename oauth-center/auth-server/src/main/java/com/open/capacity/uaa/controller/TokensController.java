package com.open.capacity.uaa.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.uaa.common.util.AuthUtils;
import com.open.capacity.uaa.model.TokenVo;
import com.open.capacity.uaa.service.ITokensService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * token管理接口
 *
 * @author owen
 */
@Api(tags = "Token管理")
@Slf4j
@RestController
@RequestMapping("/tokens")
public class TokensController {
	@Resource
	private ITokensService tokensService;

	@Resource
	private ClientDetailsService clientDetailsService;

	@Resource
	private PasswordEncoder passwordEncoder;

	@GetMapping("")
	@ApiOperation(value = "token列表")
	public PageResult<TokenVo> list(@RequestParam Map<String, Object> params, String tenantId) {
		return tokensService.listTokens(params, tenantId);
	}

	@GetMapping("/key")
	@ApiOperation(value = "获取jwt密钥")
	public ResponseEntity<String> key(HttpServletRequest request) {
		try {
			String[] clientArr = AuthUtils.extractClient(request);
			ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientArr[0]);
			if (clientDetails == null || !passwordEncoder.matches(clientArr[1], clientDetails.getClientSecret())) {
				throw new BadCredentialsException("应用ID或密码错误");
			}
		} catch (AuthenticationException ae) {
			return ResponseEntity.failed(ae.getMessage());
		}
		org.springframework.core.io.Resource res = new ClassPathResource(SecurityConstants.RSA_PUBLIC_KEY);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
			return ResponseEntity.succeed(br.lines().collect(Collectors.joining("\n")));
		} catch (IOException ioe) {
			log.error("key error", ioe);
			return ResponseEntity.failed(ioe.getMessage());
		}
	}


}
