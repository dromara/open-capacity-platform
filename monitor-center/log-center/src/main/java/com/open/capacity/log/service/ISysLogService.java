package com.open.capacity.log.service;

import com.open.capacity.common.dto.PageResult;
import com.open.capacity.log.entity.SysLog;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author owen
 */
public interface ISysLogService extends IService<SysLog> {

	PageResult<SysLog> findList(Map<String, Object> params);

}
