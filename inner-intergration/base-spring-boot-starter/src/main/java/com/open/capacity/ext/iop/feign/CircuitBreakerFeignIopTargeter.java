/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.feign;

import com.open.capacity.ext.commom.utli.ReflectUtils;
import feign.Feign;
import feign.Target;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.CircuitBreakerNameResolver;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignCircuitBreaker;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-13 15:13
 */
public class CircuitBreakerFeignIopTargeter implements FeignIopTargeter{
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final boolean circuitBreakerGroupEnabled;
    private final CircuitBreakerNameResolver circuitBreakerNameResolver;

    CircuitBreakerFeignIopTargeter(CircuitBreakerFactory circuitBreakerFactory, boolean circuitBreakerGroupEnabled, CircuitBreakerNameResolver circuitBreakerNameResolver) {
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.circuitBreakerGroupEnabled = circuitBreakerGroupEnabled;
        this.circuitBreakerNameResolver = circuitBreakerNameResolver;
    }

    @Override
    public <T> T target(IopFeignClientBuilder iopBuilder, Feign.Builder feign,
                        NamedContextFactory context, Target<T> target) {

        if (!(feign instanceof org.springframework.cloud.openfeign.FeignCircuitBreaker.Builder)) {
            return feign.target(target);
        } else {
            org.springframework.cloud.openfeign.FeignCircuitBreaker.Builder builder = (org.springframework.cloud.openfeign.FeignCircuitBreaker.Builder)feign;
            String name = iopBuilder.getContextId();
            Class<?> fallback = iopBuilder.getFallback();
            if (fallback != Void.TYPE) {
                return this.targetWithFallback(name, context, target, builder, fallback);
            } else {
                Class<?> fallbackFactory = iopBuilder.getFallbackFactory();
                return fallbackFactory != Void.TYPE ? this.targetWithFallbackFactory(name, context, target, builder, fallbackFactory) : this.builder(name, builder).target(target);
            }
        }

    }




    private <T> T targetWithFallbackFactory(String feignClientName, NamedContextFactory context,
                                            Target<T> target, FeignCircuitBreaker.Builder builder, Class<?> fallbackFactoryClass) {
        FallbackFactory<? extends T> fallbackFactory = (FallbackFactory<? extends T>) getFromContext("fallbackFactory",
                feignClientName, context, fallbackFactoryClass, FallbackFactory.class);
        return builder(feignClientName, builder).target(target, fallbackFactory);
    }

    private <T> T targetWithFallback(String feignClientName, NamedContextFactory context, Target<T> target,
                                     FeignCircuitBreaker.Builder builder, Class<?> fallback) {
        T fallbackInstance = getFromContext("fallback", feignClientName, context, fallback, target.type());
        return builder(feignClientName, builder).target(target, fallbackInstance);
    }

    private <T> T getFromContext(String fallbackMechanism, String feignClientName, NamedContextFactory context,
                                 Class<?> beanType, Class<T> targetType) {
        Object fallbackInstance = context.getInstance(feignClientName, beanType);
        if (fallbackInstance == null) {
            throw new IllegalStateException(
                    String.format("No " + fallbackMechanism + " instance of type %s found for feign client %s",
                            beanType, feignClientName));
        }

        if (!targetType.isAssignableFrom(beanType)) {
            throw new IllegalStateException(String.format("Incompatible " + fallbackMechanism
                            + " instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s",
                    beanType, targetType, feignClientName));
        }
        return (T) fallbackInstance;
    }

    private FeignCircuitBreaker.Builder builder(String feignClientName, FeignCircuitBreaker.Builder builder) {
        ReflectUtils.invokeMethod(builder,"circuitBreakerFactory",new Class[]{CircuitBreakerFactory.class},circuitBreakerFactory);
        ReflectUtils.invokeMethod(builder,"feignClientName",new Class[]{String.class},feignClientName);
        ReflectUtils.invokeMethod(builder,"circuitBreakerGroupEnabled",new Class[]{boolean.class},circuitBreakerGroupEnabled);
        ReflectUtils.invokeMethod(builder,"circuitBreakerNameResolver",new Class[]{CircuitBreakerNameResolver.class},circuitBreakerNameResolver);
        return builder;
    }

}
