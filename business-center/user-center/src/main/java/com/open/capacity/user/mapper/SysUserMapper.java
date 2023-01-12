package com.open.capacity.user.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.db.mapper.BaseMapper;

/**
 * 用户表 Mapper 接口
 *
 * @author someday
 * @data 2018-10-29
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
	
	/**
	 * 数据脱敏
	 * @param sysUser
	 */
	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into sys_user( username,password,mobile,type,create_time,update_time) values (#{username,typeHandler=com.open.capacity.db.typehandler.CryptTypeHandler},  #{password,typeHandler=com.open.capacity.db.typehandler.CryptTypeHandler}, #{mobile,typeHandler=com.open.capacity.db.typehandler.CryptTypeHandler}, #{type}, #{createTime}, #{updateTime})")
	void insertEnc(SysUser sysUser);

	/**
	 * 脱敏查询
	 * @param page
	 * @param sysUser
	 * @return
	 */
    List<SysUser> selectListEnc(Page<SysUser> page, SysUser sysUser);
	
    /**
     * 分页查询用户列表
     * @param page
     * @param params
     * @return
     */
    List<SysUser> findList(Page<SysUser> page, @Param("params") Map<String, Object> params);
}
