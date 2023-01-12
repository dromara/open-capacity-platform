package com.open.capacity.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.OrikaUtils;

/**
 * 登录用户holder
 * 
 * @author someday
 * @date 2018/6/26 
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class SysUserContextHolder {
	private static final ThreadLocal<SysUser> CONTEXT = new TransmittableThreadLocal<SysUser>() {
		@Override
		public SysUser childValue(SysUser parentValue) {
			return OrikaUtils.convert(parentValue, SysUser.class);
		}

		@Override
		public SysUser copy(SysUser parentValue) {
			return OrikaUtils.convert(parentValue, SysUser.class);
		}
	};

	public static void setUser(SysUser user) {
		CONTEXT.set(user);
	}

	public static SysUser getUser() {
		return CONTEXT.get();
	}

	public static void clear() {
		CONTEXT.remove();
	}
}