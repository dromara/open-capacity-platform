/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mvc;

import com.open.capacity.ext.mvc.decrypt.RequestDec;
import com.open.capacity.ext.mvc.decrypt.RequestDecStrategy;
import com.open.capacity.ext.mvc.encryption.ResponseEnc;
import com.open.capacity.ext.mvc.encryption.ResponseEncStrategy;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * 请求参数解析处理器
 */
public class RequestDecEncStrategyRegister implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = AopUtils.getTargetClass(bean);
        if (Objects.nonNull(AnnotationUtils.findAnnotation(beanClass, MvcDecEncHandler.class)) &&
                Objects.isNull(AnnotationUtils.findAnnotation(beanClass, Controller.class))){
            initDesStrategy(bean,beanClass);
            initEncStrategy(bean,beanClass);
        }
        return bean;
    }

    private void initDesStrategy(Object bean,Class beanClass) {
        Map<Method, RequestDec> desStrategyMethods = MethodIntrospector.selectMethods(beanClass, new MethodIntrospector.MetadataLookup<RequestDec>() {
            @Override
            public RequestDec inspect(Method method) {
                return AnnotationUtils.findAnnotation(method, RequestDec.class);
            }
        });
        if (!CollectionUtils.isEmpty(desStrategyMethods)){
            desStrategyMethods.forEach((method, requestDec) -> {
                HandlerMethod handlerMethod = new HandlerMethod(bean,method);
                RequestDecStrategyFactory.registerStrategy(requestDec.decType(),new RequestDecStrategy(bean,method,handlerMethod.getMethodParameters()));
            });
        }
    }

    private void initEncStrategy(Object bean,Class beanClass) {
        Map<Method, ResponseEnc> encStrategyMethods = MethodIntrospector.selectMethods(beanClass, new MethodIntrospector.MetadataLookup<ResponseEnc>() {
            @Override
            public ResponseEnc inspect(Method method) {
                return AnnotationUtils.findAnnotation(method, ResponseEnc.class);
            }
        });
        if (!CollectionUtils.isEmpty(encStrategyMethods)){
            encStrategyMethods.forEach((method, responseEnc) -> ResponseEncStrategyFactory.registerStrategy(responseEnc.encType(),new ResponseEncStrategy(bean,method)));
        }
    }
}
