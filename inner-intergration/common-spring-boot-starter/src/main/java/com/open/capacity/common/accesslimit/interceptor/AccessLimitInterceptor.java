package com.open.capacity.common.accesslimit.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.open.capacity.common.accesslimit.annotation.AccessLimit;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.utils.ResponseUtil;
import com.open.capacity.redis.repository.RedisRepository;

import lombok.AllArgsConstructor;

/**
 * 非网关部分应用次数限制 
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@AllArgsConstructor
@SuppressWarnings("all")
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {

	@Resource
	private RedisRepository redisRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler instanceof HandlerMethod) {

			HandlerMethod hm = (HandlerMethod) handler;
			AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
			if (accessLimit == null) {
				return true;
			}
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			String key = request.getRequestURI();

			if (!redisRepository.hasKey(key) || redisRepository.getExpire(key) <= 0) {
				redisRepository.setExpire(key, 0, seconds);
			}
			if (redisRepository.incr(key, 1) > maxCount) {
				ResponseUtil.renderJson(response, ResponseEntity.failed("访问太频繁！"));
				return false;
			}

		}
		return true;
	}

}
