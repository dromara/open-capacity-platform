package com.open.capacity.common.disruptor.autoconfigure;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import com.open.capacity.common.disruptor.BossEventBus;
import com.open.capacity.common.disruptor.DisruptorTemplate;
import com.open.capacity.common.disruptor.WorkEventBusManager;
import com.open.capacity.common.disruptor.annocation.Channel;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.common.disruptor.thread.ExecutorService;
import com.open.capacity.common.disruptor.util.CglibUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
@EnableConfigurationProperties({BossConfig.class, WorkerConfig.class, ExecutorConfig.class})
public class AsyncAutoConfigure implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    private final BossConfig bossConfig;

    private final WorkerConfig workerConfig;

    private final ExecutorConfig executorConfig;

    private ApplicationContext applicationContext;

    public AsyncAutoConfigure(BossConfig bossConfig, WorkerConfig workerConfig, ExecutorConfig executorConfig) {
        this.bossConfig = bossConfig;
        this.workerConfig = workerConfig;
        this.executorConfig = executorConfig;
    }

    @Bean
    @Conditional(EventBusCondition.class)
    @ConditionalOnMissingBean
    public BossEventBus bossEventBus() {
        return new BossEventBus(bossConfig, workerConfig);
    }

    
    @Bean
    @ConditionalOnBean(BossEventBus.class)
    public DisruptorTemplate disruptorTemplate(BossEventBus bossEventBus) {
    	
    	return new DisruptorTemplate(bossEventBus);
    }
    
    @Bean
    @Conditional(ExecutorCondition.class)
    @ConditionalOnMissingBean
    public ExecutorService executorService() {
        return new ExecutorService(executorConfig);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, EventListener> eventListenerMap = applicationContext.getBeansOfType(EventListener.class);
        WorkEventBusManager workEventBusManager = WorkEventBusManager.getSingleton();
        
        Collection<EventListener> listeners = eventListenerMap.values();
        if (CollectionUtils.isNotEmpty(listeners)  ) {
        	   for (EventListener listener : listeners) {
        		   listener.setOrder(resolveOrder(listener));
        		   listener.setExecutorService(applicationContext.getBean(ExecutorService.class));
               }
        }
        for (EventListener eventListener : listeners.stream().sorted(Comparator.comparing(EventListener::getOrder)).collect(Collectors.toList()) ) {
            Class<?> realClazz = CglibUtils.filterCglibProxyClass(eventListener.getClass());
            Channel channel = realClazz.getAnnotation(Channel.class);
            if (channel != null && !channel.value().isEmpty()) {
                workEventBusManager.getWorkEventBus(channel.value()).register(eventListener);
            }
        }
    }
    
    
    private int resolveOrder(EventListener<?,?> eventListener) {
        if (!eventListener.getClass().isAnnotationPresent(Channel.class)) {
            return Channel.LOWEST_ORDER;
        } else {
            return eventListener.getClass().getAnnotation(Channel.class).order();
        }
    }

    

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}