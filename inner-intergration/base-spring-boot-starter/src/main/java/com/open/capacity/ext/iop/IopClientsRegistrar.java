/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.iop;

import com.open.capacity.common.core.exception.HltRuntimeException;
import com.open.capacity.ext.core.PackageScanRegistrar;
import com.open.capacity.ext.iop.dynamic.DynamicIop;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import java.util.*;

public class IopClientsRegistrar extends PackageScanRegistrar {
    private Map<Class,IopClientsDefiner> definers = new HashMap<>();
    @Override
    protected Set<TypeFilter> initBeanFilter() {
        Set<TypeFilter> filters = new HashSet<>();
        AnnotationTypeFilter iopClientBeanFiler = new AnnotationTypeFilter(
                IopClient.class);
        AnnotationTypeFilter dynamicIopFiler = new AnnotationTypeFilter(
                DynamicIop.class);

        filters.add(iopClientBeanFiler);
        filters.add(dynamicIopFiler);
        return filters;
    }

    @Override
    protected void registerBeans(AnnotationMetadata metadata, BeanDefinitionRegistry registry, Environment environment) {
        MergedAnnotations mergedAnnotations = metadata.getAnnotations();
        if (mergedAnnotations.isPresent(IopClient.class)){
            registerBeans(metadata, registry, environment, false);
        }
        if (mergedAnnotations.isPresent(DynamicIop.class)){
            registerBeans(metadata, registry, environment, true);
        }
    }

    protected void registerBeans(AnnotationMetadata metadata, BeanDefinitionRegistry registry, Environment environment,boolean dymClient) {
        String className = metadata.getClassName();
        String shortClassName = ClassUtils.getShortName(className);
        String contextId;
        Map<String, Object> attributes;
        if (dymClient){
            contextId = "#dymIop." + shortClassName;
            attributes = metadata.getAnnotationAttributes(DynamicIop.class.getCanonicalName());
        }else {
            contextId ="#iop." + shortClassName;
            attributes = metadata.getAnnotationAttributes(IopClient.class.getCanonicalName());
        }
        registerConfig(registry, contextId,attributes.get("configuration"));
        AnnotationAttributes iopClient = AnnotationAttributes.fromMap(attributes);
        Class configAnnotation = iopClient.getClass("configAnnotation");
        Class clientBuilder = iopClient.getClass("clientBuilder");
        Class clientsDefiner = iopClient.getClass("definer");

        Assert.isTrue(metadata.isInterface(),String.format("@%s can only be specified on an interface",configAnnotation.getSimpleName()));
        Map<String, Object> configAttrs = metadata.getAnnotationAttributes(configAnnotation.getCanonicalName());

        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(clientBuilder);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        definition.addPropertyValue("interfaceClazz", className);

        if (dymClient){
            registerDymSelector(registry, iopClient, definition);
        }

        IopClientsDefiner iopClientsDefiner = initIopClientsDefiner(clientsDefiner);
        iopClientsDefiner.definePropertyValue(metadata, registry, environment,definition, configAttrs, clientBuilder);

        String alias = contextId;
        String beanAlias = iopClient.getString("beanAlias");
        if (StringUtils.hasText(beanAlias)) {
            alias = beanAlias;
        }

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[] { alias });
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private void registerDymSelector(BeanDefinitionRegistry registry, AnnotationAttributes iopClient, BeanDefinitionBuilder definition) {
        Class selectorClass = iopClient.getClass("selectorClass");
        String selectorBeanName =  selectorClass.getName();
        // 加载mock bean 定义信息并注册到spring容器中
        BeanDefinitionBuilder selectorDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(selectorClass);

        AbstractBeanDefinition abBeanDefinition = selectorDefinition.getBeanDefinition();
        BeanDefinitionHolder holder = new BeanDefinitionHolder(abBeanDefinition,selectorBeanName);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        definition.addPropertyReference("dymKeySelector",selectorBeanName);
    }

    private void registerSelector(){

    }


    private IopClientsDefiner initIopClientsDefiner(Class clazz){
        synchronized (clazz){
            if (definers.containsKey(clazz)){
                return definers.get(clazz);
            }
            Assert.isTrue(!clazz.isInterface(),"@%IopClient 的  definer 必须是com.open.capacity.ext.iop.IopClientsDefiner的具体实现类");
            Assert.isTrue(IopClientsDefiner.class.isAssignableFrom(clazz),"@%IopClient 的  definer 必须是com.open.capacity.ext.iop.IopClientsDefiner的具体实现类");
            try {
                IopClientsDefiner definer = (IopClientsDefiner)clazz.newInstance();
                definers.put(clazz,definer);
                return definer;
            }catch (Exception e){
                throw new HltRuntimeException("@%IopClient 的  definer 必须是com.open.capacity.ext.iop.IopClientsDefiner的具体实现类");
            }
        }
    }


    private void registerConfig( BeanDefinitionRegistry registry, String contextId,
                                 Object configuration){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(IopClientSpecification.class);
        builder.addConstructorArgValue(contextId);
        builder.addConstructorArgValue(configuration);
        registry.registerBeanDefinition(
                contextId + "." + IopClientSpecification.class.getSimpleName(),
                builder.getBeanDefinition());
    }

}
