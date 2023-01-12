package com.open.capacity.uaa.common.handler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.open.capacity.common.model.SysUser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* 认证失败 handler
*
* @author owen
* @date 2018/8/5
* blog: https://blog.51cto.com/13005375 
* code: https://gitee.com/owenwangwen/open-capacity-platform
*/
@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationFailureEvenHandler extends AbstractAuthenticationFailureEvenHandler {

	private final ApplicationEventPublisher publisher;

	/**
	 * 处理登录失败方法
	 * <p>
	 *
	 * @param authenticationException 登录的authentication 对象
	 * @param authentication          登录的authenticationException 对象
	 */
	@Override
	public void handle(AuthenticationException authenticationException, Authentication authentication) {
		Object principal = authentication.getPrincipal();
		String username = null;
		if(log.isInfoEnabled()) {
			log.info("用户：{} 登录失败，异常：{}", principal, authenticationException.getLocalizedMessage());
		}

		// 表单登陆时失败，principal 是返回String
		if (principal instanceof String) {
			username = (String) principal;
		} else if (principal instanceof SysUser) {
			username = (((SysUser) principal)).getUsername();
		}

		 
  
	}

}
