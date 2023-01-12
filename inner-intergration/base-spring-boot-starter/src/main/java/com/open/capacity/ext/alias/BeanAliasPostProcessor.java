/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.alias;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.AliasRegistry;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
@Configuration
public class BeanAliasPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClazz = AopUtils.getTargetClass(bean);
        Aliases aliases = AnnotationUtils.findAnnotation(beanClazz, Aliases.class);
        Set<String> aliasNames = new HashSet<>();
        if (Objects.nonNull(aliases) && aliases.value().length > 0){
            for (Alias alias:aliases.value()){
                if (alias.value().length > 0){
                    Arrays.stream(alias.value()).forEach(a -> aliasNames.add(BeansAliasUtils.createAliasBeanName(alias.groupName(),a)));
                }
            }
        }else {
            Alias alias = AnnotationUtils.findAnnotation(beanClazz, Alias.class);
            if (Objects.nonNull(alias) && alias.value().length > 0){
                Arrays.stream(alias.value()).forEach(a -> aliasNames.add(BeansAliasUtils.createAliasBeanName(alias.groupName(),a)));
            }
        }
        if (!aliasNames.isEmpty()){
            for (String aliasName:aliasNames){
                ((AliasRegistry) applicationContext).registerAlias(beanName,aliasName);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
