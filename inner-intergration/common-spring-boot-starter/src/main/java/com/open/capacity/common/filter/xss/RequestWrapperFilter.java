package com.open.capacity.common.filter.xss;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.open.capacity.common.filter.FilterCondition;
import com.open.capacity.common.properties.SecurityProperties;

/**
 * @author someday
 * @date 2019-08-13
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 * request 处理过滤器
 */
@ConditionalOnClass(Filter.class)
@Conditional(FilterCondition.class)
public class RequestWrapperFilter extends OncePerRequestFilter {

	@Resource
	private SecurityProperties securityProperties ;
	
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		//非网关服务xss拦截
		if (securityProperties.getXss().getEnable()) {
			filterChain.doFilter(new RequestWrapper(request), response);
		} else {
			filterChain.doFilter(request, response);
		}

	}
	static class FilterCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !context.getEnvironment().containsProperty("spring.cloud.gateway") ;
        }
    }
}