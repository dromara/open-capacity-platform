/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public interface IopClientsDefiner {
    void definePropertyValue(AnnotationMetadata metadata, BeanDefinitionRegistry registry, Environment environment, BeanDefinitionBuilder definition, Map<String, Object> configAttrs , Class clientBuilder);
}
