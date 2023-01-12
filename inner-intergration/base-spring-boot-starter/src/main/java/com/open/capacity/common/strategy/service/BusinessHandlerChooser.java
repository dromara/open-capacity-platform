package com.open.capacity.common.strategy.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotationUtils;

import com.open.capacity.common.strategy.annonation.HandlerType;

/**
 * 业务处理策略选择器
 * @author someday
 * @2018-01-22
 */
public class BusinessHandlerChooser {

	private Map<HandlerType, BusinessHandler> businessHandlerMap;

	public void setBusinessHandlerMap(List<BusinessHandler> orderHandlers) {
		// 注入各类型的订单处理类
		businessHandlerMap = orderHandlers.stream().collect(
				Collectors.toMap(orderHandler -> AnnotationUtils.findAnnotation(orderHandler.getClass(), HandlerType.class),
						v -> v, (v1, v2) -> v1));
	}

	public <R, T> BusinessHandler<R, T> businessHandlerChooser(String type ) {
		HandlerType orderHandlerType = new HandlerTypeImpl(type);
		return businessHandlerMap.get(orderHandlerType);
	}
}
