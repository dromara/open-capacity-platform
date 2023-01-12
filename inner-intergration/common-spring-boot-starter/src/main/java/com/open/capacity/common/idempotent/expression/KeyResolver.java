package com.open.capacity.common.idempotent.expression;

import org.aspectj.lang.JoinPoint;

import com.open.capacity.common.idempotent.annotation.Idempotent;

/**
 * @author someday
 * @date 2018/9/25
 * 唯一标志处理器
 */
public interface KeyResolver {

	/**
	 * 解析处理 key
	 *
	 * @param idempotent 接口注解标识
	 * @param point      接口切点信息
	 * @return 处理结果
	 */
	String resolver(Idempotent idempotent, JoinPoint point);

}
