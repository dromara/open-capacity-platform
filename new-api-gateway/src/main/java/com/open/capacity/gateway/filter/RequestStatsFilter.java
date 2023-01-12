package com.open.capacity.gateway.filter;

import com.open.capacity.log.properties.TraceProperties;
import com.open.capacity.log.trace.MDCTraceUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 程序名 : RequestStatsFilter 建立日期: 2018-09-09 作者 : someday 模块 : 网关 描述 :
 * 请求传递traceId校验 备注 : version20180909001
 * <p>
 * 修改历史 序号 日期 修改人 修改原因
 */
@Slf4j
@Component
public class RequestStatsFilter implements GlobalFilter, Ordered {
	@Autowired
	private TraceProperties traceProperties;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		if (traceProperties.getEnable()) {
			// 链路追踪id
			MDCTraceUtils.addTrace();
			if (log.isDebugEnabled()) {
				log.info("traceId = {}, request url = {}", MDCTraceUtils.getTraceId(), exchange.getRequest().getURI());
			}
			ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(h -> {
				h.add(MDCTraceUtils.TRACE_ID_HEADER, MDCTraceUtils.getTraceId());
				h.add(MDCTraceUtils.SPAN_ID_HEADER, MDCTraceUtils.getNextSpanId());
			}).build();
			ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
			return chain.filter(build);
		}
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}
}
