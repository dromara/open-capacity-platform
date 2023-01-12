package com.open.capacity.gateway.error;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.google.common.collect.Maps;
import com.open.capacity.common.constant.CommonConstant;

import reactor.core.publisher.Mono;

/**
 * 
 * 限流异常处理
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class CustomerSentinelExceptionHandler implements BlockRequestHandler {

	@Override
	public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable t) {
		Map<String, Object> map = Maps.newHashMap();
		map.put(CommonConstant.STATUS, 429);
		map.put("msg", "接口限流了");
		return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(map));
	}

}
