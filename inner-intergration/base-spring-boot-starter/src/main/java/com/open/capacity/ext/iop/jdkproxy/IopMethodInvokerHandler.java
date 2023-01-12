/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.jdkproxy;

import java.lang.reflect.Method;

public interface IopMethodInvokerHandler {
    Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable;
}
