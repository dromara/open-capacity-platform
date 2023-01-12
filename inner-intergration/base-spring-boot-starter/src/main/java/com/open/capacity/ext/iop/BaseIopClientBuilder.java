/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop;

public abstract class BaseIopClientBuilder implements IopClientBuilder{
    private Class interfaceClazz;

    @Override
    public <T> Class<T> getInterfaceClazz() {
        return interfaceClazz;
    }

    @Override
    public <T> void setInterfaceClazz(Class<T> interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }
}