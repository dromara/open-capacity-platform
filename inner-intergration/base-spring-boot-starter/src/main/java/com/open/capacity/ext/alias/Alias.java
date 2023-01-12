/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.alias;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Aliases.class)
@Component
public @interface Alias {
    String[] value() default {};
    String groupName() default BeansAliasUtils.DEF_GROUP_NAME;
}
