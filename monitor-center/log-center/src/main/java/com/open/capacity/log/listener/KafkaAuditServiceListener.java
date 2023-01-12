package com.open.capacity.log.listener;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.open.capacity.log.config.AuditLogSinkConfig;
import com.open.capacity.log.entity.SysLog;
import com.open.capacity.log.model.Audit;
import com.open.capacity.log.service.ISysLogService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 审计日志实现类-kafka
 *
 * @author someday
 * @date 2020/2/8 code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@Service
@AllArgsConstructor
@ConditionalOnClass(BindingServiceConfiguration.class)
@AutoConfigureBefore({ BindingServiceConfiguration.class })
@EnableBinding(AuditLogSinkConfig.Sink.class)
@ConditionalOnProperty(name = "ocp.audit-log.log-type", havingValue = "kafka")
public class KafkaAuditServiceListener {

	@Autowired
	private ISysLogService ISysLogService;

	@StreamListener(target = AuditLogSinkConfig.Sink.INPUT)
	public void auditSave(@Payload Audit audit) {

		SysLog log = Optional.ofNullable(audit).map(item -> {
			SysLog sysLog = new SysLog();
			sysLog.setUserId(item.getUserId());
			sysLog.setUserName(item.getUserName());
			sysLog.setApplicationName(item.getApplicationName());
			sysLog.setClassName(item.getClassName());
			sysLog.setClientId(audit.getClientId());
			sysLog.setMethodName(audit.getMethodName());
			sysLog.setOperation(item.getOperation());
			sysLog.setCreateTime(Date.from(item.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()));
			sysLog.setUpdateTime(new Date());
			return sysLog;
		}).orElse(null);
		ISysLogService.save(log);

	}
}
