package com.open.capacity.common.exception;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author owen
 */
public class ExceptionCondition implements Condition {

	/**
	 * 条件装配
	 */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().containsProperty("ocp.exception.notice.alertUrl") ;
    }

}
