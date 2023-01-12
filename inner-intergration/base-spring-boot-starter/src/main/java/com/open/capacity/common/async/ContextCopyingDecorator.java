package com.open.capacity.common.async;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.open.capacity.common.context.LbIsolationContextHolder;
import com.open.capacity.common.context.SysUserContextHolder;
import com.open.capacity.common.context.TenantContextHolder;
import com.open.capacity.common.model.SysUser;

/**
 * @author owen 
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 *
 */
// https://stackoverflow.com/questions/23732089/how-to-enable-request-scope-in-async-task-executor
// 传递RequestAttributes and MDC and SecurityContext
public class ContextCopyingDecorator implements TaskDecorator {
	@Override
	public Runnable decorate(Runnable runnable) {
		// 主线程
		RequestAttributes context = RequestContextHolder.currentRequestAttributes(); // 1
		Map<String, String> previous = MDC.getCopyOfContextMap(); // 2
		SecurityContext securityContext = SecurityContextHolder.getContext(); // 3
		String tenatId =  TenantContextHolder.getTenant() ;  //4
		SysUser sysUser = SysUserContextHolder.getUser() ; //5
		String  version = LbIsolationContextHolder.getVersion() ; //6;
		// 子线程
		return () -> {
			try {
				// 将变量重新放入到run线程中
				RequestContextHolder.setRequestAttributes(context); // 1
				MDC.setContextMap(previous); // 2
				SecurityContextHolder.setContext(securityContext); // 3
				TenantContextHolder.setTenant(tenatId);          //4
				SysUserContextHolder.setUser(sysUser); //5
				LbIsolationContextHolder.setVersion(version);
				runnable.run();
			} finally {
				RequestContextHolder.resetRequestAttributes(); // 1
				MDC.clear(); // 2
				SecurityContextHolder.clearContext(); // 3
				TenantContextHolder.clear();  //4
				SysUserContextHolder.clear(); //5
				LbIsolationContextHolder.clear(); //6
			}
		};
	}
}
