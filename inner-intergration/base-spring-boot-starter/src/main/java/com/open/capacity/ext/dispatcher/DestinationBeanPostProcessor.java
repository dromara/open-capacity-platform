/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.dispatcher;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class DestinationBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClazz = AopUtils.getTargetClass(bean);
        if (Objects.nonNull(AnnotationUtils.findAnnotation(beanClazz,Destination.class))){
            initDeMethodHandlers(bean,beanClazz);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void initDeMethodHandlers(Object bean,Class beanClazz){
        Map<Method, DsMethod> dsMethods = MethodIntrospector.selectMethods(beanClazz, new MethodIntrospector.MetadataLookup<DsMethod>() {
            @Override
            public DsMethod inspect(Method method) {
                return AnnotationUtils.findAnnotation(method, DsMethod.class);
            }
        });
        if (!CollectionUtils.isEmpty(dsMethods)){
            dsMethods.forEach((k,v) -> {
                DispatchRoute dispatchRoute = new DispatchRoute(v.group(), v.bizType());
                DsMethodHandler handler = new DsMethodHandler(bean,k.getGenericParameterTypes(),k,dispatchRoute.getGroupName(),dispatchRoute.getBizName());
                DestinationFactory.registerStrategy(dispatchRoute,handler);
            });

        }
    }
}
