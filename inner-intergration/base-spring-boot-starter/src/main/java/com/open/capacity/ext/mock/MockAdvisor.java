/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class MockAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {
    private final Advice advice;
    private final Pointcut pointcut;

    public MockAdvisor(Advice advice, Set<Class> mockSourceClass) {
        this.advice = advice;
        this.pointcut = buildPointcut(mockSourceClass);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware)this.advice).setBeanFactory(beanFactory);
        }
    }

    private Pointcut buildPointcut(Set<Class> mockSourceClass) {
        return new ClassesPoint(mockSourceClass) ;
    }

    private static class ClassesPoint implements Pointcut {
        private Set<Class> classes;

        public ClassesPoint(Set<Class> classes) {
            this.classes = classes;
        }

        @Override
        public ClassFilter getClassFilter() {
            return new SetClassFilter(classes);
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return MethodMatcher.TRUE;
        }
    }


    private static class SetClassFilter implements ClassFilter {
        private Set<Class> classes;
        public SetClassFilter(Set<Class> classes) {
            this.classes = classes;
        }

        @Override
        public boolean matches(Class<?> candidate) {
            if (CollectionUtils.isEmpty(classes)){
                return false;
            }
            for (Class clazz: classes){
                if (clazz.isAssignableFrom(candidate)){
                    return true;
                }
            }
            return false;
        }
    }
}
