package com.open.capacity.common.properties;

import lombok.Data;

/**
 * 应用限流
 */
@Data
public class RateLimitProperties {

	/**
	 * 是否开启监控端点
	 */
	private Boolean enable = true;

}
