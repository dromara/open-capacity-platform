package com.open.capacity.sentinel;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.google.common.collect.Maps;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.dto.ResponseEntity;

import cn.hutool.json.JSONUtil;

/**
 * Sentinel配置类
 */
public class SentinelAutoConfig {

	/**
	 * 限流、熔断统一处理类
	 */
	@Configuration
	@ConditionalOnClass(HttpServletRequest.class)
	public static class WebmvcHandler {
		@Bean
		public BlockExceptionHandler webmvcBlockExceptionHandler() {
			return (request, response, e) -> {
				response.setStatus(429);
				ResponseEntity result = ResponseEntity.failed(e.getMessage());
				response.getWriter().print(JSONUtil.toJsonStr(result));
			};
		}
	}

	/**
	 * 限流、熔断统一处理类
	 */
	@Configuration
	@ConditionalOnClass(ServerResponse.class)
	public static class WebfluxHandler {
		@Bean
		public BlockRequestHandler webfluxBlockExceptionHandler() {
			return (exchange, t) -> {
				Map<String, Object> map = Maps.newHashMap();
				map.put(CommonConstant.STATUS, 429);
				map.put("msg", "接口限流了");
				return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(map));
			};
		}

	}

}
