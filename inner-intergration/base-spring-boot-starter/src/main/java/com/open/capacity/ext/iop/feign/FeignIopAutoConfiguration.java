/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.feign;

import com.open.capacity.ext.iop.IopContext;
import feign.Feign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.openfeign.CircuitBreakerNameResolver;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-13 15:29
 */
@Configuration
@ConditionalOnClass(Feign.class)
@EnableConfigurationProperties({ FeignClientProperties.class,
        FeignHttpClientProperties.class })
public class FeignIopAutoConfiguration {

    @Bean
    public IopContext feignIopContext() {
        return new IopContext();
    }

    @Configuration
    @ConditionalOnBean(CircuitBreakerFactory.class)
    protected static class CircuitBreakerTargeterConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public FeignIopTargeter feignIopTargeter(CircuitBreakerFactory circuitBreakerFactory,
                                                 @Value("${feign.circuitbreaker.group.enabled:false}") boolean circuitBreakerGroupEnabled,
                                                 CircuitBreakerNameResolver circuitBreakerNameResolver) {
            return new CircuitBreakerFeignIopTargeter(circuitBreakerFactory,circuitBreakerGroupEnabled,circuitBreakerNameResolver);
        }

    }
}
