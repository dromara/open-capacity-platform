package com.open.capacity.log.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.capacity.log.entity.SysLog;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author owen
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {

	List<SysLog> findList(Page<SysLog> page, Map<String, Object> params);
	
	
}
