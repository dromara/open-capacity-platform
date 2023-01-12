/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.core;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public interface BeanDefinitionPostProcessor {
    boolean supportResetDefinition(BeanDefinition beanDefinition);
    void resetDefinition(String beanName, BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory);
}
