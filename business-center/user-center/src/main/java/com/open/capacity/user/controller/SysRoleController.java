package com.open.capacity.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.user.service.ISysRoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen E-mail: 624191343@qq.com 角色管理
 */
@Slf4j
@RestController
@Api(tags = "角色模块api")
public class SysRoleController {
	@Autowired
	private ISysRoleService sysRoleService;

	/**
	 * 后台管理查询角色
	 * 
	 * @param params
	 * @return
	 */
	@GetMapping("/roles")
	@ApiOperation(value = "后台管理查询角色")
	public PageResult<SysRole> findRoles(@RequestParam Map<String, Object> params) {
		return sysRoleService.findRoles(params);
	}

	/**
	 * 用户管理查询所有角色
	 * 
	 * @return
	 */
	@GetMapping("/allRoles")
	@ApiOperation(value = "后台管理查询角色")
	public ResponseEntity<List<SysRole>> findAll() {
		List<SysRole> result = sysRoleService.list();
		return ResponseEntity.succeed(result);
	}

	/**
	 * 角色新增或者更新
	 *
	 * @param sysRole
	 * @return
	 */
	@SneakyThrows
	@PostMapping("/roles/saveOrUpdate")
	@ApiOperation(value = "后台管理保存更新角色")
	public ResponseEntity saveOrUpdate(@RequestBody SysRole sysRole) {

		Try.of(() -> sysRoleService.saveOrUpdateRole(sysRole)).onFailure(ex -> log.error("role-saveOrUpdate-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));
		return ResponseEntity.succeed("操作成功");

	}

	/**
	 * 后台管理删除角色 delete /role/1
	 *
	 * @param id
	 */
	@DeleteMapping("/roles/{id}")
	@ApiOperation(value = "后台管理删除角色")
	public ResponseEntity deleteRole(@PathVariable Long id) {

		if (id == 1L) {
			return ResponseEntity.failed("管理员不可以删除");
		}

		Try.of(() -> sysRoleService.deleteRole(id)).onFailure(ex -> log.error("role-deleteRole-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));
		return ResponseEntity.succeed("操作成功");

	}
}
