package com.open.capacity.gateway.config;

import com.open.capacity.common.config.DefaultAsycTaskConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @author someday
 * 线程池配置、启用异步
 * @Async quartz 需要使用
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
public class CustomerAsycTaskConfig extends DefaultAsycTaskConfig {

}
