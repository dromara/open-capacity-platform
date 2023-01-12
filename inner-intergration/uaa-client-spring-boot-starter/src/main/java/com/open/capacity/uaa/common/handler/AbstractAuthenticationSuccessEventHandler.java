
package com.open.capacity.uaa.common.handler;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import com.open.capacity.common.model.SysUser;

/**
* 认证成功事件处理器
*
* @author owen
* @date 2018/8/5
* blog: https://blog.51cto.com/13005375 
* code: https://gitee.com/owenwangwen/open-capacity-platform
*/
public abstract class AbstractAuthenticationSuccessEventHandler
		implements ApplicationListener<AuthenticationSuccessEvent> {
	/**
	 *
	 * @param event the event to respond to
	 */
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		Authentication authentication = (Authentication) event.getSource();
		Object obj = authentication.getPrincipal();
		if (obj instanceof SysUser) {
			handle(authentication);
		}
	}

	/**
	 * 处理登录成功方法
	 * 获取到登录的authentication 对象
	 * @param authentication 登录对象
	 */
	public abstract void handle(Authentication authentication);
}
