package com.open.capacity.common.feign;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;

import feign.Logger.Level;
import feign.Request;

/**
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class GlobalFeignConfig {

	/**
	 * 日志处理
	 * @return
	 */
    @Bean
    public Level level() {
        return Level.FULL;
    }
    /**
     * 全局超时配置
     * @return
     */
    @Bean
    public Request.Options options(){
    	return new Request.Options(5000, TimeUnit.MILLISECONDS, 10000, TimeUnit.MILLISECONDS, true);
    }
}
