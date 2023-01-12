package com.open.capacity.user.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.IService;
import com.open.capacity.common.model.SysMenu;
import com.open.capacity.user.model.SysRoleMenu;

/**
 * @author someday
 */
public interface ISysRoleMenuService extends IService<SysRoleMenu> {

	int delete(Long roleId, Long menuId);

	List<SysMenu> findMenusByRoleIds(Set<Long> roleIds, Integer type);

	List<SysMenu> findMenusByRoleCodes(Set<String> roleCodes, Integer type);
}
