package com.open.capacity.common.constant;

/**
 * code定义
 * @Author: someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public enum CodeEnum {
	SUCCESS(0), ERROR(1);

	private Integer statusCodeValue;

	CodeEnum(Integer statusCodeValue) {
		this.statusCodeValue = statusCodeValue;
	}

	public Integer getStatusCodeValue() {
		return statusCodeValue;
	}
}
