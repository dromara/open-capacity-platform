package com.open.capacity.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.open.capacity.log.trace.MDCTraceUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 程序名 : ResponseStatsFilter 建立日期: 2018-09-09 作者 : someday 模块 : 网关 描述 :
 * 应答traceId: version20180909001
 * <p>
 * 修改历史 序号 日期 修改人 修改原因
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class ResponseStatsFilter implements GlobalFilter, Ordered {

	@Override
	public int getOrder() {
		return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String traceId = MDCTraceUtils.getTraceId();
		ServerHttpRequest request = exchange.getRequest();
		// 这里可以修改ServerHttpRequest实例
		ServerHttpResponse response = exchange.getResponse();
		// 这里可以修改ServerHttpResponse实例
		response.getHeaders().add(MDCTraceUtils.TRACE_ID_HEADER, traceId);
		// 构建新的ServerWebExchange实例
		if (log.isDebugEnabled()) {
			log.info("traceId = {} ,response url = {}", MDCTraceUtils.getTraceId(), request.getURI().getPath());
		}
		ServerWebExchange newExchange = exchange.mutate().request(exchange.getRequest()).response(response).build();
		// 清理mdc的值
		MDCTraceUtils.removeTrace();
		return chain.filter(newExchange);

	}

}
