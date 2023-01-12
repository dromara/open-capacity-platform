/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock.annotation;

import com.open.capacity.ext.mock.MockBeanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MockBeanRegistrar.class)
public @interface EnableMock {
    /**
     * 需要扫描mock bean 的包名
     * @author: hillchen
     * @data: 2022/8/22 10:24
     * @param:
     * @return:
     */
    String[] basePackages() default {};
}
