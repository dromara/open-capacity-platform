package com.open.capacity.log.service.impl;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import com.open.capacity.log.config.AuditLogStreamsConfig;
import com.open.capacity.log.model.Audit;
import com.open.capacity.log.service.IAuditService;

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
@EnableBinding(AuditLogStreamsConfig.Source.class)
@ConditionalOnProperty(name = "ocp.audit-log.log-type", havingValue = "kafka")
public class KafkaAuditServiceImpl implements IAuditService {

	private AuditLogStreamsConfig.Source source;

	@Override
	public void save(Audit audit) {
		source.output().send(MessageBuilder.withPayload(audit)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build());

	}
}
