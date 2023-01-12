package com.open.capacity.common.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.common.resolver.ClientArgumentResolver;
import com.open.capacity.common.resolver.TokenArgumentResolver;

/**
 * 默认SpringMVC拦截器，用于非网关服务
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class DefaultSpringWebMvcConfig implements WebMvcConfigurer {

	@Lazy
	@Autowired
	private UserFeignClient userFeignClient;

	/**
	 * 安全防火墙
	 * 
	 * @return
	 */
	@Bean
	public HttpFirewall httpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		Collection<String> allowedHttpMethods = new ArrayList<>();
		allowedHttpMethods.add(HttpMethod.GET.name());
		allowedHttpMethods.add(HttpMethod.OPTIONS.name());
		allowedHttpMethods.add(HttpMethod.POST.name());
		allowedHttpMethods.add(HttpMethod.PUT.name());
		allowedHttpMethods.add(HttpMethod.HEAD.name());
		allowedHttpMethods.add(HttpMethod.PATCH.name());
		allowedHttpMethods.add(HttpMethod.DELETE.name());
		firewall.setAllowedHttpMethods(allowedHttpMethods);
		return firewall;
	}

	/**
	 * Token参数解析
	 * zlt
	 * @param argumentResolvers 解析类
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		// 注入用户信息
		argumentResolvers.add(new TokenArgumentResolver(userFeignClient));
		// 注入应用信息
		argumentResolvers.add(new ClientArgumentResolver());
	}

}
