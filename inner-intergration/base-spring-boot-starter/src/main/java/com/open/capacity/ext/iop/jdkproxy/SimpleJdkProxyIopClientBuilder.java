/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.jdkproxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;

public class SimpleJdkProxyIopClientBuilder extends JdkProxyIopClientBuilder implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext ;
    private Class<? extends IopMethodInvokerHandler> methodInvoker;
    private IopMethodInvokerHandler invoker;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoker.invoke(proxy, method, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setMethodInvoker(Class<? extends IopMethodInvokerHandler> methodInvoker) {
        this.methodInvoker = methodInvoker;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        invoker = applicationContext.getBean(methodInvoker);
    }
}
