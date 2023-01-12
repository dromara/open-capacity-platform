package com.open.capacity.file.config;

import com.open.capacity.common.config.DefaultAsycTaskConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @author someday
 * 线程池配置、启用异步
 * @Async quartz 需要使用
 */
@Configuration
public class CustomerAsycTaskConfig extends DefaultAsycTaskConfig {

}
