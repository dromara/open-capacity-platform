/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.dynamic;

import com.open.capacity.ext.iop.feign.IopFeignTarget;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DynamicIop(clientBuilder = DymFeignIopClientsBuilder.class,configAnnotation = DymFeignIopClient.class,definer = DymFeignIopClientsDefiner.class,selectorClass=DymKeySelector.class)
public @interface DymFeignIopClient {
    @AliasFor(annotation = DynamicIop.class)
    Class<? extends DymKeySelector<?,? extends IopFeignTarget>>selectorClass();
    @AliasFor(annotation = DynamicIop.class)
    Class<?>[] configuration() default {};
    boolean decode404() default false;
    Class<?> fallback() default void.class;
    Class<?> fallbackFactory() default void.class;
}
