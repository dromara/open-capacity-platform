package com.open.capacity.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceInfo {

	/**
	 * 浏览器名称
	 */
	private String browserName;

	/**
	 * 系统
	 */
	private String os;

	/**
	 * 系统版本
	 */
	private String osVersion;

	/**
	 * 设备类型
	 */
	private String deviceType;

	/**
	 * 厂商
	 */
	private String deviceManufacturer;

	/**
	 * 终端用户唯一标识
	 */
	private String udid;
}
