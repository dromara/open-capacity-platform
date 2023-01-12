package com.open.capacity.gateway.filter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.gateway.utils.ReactiveAddrUtil;
import com.open.capacity.log.enums.LogEnums;
import com.open.capacity.log.model.Log;
import com.open.capacity.log.trace.MDCTraceUtils;
import com.open.capacity.log.util.BizLogUtil;

import cn.hutool.http.HttpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import io.vavr.control.Try;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * requestTrace 过滤器
 * 执行顺序 RequestStatsFilter -> ResponseStatsFilter -> RequestTraceGatewayFilterFactory
 */
@Slf4j
@Component
public class RequestTraceGatewayFilterFactory
		extends AbstractGatewayFilterFactory<RequestTraceGatewayFilterFactory.Config> {

	private static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

	public RequestTraceGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Lists.newArrayList("enabled");
	}

	@Override
	public GatewayFilter apply(Config config) {

		return (exchange, chain) -> {
			Boolean enabled = config.getEnabled();
			if (enabled) {
				ServerHttpRequest request = exchange.getRequest();
				// 记录请求日志
				Map requestTrace = Try.of(() -> getRequestTrace(exchange, request))
						.onFailure(ex -> log.error("requesttrace error", ex)).getOrElse(Maps.newHashMap());
				Long startTime = System.currentTimeMillis();
				return chain.filter(exchange.mutate().request(request.mutate().build()).build())
						.then(Mono.fromRunnable(() -> {
							Long endTime = System.currentTimeMillis();
							boolean statusFlag = exchange.getResponse().getStatusCode().equals(HttpStatus.OK);
							requestTrace.put("TimeElapsed", endTime - startTime);
							requestTrace.put(CommonConstant.STATUS,
									statusFlag ? CommonConstant.SUCCESS : CommonConstant.FAIL);
							BizLogUtil.info(LogEnums.REQUEST_LOG.getTag(), LogEnums.REQUEST_LOG.getName(),
									Log.builder().clientIp(ReactiveAddrUtil.getRemoteAddr(request))
											.params(getParams(request.getQueryParams().toSingleValueMap()))
											.body(exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY))
											.objectId(LogEnums.REQUEST_LOG.getId())
											.traceId(request.getHeaders().getFirst(MDCTraceUtils.TRACE_ID_HEADER))
											.spanId(request.getHeaders().getFirst(MDCTraceUtils.SPAN_ID_HEADER))
											.objectParam(requestTrace).build());
						}));

			}

			return chain.filter(exchange);
		};
	}

	@Data
	public static class Config {
		private Boolean enabled;
	}

	/**
	 * 获取请求日志
	 *
	 * @param request 请求
	 * @return requestLog
	 */
	private Map getRequestTrace(ServerWebExchange exchange, ServerHttpRequest request) {
		// 埋点
		Map requestTrace = Maps.newHashMap();
		requestTrace.put("RemoteAddr", ReactiveAddrUtil.getRemoteAddr(request));
		requestTrace.put("Uri", request.getURI().getRawPath());
		requestTrace.put("Method", request.getMethodValue());
		requestTrace.put("User-Agent", request.getHeaders().getFirst(HttpHeaders.USER_AGENT));
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeaders().getFirst(HttpHeaders.USER_AGENT));
		requestTrace.put("Browser", ReactiveAddrUtil.getBrowser(userAgent.getBrowser().name()));
		requestTrace.put("OperatingSystem", ReactiveAddrUtil.getOperatingSystem(userAgent.getOperatingSystem().name()));
		requestTrace.put("CreatTime", LocalDateTime.now());
		return requestTrace;
	}

	/**
	 * 获取请求参数
	 *
	 * @param parameterMap 请求参数
	 * @return params
	 */
	private String getParams(Map<String, String> parameterMap) {
		return HttpUtil.toParams(parameterMap);
	}

}
