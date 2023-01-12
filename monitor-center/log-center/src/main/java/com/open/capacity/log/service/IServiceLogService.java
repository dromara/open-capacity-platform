package com.open.capacity.log.service;

import java.util.Map;

import com.open.capacity.common.dto.PageResult;

public interface IServiceLogService {

	public PageResult queryByPage(Map<String, Object> params);
}
