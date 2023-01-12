package com.open.capacity.common.constant;

/**
 * 配置项常量
 *
 * @author zlt
 * @date 2019/9/3
 */
public interface ConfigConstants {
    /**
     * 是否开启自定义隔离规则
     */
    String CONFIG_GATEWAY_ISOLATION = "ocp.gateway.isolation";

    String CONFIG_LOADBALANCE_ISOLATION = "ocp.loadbalance.isolation";

    String CONFIG_LOADBALANCE_ISOLATION_ENABLE = CONFIG_LOADBALANCE_ISOLATION + ".enabled";
    
    String CONFIG_LOADBALANCE_ISOLATION_CHOOSER = CONFIG_LOADBALANCE_ISOLATION + ".chooser";

    String CONFIG_LOADBALANCE_VERSION = "ocp.loadbalance.version";


}
