package com.open.capacity.log.aspect;

import java.lang.reflect.Type;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.open.capacity.log.model.WebLog;
import com.open.capacity.log.properties.TraceProperties;
import com.open.capacity.log.trace.MDCTraceUtils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import cn.hutool.core.convert.Convert;
import dnl.utils.text.table.TextTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnClass({ HttpServletRequest.class })
@RestControllerAdvice(annotations = { RestController.class, Controller.class })
public class WebLogBodyAdvice extends RequestBodyAdviceAdapter implements ResponseBodyAdvice<Object> {

	@Resource
	private WebLog webLog;
	@Resource
	private HttpServletRequest request;
	@Resource
	private TraceProperties traceProperties;

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return Convert.toBool(traceProperties.getEnable(), false);
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return Convert.toBool(traceProperties.getEnable(), false);
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		webLog.setPath(request.getServletPath());
		webLog.setParams(request.getParameterMap());
		webLog.setReq(body);
		return body;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		webLog.setResp(body);
		printLogTable(webLog);
		return body;
	}

	private void printLogTable(Object obj) {
		try {
			String[][] values = { { "请求响应日志", MDCTraceUtils.getTraceId(), JSON.toJSONString(obj) } };
			TextTable log = new TextTable(new String[] { "name", "traceId", "msg" }, values);
			log.printTable();
		} catch (Exception e) {
			log.error("记录异常", e);
		}
	}

}
