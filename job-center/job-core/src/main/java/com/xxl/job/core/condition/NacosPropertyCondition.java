package com.xxl.job.core.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * The Nacos auto configuration condition match
 * @author someday
 */
public class NacosPropertyCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        String nacosProperty = environment.getProperty("spring.cloud.nacos.server-addr");
        return ! StringUtils.isEmpty(nacosProperty)  ;
    }

}
