package com.open.capacity.db.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DynamicDataSourceCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return   "true".equals(context.getEnvironment().getProperty("spring.datasource.dynamic.enabled"));
	}

}
