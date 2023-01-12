package com.open.capacity.gateway.filter;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.properties.SecurityProperties;

import reactor.core.publisher.Mono;

/**
 * 鉴权信息是否需要传递到下游服务，传递则下游需要鉴权，不传递，下游不需要鉴权
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Component
public class AuthWebPassFilter implements GlobalFilter, Ordered {
	@Autowired
	private SecurityProperties securityProperties;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		if(!securityProperties.getAuth().getIsPassAuth()) {
			ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(h -> {
				
				if(h.get(CommonConstant.TOKEN_HEADER)!=null) {
					if(h.get(CommonConstant.TOKEN_HEADER).stream().anyMatch( item-> item.contains(CommonConstant.BEARER_TYPE) ) ) {
						h.remove(CommonConstant.TOKEN_HEADER);
					}
				}
				
			
			}).build();
			ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
			return chain.filter(build);
		}
		
		return chain.filter(exchange);

	}
	
	private ServerWebExchange modificationRequestParam(ServerWebExchange exchange) {
        //获取uri对象
        URI uri = exchange.getRequest().getURI();
        StringBuilder query = new StringBuilder();
        //获取 url上的参数
        String originalQuery = uri.getRawQuery();
        //如果有参数才进行修改
        if (StringUtils.isNotBlank(originalQuery)) {
            //修改 url上的参数。这里可以自定义修改
            originalQuery = originalQuery.replace(CommonConstant.TOKEN_HEADER,CommonConstant.TOKEN );
            //todo 拿到新构建的参数创建一个新的URI对象
            URI newUri = UriComponentsBuilder.fromUri(uri).replaceQuery(query.toString()).build(true).toUri();
            //用新的URI 创建新的 ServerHttpRequest
            ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();
            //返回新构建的 exchange
            return exchange.mutate().request(request).build();
            
        }
        return exchange;
    }
 

	@Override
	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}

}
