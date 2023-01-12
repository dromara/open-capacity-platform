package com.open.capacity.common.properties;

import lombok.Data;

@Data
public class AntiReplayProperties {

	/**
	 * 是否防重放
	 */
	private Boolean enable = false;

	/**
	 * 密钥
	 */
	private String key ="1111111222212345";

	/**
	 * 过期时间 单位 /秒
	 */
	private Integer expireTime = 60;
}
