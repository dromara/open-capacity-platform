package com.open.capacity.uaa.common.handler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.open.capacity.common.model.SysUser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* 认证成功 handler
*
* @author owen
* @date 2018/8/5
* blog: https://blog.51cto.com/13005375 
* code: https://gitee.com/owenwangwen/open-capacity-platform
*/
@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationSuccessEventHandler extends AbstractAuthenticationSuccessEventHandler {

	private final ApplicationEventPublisher publisher;


	/**
	 * 处理登录成功方法
	 * 获取到登录的authentication 对象
	 * @param authentication 登录对象
	 */
	@Override
	public void handle(Authentication authentication) {
		if (log.isDebugEnabled()) {
			log.debug("用户：{} 登录成功", authentication.getPrincipal());
		}
 
		SysUser thUser = (SysUser) authentication.getPrincipal();
	  
	}
}
