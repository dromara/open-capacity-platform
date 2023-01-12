package com.open.capacity.common.idempotent;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.open.capacity.common.idempotent.aspect.IdempotentAspect;
import com.open.capacity.common.idempotent.aspect.RepeatSubmitAspect;
import com.open.capacity.common.idempotent.condition.IdempotentCondition;
import com.open.capacity.common.idempotent.expression.ExpressionResolver;
import com.open.capacity.common.idempotent.expression.KeyResolver;

/**
 * @author someday
 * @date 2018/9/25
 * 幂等插件初始化
 */
@Conditional(IdempotentCondition.class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class IdempotentAutoConfiguration {

	/**
	 * 切面 拦截处理所有 @Idempotent
	 *
	 * @return Aspect
	 */
	@Bean
	public IdempotentAspect idempotentAspect() {
		return new IdempotentAspect();
	}
	
	@Bean
	public RepeatSubmitAspect repeatSubmitAspect() {
		return new RepeatSubmitAspect();
	}

	/**
	 * key 解析器
	 *
	 * @return KeyResolver
	 */
	@Bean
	@ConditionalOnMissingBean(KeyResolver.class)
	public KeyResolver keyResolver() {
		return new ExpressionResolver();
	}

}
