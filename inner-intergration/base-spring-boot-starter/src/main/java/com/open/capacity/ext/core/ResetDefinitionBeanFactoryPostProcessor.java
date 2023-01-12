/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class ResetDefinitionBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private List<BeanDefinitionPostProcessor> beanDefinitionPostProcessors = new ArrayList<>();
    private volatile boolean initialized = false;
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        init(beanFactory);
        if (CollectionUtils.isEmpty(beanDefinitionPostProcessors)){
            return;
        }
        Iterator<String> beanNamesIterator = beanFactory.getBeanNamesIterator();
        while (beanNamesIterator.hasNext()){
            String beanName = beanNamesIterator.next();
            BeanDefinition beanDefinition = getBeanDefinition(beanFactory,beanName);
            if (Objects.isNull(beanDefinition)){
                continue;
            }
            for (BeanDefinitionPostProcessor beanDefinitionPostProcessor: beanDefinitionPostProcessors){
                if (beanDefinitionPostProcessor.supportResetDefinition(beanDefinition)){
                    beanDefinitionPostProcessor.resetDefinition(beanName,beanDefinition,beanFactory);
                }
            }
        }
    }

    private BeanDefinition getBeanDefinition(ConfigurableListableBeanFactory beanFactory,String beanName){
        try {
            return beanFactory.getBeanDefinition(beanName);
        }catch (Exception e){
            return null;
        }
    }

    private void init(ConfigurableListableBeanFactory beanFactory){
        if (initialized){
            return;
        }
        initialized = true;

        String[] postProcessorNames =
                beanFactory.getBeanNamesForType(BeanDefinitionPostProcessor.class, true, false);
        if (postProcessorNames != null && postProcessorNames.length > 0){
            List<BeanDefinitionPostProcessor> priorityPostProcessors = new ArrayList<>();
            List<BeanDefinitionPostProcessor> orderedPostProcessors = new ArrayList<>();
            List<BeanDefinitionPostProcessor> postProcessors = new ArrayList<>();
            for (String ppName : postProcessorNames) {
                BeanDefinitionPostProcessor beanDefinitionPostProcessor = beanFactory.getBean(ppName, BeanDefinitionPostProcessor.class);
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    priorityPostProcessors.add(beanDefinitionPostProcessor);
                }else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                    orderedPostProcessors.add(beanDefinitionPostProcessor);
                }else {
                    postProcessors.add(beanDefinitionPostProcessor);
                }
            }
            if (!CollectionUtils.isEmpty(priorityPostProcessors)){
                sortPostProcessors(priorityPostProcessors,beanFactory);
                this.beanDefinitionPostProcessors.addAll(priorityPostProcessors);
            }

            if (!CollectionUtils.isEmpty(orderedPostProcessors)){
                sortPostProcessors(orderedPostProcessors,beanFactory);
                this.beanDefinitionPostProcessors.addAll(orderedPostProcessors);
            }

            if (!CollectionUtils.isEmpty(postProcessors)){
                sortPostProcessors(postProcessors,beanFactory);
                this.beanDefinitionPostProcessors.addAll(postProcessors);
            }
        }

    }

    private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        // Nothing to sort?
        if (postProcessors.size() <= 1) {
            return;
        }
        Comparator<Object> comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
        }
        if (comparatorToUse == null) {
            comparatorToUse = OrderComparator.INSTANCE;
        }
        postProcessors.sort(comparatorToUse);
    }

}
