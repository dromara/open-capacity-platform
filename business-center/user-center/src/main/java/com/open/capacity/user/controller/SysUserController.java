package com.open.capacity.user.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.open.capacity.common.annotation.LoginUser;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.constant.UserType;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.common.model.LoginAppUser;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.ExcelUtil;
import com.open.capacity.log.annotation.AuditLog;
import com.open.capacity.user.model.SysUserExcel;
import com.open.capacity.user.service.ISysUserService;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen 
 * implements 溯源1对1
 * E-mail: 624191343@qq.com 用户
 */
@Slf4j
@RestController
@Api(tags = "用户模块api")
public class SysUserController implements UserFeignClient{
	private static final String ADMIN_CHANGE_MSG = "超级管理员不给予修改";

	@Autowired
	private ISysUserService sysUserService;

	/**
	 *  当前登录用户 LoginAppUser
	 * 
	 * @param user
	 * @return
	 */
	@GetMapping("/users/current")
	@ApiOperation(value = "根据access_token当前登录用户")
	public ResponseEntity<LoginAppUser> getLoginAppUser(@LoginUser(isFull = true) SysUser user) {
		return ResponseEntity.succeed(sysUserService.getLoginAppUser(user));
	}

	/**
	 * 查询用户实体对象SysUser
	 * 
	 * @param username
	 * @return
	 */
	@GetMapping(value = "/users/name/{username}")
	@ApiOperation(value = "根据用户名查询用户实体")
	@Cacheable(value = "user", key = "#username", unless = "#result == null")
	public SysUser selectByUsername(@PathVariable String username) {
		return sysUserService.selectByUsername(username);
	}

	/**
	 * 查询用户登录对象LoginAppUser
	 * 
	 * @param username
	 * @return
	 */
	@GetMapping(value = "/users-anon/login", params = "username")
	@ApiOperation(value = "根据用户名查询用户")
	public LoginAppUser findByUsername(String username) {
		return sysUserService.findByUsername(username);
	}

	/**
	 * 通过手机号查询用户、角色信息
	 *
	 * @param mobile 手机号
	 * @return
	 */
	@GetMapping(value = "/users-anon/mobile", params = "mobile")
	@ApiOperation(value = "根据手机号查询用户")
	public LoginAppUser findByMobile(String mobile) {
		return sysUserService.findByMobile(mobile);
	}

	/**
	 * 根据userId查询用户信息
	 *
	 * @param userId  
	 * @return 
	 */
	@GetMapping(value = "/users-anon/userId", params = "userId")
	@ApiOperation(value = "根据UserId查询用户")
	public LoginAppUser findByUserId(String userId) {
		return sysUserService.findByUserId(userId);
	}

	/**
	 * 根据OpenId查询用户信息
	 *
	 * @param openId  
	 * @return  
	 */
	@GetMapping(value = "/users-anon/openId", params = "openId")
	@ApiOperation(value = "根据OpenId查询用户")
	public LoginAppUser findByOpenId(String openId) {
		return sysUserService.findByOpenId(openId);
	}
	
	
	/***
	 * 根据id查询用户
	 * @param id
	 * @return
	 */
	@GetMapping("/users/{id}")
	@ApiOperation(value = "根据id查询用户")
	public SysUser findUserById(@PathVariable Long id) {
		return sysUserService.getById(id);
	}

	/**
	 * 获取用户的角色
	 *
	 * @param
	 * @return
	 */
	@GetMapping("/users/{id}/roles")
	@ApiOperation(value = "根据id查询用户角色")
	public List<SysRole> findRolesByUserId(@PathVariable Long id) {
		return sysUserService.findRolesByUserId(id);
	}

