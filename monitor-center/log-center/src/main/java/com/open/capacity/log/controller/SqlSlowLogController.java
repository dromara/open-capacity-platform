package com.open.capacity.log.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.dto.PageResult;
import com.open.capacity.log.service.ISqlSlowLogService;

/**
 * ELK收集mysql慢查询日志数据
 */
@RestController
public class SqlSlowLogController {

	@Autowired
	private ISqlSlowLogService sqlSlowLogService;

	@GetMapping(value = "/slowQueryLog")
	public PageResult queryByPage(@RequestParam Map<String, Object> params) {

		return sqlSlowLogService.queryByPage(params);

	}
}
