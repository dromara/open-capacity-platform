package com.open.capacity.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;


/**

 * 配置alertmanager告警地址
 * 配置格式：http://192.168.11.168:9093/api/v1/alerts
 * @author owen
 *
 */
@Data
@ConfigurationProperties(prefix = "ocp.exception.notice")
public class ExceptionNoticeProperties {

	private String alertUrl;
}