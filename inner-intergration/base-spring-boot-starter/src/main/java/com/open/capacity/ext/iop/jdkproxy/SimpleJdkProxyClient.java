/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.jdkproxy;

import com.open.capacity.ext.iop.IopClient;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@IopClient(clientBuilder = SimpleJdkProxyIopClientBuilder.class,configAnnotation = SimpleJdkProxyClient.class)
public @interface SimpleJdkProxyClient {
    Class<? extends IopMethodInvokerHandler> methodInvoker();
}
