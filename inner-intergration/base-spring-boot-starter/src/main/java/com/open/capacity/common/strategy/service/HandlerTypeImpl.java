package com.open.capacity.common.strategy.service;

import java.lang.annotation.Annotation;
import com.open.capacity.common.strategy.annonation.HandlerType;

/**
 * 策略模型业务类型注解实现类
 * @author someday @2018-01-22
 */
public class HandlerTypeImpl implements HandlerType {

	private final String type;

	HandlerTypeImpl(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		hashCode += (127 * "type".hashCode()) ^ type.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HandlerType)) {
			return false;
		}
		HandlerType other = (HandlerType) obj;
		return type.equals(other.type());
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return HandlerType.class;
	}

	@Override
	public String type() {
		return type;
	}

}
