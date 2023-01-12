package com.open.capacity.common.strategy.service;

/**
 * 业务处理策略接口
 * @author someday
 * @2018-01-22
 */
public interface BusinessHandler<R, T> {

	/**
	 * 业务处理
	 *
	 * @param t 业务实体返回参数
	 * @return R　结果
	 */
	R businessHandler(T t);
}
