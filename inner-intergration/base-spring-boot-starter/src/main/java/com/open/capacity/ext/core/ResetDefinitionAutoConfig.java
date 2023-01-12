/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResetDefinitionAutoConfig {
    @Bean
    public ResetDefinitionBeanFactoryPostProcessor resetDefinitionBeanFactoryPostProcessor(){
        return new ResetDefinitionBeanFactoryPostProcessor();
    }
}
