/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.dynamic;

public abstract class BaseDymIopClientBuilder implements DymIopClientsBuilder{
    private Class interfaceClazz;
    private DymKeySelector dymKeySelector;
    @Override
    public <T> Class<T> getInterfaceClazz() {
        return interfaceClazz;
    }

    @Override
    public <T> void setInterfaceClazz(Class<T> interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }

    public DymKeySelector getDymKeySelector() {
        return dymKeySelector;
    }

    public void setDymKeySelector(DymKeySelector dymKeySelector) {
        this.dymKeySelector = dymKeySelector;
    }
}
