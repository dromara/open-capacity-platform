package com.open.capacity.gateway.filter;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.gateway.loadbalancer.GrayLoadBalancer;

import reactor.core.publisher.Mono;

/**
 * 示例
 * 自定义权重灰度 + 前端版本控制灰度
 * @author jarvis create by 2018/5/8
 */
public class GrayVersionIsolationFilter implements GlobalFilter, Ordered {

	private static final Log log = LogFactory.getLog(GrayVersionIsolationFilter.class);
	private static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10150;
	private final LoadBalancerClientFactory clientFactory;
	private final GatewayLoadBalancerProperties properties;

	public GrayVersionIsolationFilter(LoadBalancerClientFactory clientFactory, GatewayLoadBalancerProperties properties) {
		this.clientFactory = clientFactory;
		this.properties = properties;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		URI url = (URI) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
		String schemePrefix = (String) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);
		//前端版本控制
		if (exchange.getRequest().getHeaders().containsKey(CommonConstant.O_C_P_VERSION)) {
			String version = exchange.getRequest().getHeaders().get(CommonConstant.O_C_P_VERSION).get(0);
			ServerHttpRequest rebuildRequest = exchange.getRequest().mutate().headers(header -> {
				header.add(CommonConstant.O_C_P_VERSION, version);
			}).build();
			ServerWebExchange rebuildServerWebExchange = exchange.mutate().request(rebuildRequest).build();
			return chain.filter(rebuildServerWebExchange);
		} else if (url != null && (CommonConstant.GRAY_LB.equals(url.getScheme()) || CommonConstant.GRAY_LB.equals(schemePrefix))) {
			//后端配置灰度
			ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);
			if (log.isTraceEnabled()) {
				log.trace(ReactiveLoadBalancerClientFilter.class.getSimpleName() + " url before: " + url);
			}
			return this.choose(exchange).doOnNext((response) -> {
				if (!response.hasServer()) {
					throw NotFoundException.create(this.properties.isUse404(),
							"Unable to find instance for " + url.getHost());
				} else {
					URI uri = exchange.getRequest().getURI();
					String overrideScheme = null;
					if (schemePrefix != null) {
						overrideScheme = url.getScheme();
					}
					DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(
							(ServiceInstance) response.getServer(), overrideScheme);
					URI requestUrl = this.reconstructURI(serviceInstance, uri);
					if (log.isTraceEnabled()) {
						log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
					}
					exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
				}
			}).then(chain.filter(exchange));
		} else {
			return chain.filter(exchange);
		}
	}

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

	protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
		return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
	}

	private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
		URI uri = (URI) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
		GrayLoadBalancer loadBalancer = new GrayLoadBalancer(clientFactory.getLazyProvider(uri.getHost(), ServiceInstanceListSupplier.class), uri.getHost());
		if (loadBalancer == null) {
			throw new NotFoundException("No loadbalancer available for " + uri.getHost());
		} else {
			return loadBalancer.choose(this.createRequest(exchange));
		}
	}

	private Request createRequest(ServerWebExchange exchange) {
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Request<HttpHeaders> request = new DefaultRequest<>(headers);
		return request;
	}
}
