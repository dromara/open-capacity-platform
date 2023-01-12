
package com.open.capacity.uaa.utils;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.open.capacity.common.utils.MessageSourceUtil;

import lombok.extern.slf4j.Slf4j;


/**
 * 自定义账号状态检查
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class PreAuthenticationChecks implements UserDetailsChecker {
	private MessageSourceAccessor messages = MessageSourceUtil.getAccessor();

	@Override
	public void check(UserDetails user) {

		if (user == null) {
			if (log.isDebugEnabled()) {
				log.debug("User not found");
			}
			throw new UsernameNotFoundException(
					messages.getMessage("DigestAuthenticationFilter.usernameNotFound", "User not found"));
		}

		if (!user.isAccountNonLocked()) {
			if (log.isDebugEnabled()) {
				log.debug("User account is locked");
			}
			throw new LockedException(
					messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
		}

		if (!user.isEnabled()) {
			if (log.isDebugEnabled()) {
				log.debug("User account is disabled");
			}
			throw new DisabledException(
					messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
		}

		if (!user.isAccountNonExpired()) {
			if (log.isDebugEnabled()) {
				log.debug("User account is expired");
			}
			throw new AccountExpiredException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired",
					"User account has expired"));
		}
	}
}
