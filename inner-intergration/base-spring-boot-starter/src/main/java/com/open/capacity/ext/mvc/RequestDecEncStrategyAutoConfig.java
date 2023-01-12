/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestDecEncStrategyAutoConfig {
    @Bean
    protected RequestDecEncStrategyRegister requestDecEncStrategyRegister(){
        return new RequestDecEncStrategyRegister();
    }
}
