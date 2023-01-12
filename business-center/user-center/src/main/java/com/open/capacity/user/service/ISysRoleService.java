package com.open.capacity.user.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.model.SysRole;

/**
 * @author someday
 *  code: https://gitee.com/owenwangwen/open-capacity-platform

 
 */
public interface ISysRoleService extends IService<SysRole> {
	boolean saveRole(SysRole sysRole) ;

	boolean deleteRole(Long id);

	/**
	 * 角色列表
	 * @param params
	 * @return
	 */
	PageResult<SysRole> findRoles(Map<String, Object> params);

	/**
	 * 新增或更新角色
	 * @param sysRole
	 * @return ResponseEntity
	 */
	boolean saveOrUpdateRole(SysRole sysRole)  ;

}
