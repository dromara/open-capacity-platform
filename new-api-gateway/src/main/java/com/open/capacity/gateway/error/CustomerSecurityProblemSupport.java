package com.open.capacity.gateway.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.utils.WebfluxResponseUtil;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait;

import reactor.core.publisher.Mono;
/**
 * 
 * 异常处理
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 *
 */
public class CustomerSecurityProblemSupport implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {

	private SecurityAdviceTrait advice;
	private ObjectMapper mapper;

	public CustomerSecurityProblemSupport(final SecurityAdviceTrait advice, final ObjectMapper mapper) {
		this.advice = advice;
		this.mapper = mapper;
	}

	public Mono<Void> commence(final ServerWebExchange exchange, final AuthenticationException e) {
		return advice.handleAuthentication(e, exchange)
				.flatMap(entity -> WebfluxResponseUtil.responseProblem(exchange,  (org.springframework.http.ResponseEntity)entity) );
	}

	public Mono<Void> handle(final ServerWebExchange exchange, final AccessDeniedException e) {
		return advice.handleAccessDenied(e, exchange)
				.flatMap(entity -> WebfluxResponseUtil.responseProblem(exchange,  (org.springframework.http.ResponseEntity)entity) );
	}

}
