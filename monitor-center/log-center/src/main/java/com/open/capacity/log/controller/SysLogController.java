package com.open.capacity.log.controller;


import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.dto.PageResult;
import com.open.capacity.log.entity.SysLog;
import com.open.capacity.log.service.ISysLogService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author owen
 */
@RestController
public class SysLogController {
	
	@Resource
	private ISysLogService sysLogService;
	
	/**
	 * 日志查询
	 *
	 * @param params
	 * @return
	 */
	@RequestMapping("/auditLog")
	public PageResult<SysLog> findFiles(@RequestParam Map<String, Object> params) {
		
		return sysLogService.findList(params);
	}

	
}

