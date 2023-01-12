package com.open.capacity.common.lb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RestTemplate 配置
 *
 * @author someday
 * @date 2017/11/17
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "ocp.rest-template")
public class RestTemplateProperties {
    /**
     * 最大链接数
     */
    private int maxTotal = 1000;
    /**
     * 同路由最大并发数
     */
    private int maxPerRoute = 1000;
    /**
     * 读取超时时间 ms
     */
    private int readTimeout = 35000;
    /**
     * 客户端和服务器建立连接的timeout ms
     */
    private int connectTimeout = 10000;
    
    /**
     * 连接池获取连接的超时时间 ms
     */
    private int connectionRequestTimeout = 200 ;
     
}
