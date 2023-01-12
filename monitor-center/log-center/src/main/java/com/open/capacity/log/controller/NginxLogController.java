package com.open.capacity.log.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.dto.PageResult;
import com.open.capacity.log.service.INginxLogService;


/**
 * nginx日志查询
 */
@RestController
public class NginxLogController {

	@Autowired
	private INginxLogService nginxLogService;

	@GetMapping(value = "/nginxLog")
	public PageResult queryByPage(@RequestParam Map<String, Object> params)  {

		return nginxLogService.queryByPage(params);

	}
}
