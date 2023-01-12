package com.open.capacity.user.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.lock.DistributedLock;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.user.mapper.SysRoleMapper;
import com.open.capacity.user.mapper.SysRoleMenuMapper;
import com.open.capacity.user.mapper.SysUserRoleMapper;
import com.open.capacity.user.service.ISysRoleService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

	@Resource
	private SysUserRoleMapper userRoleMapper;

	@Resource
	private SysRoleMenuMapper roleMenuMapper;

	@Autowired
	private DistributedLock lock;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveRole(SysRole sysRole)   {
		boolean flag = false ;
		SysRole role = this.getOne(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getCode, sysRole.getCode()));
		Assert.isTrue(role == null, "角色code已存在");
		flag = this.save(sysRole);
		log.info("保存角色：{}", sysRole);
		return flag ;

	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean deleteRole(Long id) {
		boolean flag  = false ; 
		roleMenuMapper.delete(id, null)  ;
		userRoleMapper.deleteUserRole(null, id)   ;
		flag  = baseMapper.deleteById(id) > 0 ? true : false;
		return  flag;
	}

	@Override
	public PageResult<SysRole> findRoles(Map<String, Object> params) {
		Integer curPage = MapUtils.getInteger(params, "page");
		Integer limit = MapUtils.getInteger(params, "limit");
		Page<SysRole> page = new Page<>(curPage == null ? 0 : curPage, limit == null ? -1 : limit);
		List<SysRole> list = baseMapper.findList(page, params);
		return PageResult.<SysRole>builder().data(list).statusCodeValue(0).count(page.getTotal()).build();
	}

	@Override
	@Transactional
	public boolean saveOrUpdateRole(SysRole sysRole)   {
		boolean flag = false ;
		if (sysRole.getId() == null) {
			flag = this.saveRole(sysRole);
		} else {
			flag = baseMapper.updateById(sysRole) > 0 ? true : false ;
		}
		return flag ;
	}

}
