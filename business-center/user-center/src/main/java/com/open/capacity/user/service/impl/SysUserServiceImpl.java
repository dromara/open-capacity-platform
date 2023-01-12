package com.open.capacity.user.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.constant.UserType;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.model.BaseEntity;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.model.SysMenu;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.EntityUtils;
import com.open.capacity.user.mapper.SysRoleMenuMapper;
import com.open.capacity.user.mapper.SysUserMapper;
import com.open.capacity.user.model.SysRoleUser;
import com.open.capacity.user.model.SysUserExcel;
import com.open.capacity.user.service.ISysRoleUserService;
import com.open.capacity.user.service.ISysUserService;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Resource
	private ISysRoleUserService roleUserService;

	@Resource
	private SysRoleMenuMapper roleMenuMapper;

	@Override
	public LoginAppUser findByUsername(String username) {
		SysUser sysUser = this.selectByUsername(username);
		return getLoginAppUser(sysUser);
	}

	@Override
	public LoginAppUser findByOpenId(String username) {
		SysUser sysUser = this.selectByOpenId(username);
		return getLoginAppUser(sysUser);
	}

	@Override
	public LoginAppUser findByMobile(String username) {
		SysUser sysUser = this.selectByMobile(username);
		return getLoginAppUser(sysUser);
	}

	/**
	 * 根据用户名查询用户
	 * 
	 * @param username
	 * @return
	 */
	@Override
	public SysUser selectByUsername(String username) {
		List<SysUser> users = baseMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
		return getUser(users);
	}

	/**
	 * 根据手机号查询用户
	 * 
	 * @param mobile
	 * @return
	 */
	@Override
	public SysUser selectByMobile(String mobile) {
		List<SysUser> users = baseMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getMobile, mobile));
		return getUser(users);
	}

	/**
	 * 根据openId查询用户
	 * 
	 * @param openId
	 * @return
	 */
	@Override
	public SysUser selectByOpenId(String openId) {
		List<SysUser> users = baseMapper.selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getOpenId, openId));
		return getUser(users);
	}

	/**
	 * 根据userId查询用户
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public LoginAppUser findByUserId(String userId) {

		SysUser sysUser = baseMapper.selectById(userId);
		return getLoginAppUser(sysUser);
	}

	private SysUser getUser(List<SysUser> users) {
		SysUser user = null;
		if (users != null && !users.isEmpty()) {
			user = users.get(0);
		}
		return user;
	}

	/**
	 * 给用户设置角色
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void setRoleToUser(Long id, Set<Long> roleIds) {
		SysUser sysUser = baseMapper.selectById(id);
		if (sysUser == null) {
			throw new IllegalArgumentException("用户不存在");
		}

		roleUserService.deleteUserRole(id, null);
		if (!CollectionUtils.isEmpty(roleIds)) {
			List<SysRoleUser> roleUsers = new ArrayList<>(roleIds.size());
			roleIds.forEach(roleId -> roleUsers.add(SysRoleUser.builder().userId(id).roleId(roleId).build()));
			roleUserService.saveBatch(roleUsers);
		}
	}

	@Transactional
	@Override
	public boolean updatePassword(Long id, String oldPassword, String newPassword) {

		boolean flag = false;
		SysUser sysUser = baseMapper.selectById(id);
		if (StrUtil.isNotBlank(oldPassword)) {
			if (!passwordEncoder.matches(oldPassword, sysUser.getPassword())) {
				throw new BusinessException("更新异常");
			}
		}
		if (StrUtil.isBlank(newPassword)) {
			newPassword = CommonConstant.DEF_USER_PASSWORD;
		}
		SysUser user = new SysUser();
		user.setId(id);
		user.setPassword(passwordEncoder.encode(newPassword));
		flag = baseMapper.updateById(user) > 0 ? true : false;
		return flag;
	}

	@Override
	public PageResult<SysUser> findUsers(Map<String, Object> params) {
		Page<SysUser> page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
		List<SysUser> list = baseMapper.findList(page, params);
		
		if (!CollectionUtils.isEmpty(list)) {
			List<Long> userIds = EntityUtils.toList(list, SysUser::getId) ;
			List<SysRole> sysRoles = roleUserService.findRolesByUserIds(userIds);
			list.forEach(u -> u.setRoles(sysRoles.stream().filter(r -> !ObjectUtils.notEqual(u.getId(), r.getUserId()))
					.collect(Collectors.toList())));
		}
		return PageResult.<SysUser>builder().data(list).statusCodeValue(0).count(page.getTotal()).build();
	}

	@Override
	public List<SysRole> findRolesByUserId(Long userId) {
		return roleUserService.findRolesByUserId(userId);
	}

	@Override
	public int updateEnabled(Map<String, Object> params) {
		int i = 0;
		Long id = MapUtils.getLong(params, "id");
		Boolean enabled = MapUtils.getBoolean(params, "enabled");
		SysUser appUser = baseMapper.selectById(id);
		appUser.setEnabled(enabled);
		appUser.setUpdateTime(new Date());
		i = baseMapper.updateById(appUser);
		log.info("修改用户：{}", appUser);
		return i;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateUser(SysUser sysUser) {

		boolean flag = false;

		if (sysUser.getId() == null) {
			
			long count = this.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, sysUser.getUsername()));
			if(count > 0) {
				throw new BusinessException(String.format("%s用户已存在",sysUser.getUsername()));
			}
			
			if (StringUtils.isBlank(sysUser.getType())) {
				sysUser.setType(UserType.BACKEND.name());
			}
			sysUser.setPassword(passwordEncoder.encode(CommonConstant.DEF_USER_PASSWORD));
			sysUser.setEnabled(Boolean.TRUE);
		} 
		this.saveOrUpdate(sysUser);
		if (StrUtil.isNotEmpty(sysUser.getRoleId())) {
			// 更新角色
			roleUserService.deleteUserRole(sysUser.getId(), null);
			List roleIds = Convert.toList(sysUser.getRoleId());
			if (!CollectionUtils.isEmpty(roleIds)) {
				List<SysRoleUser> roleUsers = new ArrayList<>(roleIds.size());
				roleIds.forEach(roleId -> roleUsers.add(SysRoleUser.builder().userId(sysUser.getId())
						.roleId(Long.parseLong(roleId.toString())).build()));
				flag = roleUserService.saveBatch(roleUsers);
			}
		}

		return flag;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean delUser(Long id) {
		roleUserService.deleteUserRole(id, null);
		return baseMapper.deleteById(id) > 0;
	}

	@Override
	public List<SysUserExcel> findAllUsers(Map<String, Object> params) {
		List<SysUserExcel> sysUserExcels = new ArrayList<>();
		List<SysUser> list = baseMapper.findList(new Page<>(1, -1), params);

		for (SysUser sysUser : list) {
			SysUserExcel sysUserExcel = new SysUserExcel();
			BeanUtils.copyProperties(sysUser, sysUserExcel);
			sysUserExcels.add(sysUserExcel);
		}
		return sysUserExcels;
	}

	@Override
	public LoginAppUser getLoginAppUser(SysUser sysUser) {
		if (sysUser != null) {
			LoginAppUser loginAppUser = new LoginAppUser();
			BeanUtils.copyProperties(sysUser, loginAppUser);

			List<SysRole> sysRoles = roleUserService.findRolesByUserId(sysUser.getId());
			// 设置角色
			loginAppUser.setRoles(sysRoles);

			if (!CollectionUtils.isEmpty(sysRoles)) {
				Set<Long> roleIds = sysRoles.stream().map(BaseEntity::getId).collect(Collectors.toSet());
				List<SysMenu> menus = roleMenuMapper.findMenusByRoleIds(roleIds, CommonConstant.PERMISSION);
				if (!CollectionUtils.isEmpty(menus)) {
					Set<String> permissions = menus.stream().map(p -> p.getPath()).collect(Collectors.toSet());
					// 设置权限集合
					loginAppUser.setPermissions(permissions);
				}
			}
			return loginAppUser;
		}
		return null;
	}

}