/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.dynamic;

import com.open.capacity.ext.iop.feign.IopFeignClientBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DymFeignIopClientsBuilder extends BaseDymIopClientBuilder implements  ApplicationContextAware {
    private ApplicationContext applicationContext;
    private boolean decode404;
    private Class<?> fallback = void.class;
    private Class<?> fallbackFactory = void.class;

    @Override
    public DymIopClientFactory getObject() throws Exception {
        IopFeignClientBuilder iopFeignClientBuilder = new IopFeignClientBuilder(applicationContext,
                contextId(),decode404,fallback,fallbackFactory,getObjectType());
        return new DymFeignIopClientFactory(getInterfaceClazz(),getDymKeySelector(),iopFeignClientBuilder);
    }

    @Override
    public Class<?> getObjectType() {
        return DymFeignIopClientFactory.class;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
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
}
