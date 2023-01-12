package com.open.capacity.common.accesslimit;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.open.capacity.common.accesslimit.interceptor.AccessLimitInterceptor;
import com.open.capacity.redis.repository.RedisRepository;

/**
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
public class AccessLimitConfig implements WebMvcConfigurer {

	@Resource
	private RedisRepository redisRepository;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AccessLimitInterceptor(redisRepository));

	}
}
