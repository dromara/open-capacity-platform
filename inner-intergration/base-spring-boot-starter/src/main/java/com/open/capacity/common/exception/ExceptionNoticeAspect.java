package com.open.capacity.common.exception;

import java.util.Date;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.log.annotation.ExceptionNoticeLog;
import com.open.capacity.log.trace.MDCTraceUtils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
 

/**
 * @author owen
 * 告警通知切面
 */
@Slf4j
@Aspect
@Component
@Conditional(ExceptionCondition.class)
@ConditionalOnClass({HttpServletRequest.class, RequestContextHolder.class})
public class ExceptionNoticeAspect {

	@Resource
	private ExceptionPublisher exceptionPublisher;
	
	@Resource
	private ObjectMapper objectMapper;
 
	/**
	 * 异常告警增强
	 * @param joinPoint
	 * @param exceptionNoticeLog
	 */
	@After("@within(exceptionNoticeLog) || @annotation(exceptionNoticeLog)")
	public void beforeMethod(JoinPoint joinPoint, ExceptionNoticeLog exceptionNoticeLog) {
		try {
			ExceptionEvent event = this.getEvent(joinPoint);
			exceptionPublisher.publishEvent(event);
		} catch (Exception e) {
		}
	}

	/**
	 * 构建异常事件
	 */
	@SneakyThrows
	private ExceptionEvent getEvent(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		Exception exception = (Exception) args[0];
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		ExceptionEvent event = ExceptionEvent.builder().build();
		event.setApplication(Optional.ofNullable(SpringUtil.getProperty("spring.application.name")).orElseGet(()->"default"));
		event.setApiPath(request.getRequestURI());
		event.setTraceId(MDCTraceUtils.getTraceId());
		event.setMessage(objectMapper.writeValueAsString(exception.getMessage()));
		event.setStackTrace(stackTrace(exception));
		return event;
	}
	
	private String stackTrace(Exception exception) {
		try {
			StackTraceElement callInfo = exception.getStackTrace()[0];
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(DateUtil.formatDateTime(new Date())).append(" ")
					.append("[" + callInfo.getClassName() + "#" + callInfo.getMethodName() + "]").append("-")
					.append("[" + callInfo.getLineNumber() + "]").append("-")
					.append("[" + Thread.currentThread().getName() + "]").append(" ");
			return stringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	 

}
