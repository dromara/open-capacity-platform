package com.open.capacity.uaa.cas;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;


/**
 * @author owen
 * @date 2018/8/5 
 * 扩展cas属性类
 * blog:https://blog.51cto.com/13005375 
 * code:https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
@ConfigurationProperties(prefix = "cas")
public class CasProperties {

	private String key = "casProvider";

	private String serviceUrlPrefix = "http://127.0.0.1:8080/cas";

	private String callbackUrl = "http://localhost:8001/api/";

}
