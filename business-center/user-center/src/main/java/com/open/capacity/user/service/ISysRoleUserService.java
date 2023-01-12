package com.open.capacity.user.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.user.model.SysRoleUser;

/**
 * @author someday
 */
public interface ISysRoleUserService extends IService<SysRoleUser> {
	int deleteUserRole(Long userId, Long roleId);

	/**
	 * 根据用户id获取角色
	 *
	 * @param userId
	 * @return
	 */
	List<SysRole> findRolesByUserId(Long userId);

	/**
	 * 根据用户ids 获取
	 *
	 * @param userIds
	 * @return
	 */
	List<SysRole> findRolesByUserIds(List<Long> userIds);
}
