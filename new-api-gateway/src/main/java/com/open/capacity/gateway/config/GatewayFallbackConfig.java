package com.open.capacity.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.open.capacity.gateway.error.CustomerSentinelExceptionHandler;

/**
 * @author owen
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
@SuppressWarnings("all")
public class GatewayFallbackConfig {
    
    /**
     * 限流出现Block(限制通过)时，调用处理方法，在这里指定返回内容
     * @return 返回对象
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CustomerSentinelExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new CustomerSentinelExceptionHandler();
    }
    
    

}
 