/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.dynamic;

import com.open.capacity.ext.iop.AutoFieldClientDefiner;
import com.open.capacity.ext.iop.IopClientsDefiner;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicIop {
    Class<? extends DymIopClientsBuilder> clientBuilder();
    Class<? extends IopClientsDefiner> definer() default AutoFieldClientDefiner.class;
    Class<? extends Annotation> configAnnotation() default DynamicIop.class ;
    Class<? extends DymKeySelector> selectorClass() ;
    Class<?>[] configuration() default {};
    String beanAlias () default "";

}
