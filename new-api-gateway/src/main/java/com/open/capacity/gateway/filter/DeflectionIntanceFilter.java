package com.open.capacity.gateway.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

import java.net.URI;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.gateway.loadbalancer.DeflectionInstanceBalancer;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 偏向性路由过滤
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class DeflectionIntanceFilter implements GlobalFilter, Ordered {

	private static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10150;

	private final LoadBalancerClientFactory clientFactory;

	private GatewayLoadBalancerProperties properties;

	public DeflectionIntanceFilter(LoadBalancerClientFactory clientFactory,
			GatewayLoadBalancerProperties properties) {
		this.clientFactory = clientFactory;
		this.properties = properties;
	}

	@Override
	public int getOrder() {
		return LOAD_BALANCER_CLIENT_FILTER_ORDER;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		URI url = (URI) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
		String schemePrefix = (String) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);
		if (exchange.getRequest().getHeaders().containsKey(CommonConstant.INSTANCE_INFO)) {

			addOriginalRequestUrl(exchange, url);

			if (log.isTraceEnabled()) {
				log.trace(IpHashLoadBalancerClientFilter.class.getSimpleName() + " url before: " + url);
			}

			return choose(exchange).doOnNext(response -> {

				if (!response.hasServer()) {
					throw NotFoundException.create(properties.isUse404(),
							"Unable to find instance for " + url.getHost());
				}

				URI uri = exchange.getRequest().getURI();
				String overrideScheme = null;
				if (schemePrefix != null) {
					overrideScheme = url.getScheme();
				}
				DelegatingServiceInstance serviceInstance =    new DelegatingServiceInstance(response.getServer(),
						overrideScheme);
				URI requestUrl = reconstructURI(serviceInstance, uri);
				if (log.isTraceEnabled()) {
					log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
				}
				exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
			}).then(chain.filter(exchange));
		}
		return chain.filter(exchange);

	}

	protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
		return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
	}

	private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
		
		
		URI uri = (URI) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
		DeflectionInstanceBalancer loadBalancer = new DeflectionInstanceBalancer(
				clientFactory.getLazyProvider(uri.getHost(), ServiceInstanceListSupplier.class), uri.getHost());
		if (loadBalancer == null) {
			throw new NotFoundException("No loadbalancer available for " + uri.getHost());
		} else {
			return loadBalancer.choose(this.createRequest(exchange));
		}
		
	}

	private Request createRequest(ServerWebExchange exchange) {
		String instance = exchange.getRequest().getHeaders().get(CommonConstant.INSTANCE_INFO).get(0)  ;
		Request<String> request = new DefaultRequest(instance);
		return request;
		
	}

}
