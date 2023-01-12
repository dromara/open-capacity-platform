
/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop.dynamic;

import com.open.capacity.ext.iop.IopClientSpecification;
import com.open.capacity.ext.iop.IopClientsDefiner;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;

public class DymFeignIopClientsDefiner implements IopClientsDefiner {
    @Override
    public void definePropertyValue(AnnotationMetadata metadata, BeanDefinitionRegistry registry, Environment environment, BeanDefinitionBuilder definition, Map<String, Object> configAttrs, Class clientBuilder) {
        validate(configAttrs);
        String contextId = getContextId(configAttrs,environment);
        registerConfig(registry,contextId,configAttrs.get("configuration"));
        definition.addPropertyValue("decode404", configAttrs.get("decode404"));
        definition.addPropertyValue("fallback", configAttrs.get("fallback"));
        definition.addPropertyValue("fallbackFactory", configAttrs.get("fallbackFactory"));
    }

    private void validate(Map<String, Object> attributes) {
        AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
        validateFallback(annotation.getClass("fallback"));
        validateFallbackFactory(annotation.getClass("fallbackFactory"));
    }
    private String getContextId(Map<String, Object> attributes, Environment environment) {
        String contextId = (String) attributes.get("contextId");
        return resolve(contextId,environment);
    }

    private String resolve(String value,Environment environment) {
        if (StringUtils.hasText(value)) {
            return environment.resolvePlaceholders(value);
        }
        return value;
    }
    private void registerConfig( BeanDefinitionRegistry registry, Object name,
                                 Object configuration){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(IopClientSpecification.class);
        builder.addConstructorArgValue(name);
        builder.addConstructorArgValue(configuration);
        registry.registerBeanDefinition("dymFeign."+
                name + "." + IopClientSpecification.class.getSimpleName(),
                builder.getBeanDefinition());
    }



    static void validateFallback(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(),
                "Fallback class must implement the interface annotated by @FeignClient");
    }

    static void validateFallbackFactory(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(), "Fallback factory must produce instances "
                + "of fallback classes that implement the interface annotated by @FeignClient");
    }
}

