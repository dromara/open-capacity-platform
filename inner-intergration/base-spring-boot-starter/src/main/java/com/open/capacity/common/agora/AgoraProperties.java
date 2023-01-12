package com.open.capacity.common.agora;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * ons 多线程发送配置参数 corePoolSize 线程池核心池的大小 maximumPoolSize 线程池中允许的最大线程数量
 * keepAliveTime 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间 unit keepAliveTime 的时间单位
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
@ConfigurationProperties(prefix = AgoraProperties.PREFIX)
public class AgoraProperties {

	/**
	 * The prefix of the property of {@link AgoraProperties}.
	 */
	public static final String PREFIX = "agora";

	/** appId */
	private String appId;
	/** 证书 */
	private String appCertificate;
	/** token过期时间 */
	private int expirationTimeInSeconds = 3600;
	/** 声网restful登录key: 必填 */
	private String loginKey;
	/** 声网restful登录密钥: 必填 */
	private String loginSecret;

	/** 录制区域选择 7-香港 10 -新加坡 */
	private Integer ossRegion;

	/** 声网视频宽度 */
	private Integer viewWidth;

	/** 声网视频高度 */
	private Integer viewHeight;

}
