package com.open.capacity.common.filter.user;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.filter.OncePerRequestFilter;

import com.open.capacity.common.context.SysUserContextHolder;
import com.open.capacity.common.filter.FilterCondition;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.UserUtils;

/**
 * 获取当前登录人过滤器
 *
 * @author someday
 * @date 2022/6/26
 */
@ConditionalOnClass(Filter.class)
@Conditional(FilterCondition.class)
public class LoginUserFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
        	//
            SysUser user = UserUtils.getCurrentUser(request, false);
            
            SysUserContextHolder.setUser(user);
            filterChain.doFilter(request, response);
        } finally {
        	SysUserContextHolder.clear();
        }
    }
}
