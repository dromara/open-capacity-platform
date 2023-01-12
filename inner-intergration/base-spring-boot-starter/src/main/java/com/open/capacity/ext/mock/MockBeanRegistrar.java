/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock;

import com.open.capacity.ext.core.AbstractRegistrar;
import com.open.capacity.ext.mock.annotation.EnableMock;
import com.open.capacity.ext.mock.annotation.MockBean;
import com.open.capacity.ext.mock.exception.MockBeanConfigErrorException;
import com.open.capacity.ext.mock.exception.MockBeanRepeatException;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MockBeanRegistrar extends AbstractRegistrar implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    public Map<Class,String> mockBeans = new ConcurrentHashMap<>();
    public Map<Class,Class<? extends MockChecker>> mockCheckers = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableMock.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    @Override
    protected Set<TypeFilter> initBeanFilter() {
        Set<TypeFilter> filters = new HashSet<>();
        AnnotationTypeFilter mockBeanFiler = new AnnotationTypeFilter(
                MockBean.class);
        filters.add(mockBeanFiler);
        return filters;
    }

    @Override
    protected void registerBeans(AnnotationMetadata metadata, BeanDefinitionRegistry registry, Environment environment) {
        // 获取bean定义的类名
        String beanClassName = metadata.getClassName();
        // 获取beanName
        String beanName = ClassUtils.getShortName(beanClassName);
        // 获取mock bean 类上MockService注解的属性值
        Map<String, Object> mockServiceAttributes = metadata
                .getAnnotationAttributes(
                        MockBean.class.getCanonicalName());
        // 从环境变量中获取mock bean开关属性并判断是否需要开启mock
        if (needMock(Objects.toString(mockServiceAttributes.get("mockKey")))){
            // 获取需要mock 的目标类
            Class mockTargetClazz = getMockTargetClass(mockServiceAttributes, beanClassName);
            String alias = "mock:" + beanName;
            // 将mock bean name 和targetClass 的映射关系注册到上下文中
            registerMockBean(mockTargetClazz,alias);
            // 获取二次mock校验类，并注册到上下文中
            Class<? extends MockChecker> mockClazz = (Class) mockServiceAttributes.get("checkerClazz");
            mockCheckers.put(mockTargetClazz,mockClazz);

            // 加载mock bean 定义信息并注册到spring容器中
            BeanDefinitionBuilder definition = BeanDefinitionBuilder
                    .genericBeanDefinition(beanClassName);

            AbstractBeanDefinition abBeanDefinition = definition.getBeanDefinition();
            BeanDefinitionHolder holder = new BeanDefinitionHolder(abBeanDefinition, beanClassName,
                    new String[] { alias });
            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        }
    }

    @Override
    protected void afterRegisterBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerMockCheckers(registry);
        registerMockInterceptor(registry);
    }
    private void registerMockInterceptor(BeanDefinitionRegistry registry){
        if (!CollectionUtils.isEmpty(mockBeans)){
            // 加载mock bean 定义信息并注册到spring容器中
            String mockInterceptorBeanName = MockInterceptor.class.getName();
            BeanDefinitionBuilder interceptorDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(MockInterceptor.class);
            interceptorDefinition.addConstructorArgValue(mockBeans);
            interceptorDefinition.addConstructorArgValue(mockCheckers);

            AbstractBeanDefinition interceptorBeanDefinition = interceptorDefinition.getBeanDefinition();
            BeanDefinitionHolder interceptorHolder = new BeanDefinitionHolder(interceptorBeanDefinition, mockInterceptorBeanName);
            BeanDefinitionReaderUtils.registerBeanDefinition(interceptorHolder, registry);

            // 加载mock bean 定义信息并注册到spring容器中
            BeanDefinitionBuilder definition = BeanDefinitionBuilder
                    .genericBeanDefinition(MockAdvisor.class);
            definition.addConstructorArgReference(mockInterceptorBeanName);
            definition.addConstructorArgValue(mockBeans.keySet());

            AbstractBeanDefinition abBeanDefinition = definition.getBeanDefinition();
            BeanDefinitionHolder holder = new BeanDefinitionHolder(abBeanDefinition, MockAdvisor.class.getName());
            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
        }
    }
    private void registerMockCheckers(BeanDefinitionRegistry registry){
        if (!CollectionUtils.isEmpty(mockCheckers)){
            for (Class mockClazz : mockCheckers.values()){
                // 加载mock bean 定义信息并注册到spring容器中
                BeanDefinitionBuilder definition = BeanDefinitionBuilder
                        .genericBeanDefinition(mockClazz);

                AbstractBeanDefinition abBeanDefinition = definition.getBeanDefinition();
                BeanDefinitionHolder holder = new BeanDefinitionHolder(abBeanDefinition, mockClazz.getName());
                BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
            }
        }
    }


    private void registerMockBean(Class mockTargetClazz,String beanName){
        if (mockBeans.containsKey(mockTargetClazz)){
            throw new MockBeanRepeatException(String.format("targetClass:%s已经注册了mock,不能重复注册",mockTargetClazz));
        }
        mockBeans.put(mockTargetClazz,beanName);
    }

    /***
     *  获取需要mock 的目标类,如果MockService注解的targetClazz没有指定，则获取该mockbean所实现的接口
     * @param mockServiceAttributes
     * @param beanClassName
     * @return
     */
    @SneakyThrows
    private Class getMockTargetClass(Map<String, Object> mockServiceAttributes, String beanClassName) {
        Class targetClazz = (Class) mockServiceAttributes.get("targetClazz");
        if (Objects.equals(void.class, targetClazz)  ){
            Class beanClass = Class.forName(beanClassName);
            Class[] beanInterfaces = beanClass.getInterfaces();
            if (Objects.nonNull(beanInterfaces) && beanInterfaces.length == 1){
                targetClazz = beanInterfaces[0];
            }else {
                throw new MockBeanConfigErrorException(String.format("mockBean:%s没有实现接口或实现多个接口，必须声明式定义代理接口类型",beanClassName));
            }
        }
        return targetClazz;
    }

    private boolean needMock(String mockKey){
        return  getEnvironment().containsProperty(mockKey) && getEnvironment().getProperty(mockKey,Boolean.class);
    }
}
