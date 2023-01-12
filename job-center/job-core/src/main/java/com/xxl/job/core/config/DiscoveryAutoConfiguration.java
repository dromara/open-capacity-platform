package com.xxl.job.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.xxl.job.core.condition.NacosPropertyCondition;
import com.xxl.job.core.discovery.DiscoveryProcessor;
import com.xxl.job.core.discovery.NacosDiscoveryProcessor;

/**
 * registry center auto configuration class
 * @author someday
 */
@Configuration
public class DiscoveryAutoConfiguration {

    @Bean
    @Conditional(value = NacosPropertyCondition.class)
    public DiscoveryProcessor initNacos() {
        return new NacosDiscoveryProcessor();
    }
}
