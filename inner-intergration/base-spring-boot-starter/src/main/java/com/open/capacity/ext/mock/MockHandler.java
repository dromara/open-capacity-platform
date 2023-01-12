/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Objects;

class MockHandler {
    private Method sourceMethod;
    private Method mockMethod;
    private Object mockBean;
    private MockChecker mockChecker;
    private MethodInvocation invocation;
    private Class sourceClass;

    public Method getSourceMethod() {
        return sourceMethod;
    }

    public void setSourceMethod(Method sourceMethod) {
        this.sourceMethod = sourceMethod;
    }

    public MockHandler(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    public Method getMockMethod() {
        return mockMethod;
    }

    public void setMockMethod(Method mockMethod) {
        this.mockMethod = mockMethod;
    }

    public Object getMockBean() {
        return mockBean;
    }

    public void setMockBean(Object mockBean) {
        this.mockBean = mockBean;
    }

    public MockChecker getMockChecker() {
        return mockChecker;
    }

    public void setMockChecker(MockChecker mockChecker) {
        this.mockChecker = mockChecker;
    }

    public MethodInvocation getInvocation() {
        return invocation;
    }

    public void setInvocation(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(Class sourceClass) {
        this.sourceClass = sourceClass;
    }

    public Object invoke() throws Throwable {
        if (needMock()){
            return mockMethod.invoke(mockBean,invocation.getArguments());
        }
        return invocation.proceed();
    }

    private boolean needMock(){
        return Objects.nonNull(mockBean) && Objects.nonNull(mockMethod) &&
                (Objects.isNull(mockChecker) ||
                        mockChecker.needMock(this)
                        );
    }

}
