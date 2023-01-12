package com.open.capacity.log.service.impl;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.open.capacity.log.enums.LogEnums;
import com.open.capacity.log.model.Audit;
import com.open.capacity.log.model.Log;
import com.open.capacity.log.service.IAuditService;
import com.open.capacity.log.trace.MDCTraceUtils;
import com.open.capacity.log.util.BizLogUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 审计日志实现类-打印日志
 *
 * @author someday
 * @date 2020/2/3 code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "ocp.audit-log.log-type", havingValue = "logger", matchIfMissing = true)
public class LoggerAuditServiceImpl implements IAuditService {

	@Override
	public void save(Audit audit) {

		if (log.isDebugEnabled()) {

			Map params = Maps.newHashMap();
			params.put("classname", audit.getClassName());
			params.put("method", audit.getMethodName());
			params.put("params", audit.getParams());

			BizLogUtil.info(LogEnums.AUDIT_LOG.getTag(), LogEnums.AUDIT_LOG.getName(),
					Log.builder().objectId(LogEnums.AUDIT_LOG.getId()).traceId(MDCTraceUtils.getTraceId())
							.spanId(MDCTraceUtils.getSpanId()).clientIp(audit.getIp()).userId(audit.getUserId())
							.userName(audit.getUserName()).clientId(audit.getClientId()).msg(audit.getOperation())
							.objectParam(params).build());
		}

	}
}
