package com.open.capacity.user.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.model.SysMenu;
import com.open.capacity.user.mapper.SysMenuMapper;
import com.open.capacity.user.model.SysRoleMenu;
import com.open.capacity.user.service.ISysMenuService;
import com.open.capacity.user.service.ISysRoleMenuService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

	@Resource
	private ISysRoleMenuService roleMenuService;

	/**
	 * 角色权限保存
	 * 
	 * @param roleId
	 * @param menuIds
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean setMenuToRole(Long roleId, Set<Long> menuIds) {
		boolean flag = false;
		if (!CollectionUtils.isEmpty(menuIds)) {
			roleMenuService.delete(roleId, null) ;
			List<SysRoleMenu> roleMenus = new ArrayList<>(menuIds.size());
			menuIds.forEach(menuId -> roleMenus.add(SysRoleMenu.builder().roleId(roleId).menuId(menuId).build()));
			flag = roleMenuService.saveBatch(roleMenus);
		}

		return flag;
	}

	/**
	 * 角色菜单列表
	 * 
	 * @param roleIds
	 * @return
	 */
	@Override
	public List<SysMenu> findByRoles(Set<Long> roleIds) {
		return roleMenuService.findMenusByRoleIds(roleIds, null);
	}

	/**
	 * 角色菜单列表
	 * 
	 * @param roleIds 角色ids
	 * @param roleIds 是否菜单
	 * @return
	 */
	@Override
	public List<SysMenu> findByRoles(Set<Long> roleIds, Integer type) {
		return roleMenuService.findMenusByRoleIds(roleIds, type);
	}

	@Override
	public List<SysMenu> findByRoleCodes(Set<String> roleCodes, Integer type) {
		return roleMenuService.findMenusByRoleCodes(roleCodes, type);
	}

	 

	/**
	 * 查询所有一级菜单
	 */
	@Override
	public List<SysMenu> findOnes() {

		return baseMapper.selectList(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getType, CommonConstant.MENU).orderByAsc(SysMenu::getSort));

	}

}
