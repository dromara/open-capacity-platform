/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.core;

import lombok.Data;
import org.springframework.cloud.context.named.NamedContextFactory;

@Data
public class ExtNamedSpecification implements  NamedContextFactory.Specification{
    private String name;

    private Class<?>[] configuration;

    public ExtNamedSpecification(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }
}
