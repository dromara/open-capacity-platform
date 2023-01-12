package com.open.capacity.common.properties;

import lombok.Data;

/**
 * 服务端点是否打开
 *
 * @author someday
 * @date 2019/8/13
 */
@Data
public class ActuatorProperties {
	/**
	 * 是否开启监控端点
	 */
	private Boolean enable = true ;
}
