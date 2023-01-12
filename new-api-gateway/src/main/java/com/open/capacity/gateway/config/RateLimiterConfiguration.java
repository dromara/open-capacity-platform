package com.open.capacity.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * @author owen
 * 定义spring cloud gateway中的 key-resolver: "#{@ipAddressKeyResolver}"
 * #SPEL表达式去的对应的bean ipAddressKeyResolver 要取bean的名字
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 *
 */
@Configuration
public class RateLimiterConfiguration {

	/**
	 * 根据 HostAddress 进行限流
	 * @return
	 */
	@Bean(value = "ipAddressKeyResolver")
	public KeyResolver remoteAddrKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
	}

}
