package com.open.capacity.common.feign.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;

import com.google.common.collect.ImmutableList;
import com.open.capacity.common.feign.MenuFeignClient;

import lombok.extern.slf4j.Slf4j;

/**
 * menuService降级工场
 * 
 * @author someday
 * @date 2018/1/18
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class MenuFeignClientFallbackFactory implements FallbackFactory<MenuFeignClient> {
	@Override
	public MenuFeignClient create(Throwable throwable) {
		return roleIds -> {
			log.error("调用findByRoleCodes异常：{}", roleIds, throwable);
			return  ImmutableList.of();
		};
	}
}
