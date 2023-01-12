package com.open.capacity.user.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.capacity.common.model.SysRole;
import com.open.capacity.db.mapper.BaseMapper;

/**
* @author someday
 * 角色
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
	
	/***
	 * 分页查询角色列表
	 * @param page
	 * @param params
	 * @return
	 */
	List<SysRole> findList(Page<SysRole> page, @Param("params") Map<String, Object> params);
}
