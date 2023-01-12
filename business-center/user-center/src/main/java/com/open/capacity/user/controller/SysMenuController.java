package com.open.capacity.user.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.open.capacity.common.annotation.LoginUser;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.model.SysMenu;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.EntityUtils;
import com.open.capacity.common.utils.TreeUtil;
import com.open.capacity.user.service.ISysMenuService;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 */

@Slf4j
@RestController
@Api(tags = "菜单模块api")
@RequestMapping("/menus")
@SuppressWarnings("unchecked")
public class SysMenuController {
	@Autowired
	private ISysMenuService menuService;

	/**
	 * 删除菜单
	 *
	 * @param id
	 */
	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除菜单")
	public ResponseEntity delete(@PathVariable Long id) {

		Try.of(() -> menuService.removeById(id)).onFailure(ex -> log.error("memu-delete-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));
		return ResponseEntity.succeed("操作成功");

	}

	@GetMapping("/{roleId}/menus")
	@ApiOperation(value = "根据roleId获取对应的菜单")
	public List<Map<String, Object>> findMenusByRoleId(@PathVariable Long roleId) {
		Set<Long> roleIds = new HashSet<>();
		roleIds.add(roleId);
		// 获取该角色对应的菜单
		List<SysMenu> roleMenus = menuService.findByRoles(roleIds);
		// 全部的菜单列表
		List<SysMenu> allMenus = menuService.list(Wrappers.<SysMenu>lambdaQuery().orderByAsc(SysMenu::getSort));
		List<Map<String, Object>> authTrees = new ArrayList<>();

		Map<Long, SysMenu> roleMenusMap =  EntityUtils.toMap(roleMenus, SysMenu::getId);

		for (SysMenu sysMenu : allMenus) {
			Map<String, Object> authTree = new HashMap<>();
			authTree.put("id", sysMenu.getId());
			authTree.put("name", sysMenu.getName());
			authTree.put("pId", sysMenu.getParentId());
			authTree.put("open", true);
			authTree.put("checked", false);
			if (roleMenusMap.get(sysMenu.getId()) != null) {
				authTree.put("checked", true);
			}
			authTrees.add(authTree);
		}
		return authTrees;
	}
	
	@GetMapping("/{roleCodes}")
	@ApiOperation(value = "根据roleCodes获取对应的权限")
	@Cacheable(value = "menu", key = "#roleCodes", unless = "#result == null")
	public List<SysMenu> findMenuByRoles(@PathVariable String roleCodes) {
		List<SysMenu> ResponseEntity = null;
		if (StringUtils.isNotEmpty(roleCodes)) {
			Set<String> roleSet = Convert.toSet(String.class, roleCodes) ;
			ResponseEntity = menuService.findByRoleCodes(roleSet, CommonConstant.PERMISSION);
		}
		return ResponseEntity;
	}

	/**
	 * 给角色分配菜单
	 */
	@ApiOperation(value = "角色分配菜单")
	@PostMapping("/granted")
	public ResponseEntity setMenuToRole(@RequestBody SysMenu sysMenu) {

		Try.of(() -> menuService.setMenuToRole(sysMenu.getRoleId(), sysMenu.getMenuIds()))
				.onFailure(ex -> log.error("memu-granted-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));
		return ResponseEntity.succeed("操作成功");

	}

	@ApiOperation(value = "查询所有菜单")
	@GetMapping("/findAlls")
	public PageResult<SysMenu> findAlls() {
		List<SysMenu> list = menuService.list(Wrappers.<SysMenu>lambdaQuery().orderByAsc(SysMenu::getSort));
		return PageResult.<SysMenu>builder().data(list).statusCodeValue(0).count((long) list.size()).build();
	}

	@ApiOperation(value = "获取菜单以及顶级菜单")
	@GetMapping("/findOnes")
	public PageResult<SysMenu> findOnes() {
		List<SysMenu> list = menuService.findOnes();
		return PageResult.<SysMenu>builder().data(list).statusCodeValue(0).count((long) list.size()).build();
	}

	/**
	 * 添加菜单 或者 更新
	 *
	 * @param menu
	 * @return
	 */
	@ApiOperation(value = "新增菜单")
	@PostMapping("saveOrUpdate")
	public ResponseEntity saveOrUpdate(@RequestBody SysMenu menu) {

		Try.of(() -> menuService.saveOrUpdate(menu)).onFailure(ex -> log.error("memu-saveOrUpdate-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));
		return ResponseEntity.succeed("操作成功");

	}

	/**
	 * 当前登录用户的菜单
	 *
	 * @return
	 */
	@GetMapping("/current")
	@ApiOperation(value = "查询当前用户菜单")
	public List<SysMenu> findMyMenu(@LoginUser SysUser user) {
		List<SysRole> roles = user.getRoles();
		if (CollectionUtil.isEmpty(roles)) {
			return Collections.emptyList();
		}
		List<SysMenu> menus = menuService
				.findByRoleCodes(EntityUtils.toSet(roles, SysRole::getCode), CommonConstant.MENU);
		
		return TreeUtil.listToTree(menus, SysMenu::setSubMenus ,  SysMenu::getId,
				SysMenu::getParentId, (node) ->  ObjectUtil.equal(-1L, node.getParentId()));
		
	}

}
