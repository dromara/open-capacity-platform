package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * nacos 配置
 *
 * @author someday
 * @date 2020/4/5
 *  code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "nacos")
public class NacosProperties {
    /**
     * nacos地址
     */
    private String address  ;
    /**
     * nacos命名空间
     */
    private String namespace  ;
}