	/**
	 * 用户查询
	 *
	 * @param params
	 * @return
	 */
	@GetMapping("/users")
	@ApiOperation(value = "用户查询列表")
	@ApiImplicitParams({ @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
			@ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer") })
	@AuditLog(operation = " #sysUser.username  + '查看了用户列表'")
	public PageResult<SysUser> findUsers(@RequestParam Map<String, Object> params,
			@LoginUser(isFull = true) SysUser sysUser) {
		return sysUserService.findUsers(params);
	}

	/**
	 * 修改用户状态
	 *
	 * @param params
	 * @return
	 */
	@GetMapping("/users/updateEnabled")
	@ApiOperation(value = "修改用户状态")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer"),
			@ApiImplicitParam(name = "enabled", value = "是否启用", required = true, dataType = "Boolean") })
	public ResponseEntity updateEnabled(@RequestParam Map<String, Object> params) {
		Long id = MapUtils.getLong(params, "id");
		if (checkAdmin(id)) {
			return ResponseEntity.failed(ADMIN_CHANGE_MSG);
		}

		Try.of(() -> sysUserService.updateEnabled(params)).onFailure(ex -> log.error("user-updateEnabled-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));

		return ResponseEntity.succeed("操作成功");

	}

	/**
	 * 管理后台，给用户重置密码
	 * 
	 * @param id
	 */
	@PutMapping(value = "/users/{id}/password")
	@ApiOperation(value = "修改用户密码")
	public ResponseEntity resetPassword(@PathVariable Long id) {
		if (checkAdmin(id)) {
			return ResponseEntity.failed(ADMIN_CHANGE_MSG);
		}

		Try.of(() -> sysUserService.updatePassword(id, null, null))
				.onFailure(ex -> log.error("user-resetPassword-error", ex))
				.getOrElseThrow(item -> new BusinessException("重置失败"));

		return ResponseEntity.succeed("重置成功");
	}

	/**
	 * 用户自己修改密码
	 */
	@PutMapping(value = "/users/password")
	@ApiOperation(value = "修改用户密码")
	public ResponseEntity resetPassword(@RequestBody SysUser sysUser) {

		if (checkAdmin(sysUser.getId())) {
			return ResponseEntity.failed(ADMIN_CHANGE_MSG);
		}

		Try.of(() -> sysUserService.updatePassword(sysUser.getId(), sysUser.getOldPassword(), sysUser.getNewPassword()))
				.onFailure(ex -> log.error("user-resetPassword-error", ex))
				.getOrElseThrow(item -> new BusinessException("重置失败"));

		return ResponseEntity.succeed("重置成功");

	}

	/**
	 * 删除用户
	 *
	 * @param id
	 */
	@DeleteMapping(value = "/users/{id}")
	@ApiOperation(value = "删除用户")
	public ResponseEntity delete(@PathVariable Long id) {
		if (checkAdmin(id)) {
			return ResponseEntity.failed(ADMIN_CHANGE_MSG);
		}

		Try.of(() -> sysUserService.delUser(id)).onFailure(ex -> log.error("user-delete-error", ex))
				.getOrElseThrow(item -> new BusinessException("删除失败"));

		return ResponseEntity.succeed("删除成功");
	}

	/**
	 * 新增or更新
	 *
	 * @param sysUser
	 * @return
	 */
	@SneakyThrows
	@PostMapping("/users/saveOrUpdate")
	@CacheEvict(value = "user", key = "#sysUser.username")
	@AuditLog(operation = "'新增或更新用户:' + #sysUser.username")
	public ResponseEntity saveOrUpdate(@RequestBody SysUser sysUser) {

		Try.of(() -> sysUserService.saveOrUpdateUser(sysUser)).onFailure(ex -> log.error("user-saveOrUpdate-error", ex))
				.getOrElseThrow(item -> new BusinessException("操作失败"));

		return ResponseEntity.succeed("操作成功");

	}

	/**
	 * 导出excel
	 *
	 * @return
	 */
	@SneakyThrows
	@PostMapping("/users/export")
	@ApiOperation(value = "用户导出")
	public void exportUser(@RequestParam Map<String, Object> params, HttpServletResponse response) {
		List<SysUserExcel> listUsers = sysUserService.findAllUsers(params);

		try (Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, "用户", ExcelType.XSSF),SysUserExcel.class, listUsers);) {
			String file = "users.xlsx";
			// 导出excel
			ExcelUtil.exportExcelWithWaterMark(workbook, file, response);
		}

	}

	@SneakyThrows
	@PostMapping(value = "/users/import")
	@ApiOperation(value = "用户导入")
	public ResponseEntity importExcl(@RequestParam("file") MultipartFile excl) {
		int rowNum = 0;
		if (!excl.isEmpty()) {
			List<SysUserExcel> list = ExcelUtil.importExcel(excl, 0, 1, SysUserExcel.class);
			rowNum = list.size();
			if (rowNum > 0) {
				List<SysUser> users = new ArrayList<>(rowNum);
				list.forEach(u -> {
					SysUser user = new SysUser();
					BeanUtil.copyProperties(u, user);
					user.setPassword(CommonConstant.DEF_USER_PASSWORD);
					user.setType(UserType.BACKEND.name());
					users.add(user);
				});
				sysUserService.saveBatch(users);
			}
		}
		return ResponseEntity.succeed("导入数据成功，一共【" + rowNum + "】行");
	}

	/**
	 * 是否超级管理员
	 */
	private boolean checkAdmin(long id) {
		return id == 1L;
	}
}
