/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop;


import com.open.capacity.ext.core.PackageScanner;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PackageScanner
@Import(IopClientsRegistrar.class)
public @interface EnableIopClients {
    /**
     * 需要扫描iop client 的包名
     * @author: hillchen
     * @data: 2023/02/07 23:44
     * @param:
     * @return:
     */
    @AliasFor(annotation = PackageScanner.class)
    String[] basePackages() default {};
}
