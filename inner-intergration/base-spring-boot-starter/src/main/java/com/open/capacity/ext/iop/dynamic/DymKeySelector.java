/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.dynamic;

public interface DymKeySelector<K,V> {
    V select(K key);
}
