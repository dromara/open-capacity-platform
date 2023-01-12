/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.obj.collect;

import java.util.List;

public interface TreeNode<K,N> {
    K getParentKey();
    K getKey();
    void setChildren(List<N> children);
}
