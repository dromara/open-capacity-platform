package com.open.capacity.common.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.open.capacity.common.constant.ServiceNameConstants;
import com.open.capacity.common.feign.fallback.MenuFeignClientFallbackFactory;
import com.open.capacity.common.model.SysMenu;

/**
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */

@FeignClient(name = ServiceNameConstants.USER_SERVICE,configuration = FeignExceptionConfig.class , fallbackFactory = MenuFeignClientFallbackFactory.class, decode404 = true)
public interface MenuFeignClient {
	/**
	 * 角色菜单列表
	 * @param roleCodes
	 */
	@GetMapping(value = "/menus/{roleCodes}")
	List<SysMenu> findByRoleCodes(@PathVariable("roleCodes") String roleCodes);
}
