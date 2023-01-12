/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.dynamic;

import com.open.capacity.ext.iop.feign.IopFeignClientBuilder;
import com.open.capacity.ext.iop.feign.IopFeignTarget;

public class DymFeignIopClientFactory<K,T> implements DymIopClientFactory<K,T> {
    private Class<T> iopClientClass;
    private DymKeySelector<K, IopFeignTarget> feignTargetSelector;
    private IopFeignClientBuilder iopFeignClientBuilder;

    public DymFeignIopClientFactory(Class<T> iopClientClass, DymKeySelector<K, IopFeignTarget> feignTargetSelector, IopFeignClientBuilder iopFeignClientBuilder) {
        this.iopClientClass = iopClientClass;
        this.feignTargetSelector = feignTargetSelector;
        this.iopFeignClientBuilder = iopFeignClientBuilder;
    }

    public T createIopClient(K key){
        IopFeignTarget iopFeignTarget = feignTargetSelector.select(key);
        return (T)iopFeignClientBuilder.getTarget(iopFeignTarget,iopClientClass);
    }
}
