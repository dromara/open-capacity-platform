/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.feign;

import com.open.capacity.ext.iop.BaseIopClientBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
public class FeignIopBuilder extends BaseIopClientBuilder implements InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private String name;
    private String url;
    private String path;
    private boolean decode404;
    private Class<?> fallback = void.class;
    private Class<?> fallbackFactory = void.class;

    @Override
    public <T> T buildClient() {
        IopFeignTarget iopFeignTarget = new IopFeignTarget(name,url,path);

        IopFeignClientBuilder iopFeignClientBuilder = new IopFeignClientBuilder(applicationContext,
                contextId(),decode404,fallback,fallbackFactory,getObjectType());
        return (T)iopFeignClientBuilder.getTarget(iopFeignTarget,getObjectType());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.name, "Name must be set");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDecode404(boolean decode404) {
        this.decode404 = decode404;
    }

    public void setFallback(Class<?> fallback) {
        this.fallback = fallback;
    }

    public void setFallbackFactory(Class<?> fallbackFactory) {
        this.fallbackFactory = fallbackFactory;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
