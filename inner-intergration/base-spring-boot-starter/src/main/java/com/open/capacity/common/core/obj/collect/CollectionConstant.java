/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.obj.collect;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionConstant {
    public static Function PROTOTYPE = e -> e;
    public static Predicate NO_EXCLUDER = e -> false;
    public static BiPredicate NO_BI_EXCLUDER = (e,v) -> false;

    private CollectionConstant(){}
}
