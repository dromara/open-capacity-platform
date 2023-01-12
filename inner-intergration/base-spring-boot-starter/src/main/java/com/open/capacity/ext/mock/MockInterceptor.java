/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock;

import com.open.capacity.ext.mock.annotation.IgnoreMethod;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MockInterceptor implements MethodInterceptor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    public Map<Class,String> mockBeans ;
    public Map<Class,Class<? extends MockChecker>> mockCheckers ;

    public MockInterceptor(Map<Class, String> mockBeans, Map<Class, Class<? extends MockChecker>> mockCheckers) {
        this.mockBeans = mockBeans;
        this.mockCheckers = mockCheckers;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Map<Method,MockHandler> mockHandlerMap = new HashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        MockHandler mockHandler = getOrInitHandler(invocation);
        return mockHandler.invoke();
    }

    private synchronized MockHandler getOrInitHandler(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (mockHandlerMap.containsKey(method)){
            return mockHandlerMap.get(method);
        }
        MockHandler mockHandler = new MockHandler(invocation);
        mockHandler.setSourceMethod(method);
        Class proxyClass = invocation.getClass();
        initMockBean(mockHandler,method,proxyClass);
        initMockChecker(mockHandler);
        initMockMethod(mockHandler);

        mockHandlerMap.put(method,mockHandler);
        return mockHandler;
    }

    private void initMockChecker(MockHandler mockHandler) {
        if (Objects.nonNull(mockHandler.getSourceClass()) && mockCheckers.containsKey(mockHandler.getSourceClass())){
            Class<? extends MockChecker> checkerClass = mockCheckers.get(mockHandler.getSourceClass());
            mockHandler.setMockChecker(applicationContext.getBean(checkerClass.getName(),checkerClass));
        }
    }

    private void initMockMethod(MockHandler mockHandler) {
        Method mockMethod = getMockMethod(mockHandler.getSourceMethod(),mockHandler.getMockBean());
        if (Objects.nonNull(mockMethod) && !mockMethod.isAnnotationPresent(IgnoreMethod.class)){
            mockHandler.setMockMethod(mockMethod);
        }
    }

    private void initMockBean(MockHandler mockHandler,Method method,Class proxyClass){
        Class sourceClass = getSourceClass(method,proxyClass);
        if (Objects.nonNull(sourceClass)){
            mockHandler.setSourceClass(sourceClass);
            mockHandler.setMockBean(applicationContext.getBean(mockBeans.get(sourceClass)));
        }
    }

    private Class getSourceClass(Method method,Class targetClass){
        if (mockBeans.containsKey(targetClass)){
            return targetClass;
        }else if (mockBeans.containsKey(method.getDeclaringClass())){
            return method.getDeclaringClass();
        }else if (!targetClass.isInterface()){
            Class[] beanInterfaces =  targetClass.getInterfaces();
            if (Objects.nonNull(beanInterfaces)){
                for (Class interfaceClass : beanInterfaces){
                    if (mockBeans.containsKey(interfaceClass)){
                        return interfaceClass;
                    }
                }
            }
        }
        return null;
    }

    private Method getMockMethod(Method method, Object mockBean)  {
        try{
            if (Objects.isNull(mockBean)){
                return null;
            }
            Method mockMethod = mockBean.getClass().getMethod(method.getName(), method.getParameterTypes());
            return mockMethod;
        }catch (Exception e){
            return null;
        }
    }
}
