/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.dynamic;

import org.springframework.beans.factory.FactoryBean;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-16 10:36
 */
public interface DymIopClientsBuilder extends FactoryBean<DymIopClientFactory> {
    @Override
    default Class<?> getObjectType() {
        return DymIopClientFactory.class;
    }
    <T> Class<T> getInterfaceClazz();
    <T> void setInterfaceClazz(Class<T> interfaceClazz);

    DymKeySelector getDymKeySelector();

    void setDymKeySelector(DymKeySelector dymKeySelector);

    default String contextId(){
        return "#dymIop." + getInterfaceClazz().getSimpleName();
    }
}
