package com.open.capacity.monitor.config;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.notify.CompositeNotifier;
import de.codecentric.boot.admin.server.notify.Notifier;
import de.codecentric.boot.admin.server.notify.RemindingNotifier;
import de.codecentric.boot.admin.server.notify.filter.FilteringNotifier;

@Configuration
public class NotificationConfig {
	//实例存储
    private InstanceRepository instanceRepository;
    //警告策略
    private ObjectProvider<List<Notifier>> provider;
 
    public NotificationConfig(InstanceRepository instanceRepository, ObjectProvider<List<Notifier>> provider) {
        this.instanceRepository = instanceRepository;
        this.provider = provider;
    }
 
    @Bean
    public FilteringNotifier filteringNotifier() {
        CompositeNotifier compositeNotifier = new CompositeNotifier(this.provider.getIfAvailable(Collections::emptyList));
        return new FilteringNotifier(compositeNotifier, this.instanceRepository);
    }
 
    @Bean
    @Primary
    public RemindingNotifier remindingNotifier() {
        RemindingNotifier remindingNotifier = new RemindingNotifier(filteringNotifier(), this.instanceRepository);
        //配置每隔多久提示
        remindingNotifier.setReminderPeriod(Duration.ofMinutes(10));
        //配置每隔多久检查
        remindingNotifier.setCheckReminderInverval(Duration.ofSeconds(10));
        return remindingNotifier;
    }
}