package com.open.capacity.log.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.capacity.common.dto.PageResult;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.log.entity.SysLog;
import com.open.capacity.log.mapper.SysLogMapper;
import com.open.capacity.log.service.ISysLogService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author owen
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {

	@Override
	public PageResult<SysLog> findList(Map<String, Object> params) {
		
		Page<SysLog> page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
		List<SysLog> list = baseMapper.findList(page, params);
		
		return PageResult.<SysLog>builder().data(list).statusCodeValue(0).count(page.getTotal()).build();
	}

}
