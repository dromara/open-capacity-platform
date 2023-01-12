/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.feign;

import com.open.capacity.ext.exception.HsExtRuntimeException;
import com.open.capacity.ext.iop.IopContext;
import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.cloud.openfeign.loadbalancer.RetryableFeignBlockingLoadBalancerClient;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Objects;

public class IopFeignClientBuilder {
    private ApplicationContext applicationContext;
    private String contextId;
    private boolean decode404;
    private Class<?> fallback ;
    private Class<?> fallbackFactory ;
    private Class objectType;


    public IopFeignClientBuilder(ApplicationContext applicationContext,String contextId,  boolean decode404, Class<?> fallback, Class<?> fallbackFactory, Class objectType) {
        this.applicationContext = applicationContext;
        this.contextId = contextId;
        this.decode404 = decode404;
        this.fallback = fallback;
        this.fallbackFactory = fallbackFactory;
        this.objectType = objectType;
    }

    protected Feign.Builder feign(IopContext context) {
        FeignLoggerFactory loggerFactory = get(context, FeignLoggerFactory.class);
        Logger logger = loggerFactory.create(objectType);

        // @formatter:off
        Feign.Builder builder = get(context, Feign.Builder.class)
                // required values
                .logger(logger)
                .encoder(get(context, Encoder.class))
                .decoder(get(context, Decoder.class))
                .contract(get(context, Contract.class));
        // @formatter:on

        configureFeign(context, builder);

        return builder;
    }

    protected void configureFeign(IopContext context, Feign.Builder builder) {
        FeignClientProperties properties = this.applicationContext
                .getBean(FeignClientProperties.class);
        if (properties != null) {
            if (properties.isDefaultToProperties()) {
                configureUsingConfiguration(context, builder);
                configureUsingProperties(
                        properties.getConfig().get(properties.getDefaultConfig()),
                        builder);
                configureUsingProperties(properties.getConfig().get(this.contextId),
                        builder);
            }
            else {
                configureUsingProperties(
                        properties.getConfig().get(properties.getDefaultConfig()),
                        builder);
                configureUsingProperties(properties.getConfig().get(this.contextId),
                        builder);
                configureUsingConfiguration(context, builder);
            }
        }
        else {
            configureUsingConfiguration(context, builder);
        }
    }

    protected void configureUsingConfiguration(IopContext context,
                                               Feign.Builder builder) {
        Logger.Level level = getOptional(context, Logger.Level.class);
        if (level != null) {
            builder.logLevel(level);
        }
        Retryer retryer = getOptional(context, Retryer.class);
        if (retryer != null) {
            builder.retryer(retryer);
        }
        ErrorDecoder errorDecoder = getOptional(context, ErrorDecoder.class);
        if (errorDecoder != null) {
            builder.errorDecoder(errorDecoder);
        }
        Request.Options options = getOptional(context, Request.Options.class);
        if (options != null) {
            builder.options(options);
        }
        Map<String, RequestInterceptor> requestInterceptors = context
                .getInstances(this.contextId, RequestInterceptor.class);
        if (requestInterceptors != null) {
            builder.requestInterceptors(requestInterceptors.values());
        }

        if (this.decode404) {
            builder.decode404();
        }
    }

    protected void configureUsingProperties(
            FeignClientProperties.FeignClientConfiguration config,
            Feign.Builder builder) {
        if (config == null) {
            return;
        }

        if (config.getLoggerLevel() != null) {
            builder.logLevel(config.getLoggerLevel());
        }

        if (config.getConnectTimeout() != null && config.getReadTimeout() != null) {
            builder.options(new Request.Options(config.getConnectTimeout(),
                    config.getReadTimeout()));
        }

        if (config.getRetryer() != null) {
            Retryer retryer = getOrInstantiate(config.getRetryer());
            builder.retryer(retryer);
        }

        if (config.getErrorDecoder() != null) {
            ErrorDecoder errorDecoder = getOrInstantiate(config.getErrorDecoder());
            builder.errorDecoder(errorDecoder);
        }

        if (config.getRequestInterceptors() != null
                && !config.getRequestInterceptors().isEmpty()) {
            // this will add request interceptor to builder, not replace existing
            for (Class<RequestInterceptor> bean : config.getRequestInterceptors()) {
                RequestInterceptor interceptor = getOrInstantiate(bean);
                builder.requestInterceptor(interceptor);
            }
        }

        if (config.getDecode404() != null) {
            if (config.getDecode404()) {
                builder.decode404();
            }
        }

        if (Objects.nonNull(config.getEncoder())) {
            builder.encoder(getOrInstantiate(config.getEncoder()));
        }

        if (Objects.nonNull(config.getDecoder())) {
            builder.decoder(getOrInstantiate(config.getDecoder()));
        }

        if (Objects.nonNull(config.getContract())) {
            builder.contract(getOrInstantiate(config.getContract()));
        }
    }

    private <T> T getOrInstantiate(Class<T> tClass) {
        try {
            return this.applicationContext.getBean(tClass);
        }
        catch (NoSuchBeanDefinitionException e) {
            return BeanUtils.instantiateClass(tClass);
        }
    }

    protected <T> T get(IopContext context, Class<T> type) {
        T instance = context.getInstance(this.contextId, type);
        if (instance == null) {
            throw new IllegalStateException(
                    "No bean found of type " + type + " for " + this.contextId);
        }
        return instance;
    }


    protected Object  get(IopContext context, String className) {
        Class clazz;
        try {
            clazz = Class.forName(className);
        }catch (Exception e){
            throw new HsExtRuntimeException(className + " not found",e);
        }

        Object instance = context.getInstance(this.contextId, clazz);
        if (instance == null) {
            throw new IllegalStateException(
                    "No bean found of type " + className + " for " + this.contextId);
        }
        return instance;
    }

    protected <T> T getOptional(IopContext context, Class<T> type) {
        return context.getInstance(this.contextId, type);
    }

    public <T> T getTarget(IopFeignTarget target,Class<T> objectType) {
        IopContext context = this.applicationContext.getBean(IopContext.class);
        Feign.Builder builder = feign(context);

        return targetObj(context,builder, target.buildTarget(objectType), target.isLoadBalancer());
    }

    private <T> T targetObj(IopContext context, Feign.Builder builder,Target<T> target,boolean loadBalancer){
        Client client = getOptional(context, Client.class);
        if (client != null) {
            if (loadBalancer){
                throw new IllegalStateException(
                        "No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-netflix-ribbon?");
            }else {
                if (client instanceof FeignBlockingLoadBalancerClient) {
                    client = ((FeignBlockingLoadBalancerClient)client).getDelegate();
                }

                if (client instanceof RetryableFeignBlockingLoadBalancerClient) {
                    client = ((RetryableFeignBlockingLoadBalancerClient)client).getDelegate();
                }
            }

            builder.client(client);
        }
        FeignIopTargeter targeter = get(context, FeignIopTargeter.class);
        return targeter.target(this, builder, context, target);
    }

    public String getContextId() {
        return contextId;
    }

    public boolean isDecode404() {
        return decode404;
    }

    public Class<?> getFallback() {
        return fallback;
    }

    public Class<?> getFallbackFactory() {
        return fallbackFactory;
    }

}
