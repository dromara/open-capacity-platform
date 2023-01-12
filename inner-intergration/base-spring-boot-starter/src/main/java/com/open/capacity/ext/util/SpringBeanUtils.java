/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeanUtils implements ApplicationContextAware {
    private static ApplicationContext CONTEXT;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CONTEXT = applicationContext;
    }

    public static <T> T getBean(String beanName,Class<T> clazz){
        return CONTEXT.getBean(beanName,clazz);
    }
    public static <T> T getBean(String beanName){
        return (T)CONTEXT.getBean(beanName);
    }
    public static <T> T getBean(Class<T> clazz){
        return (T)CONTEXT.getBean(clazz);
    }
}
