package com.open.capacity.common.disruptor.thread;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.esotericsoftware.minlog.Log;

import lombok.extern.slf4j.Slf4j;

/**
 * 创建业务线程池的都可以从这里获取一个自定义线程名的线程工厂
 *
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class DaemonThreadFactory implements ThreadFactory {

    private static final ConcurrentHashMap<String, DaemonThreadFactory> BUFFER = new ConcurrentHashMap<>();

    private final String name;

    private final AtomicInteger counter = new AtomicInteger(0);

    private DaemonThreadFactory(String name) {
        this.name = name;
    }

    public static DaemonThreadFactory getInstance(String name) {
        Objects.requireNonNull(name, "name must have value");
        return BUFFER.computeIfAbsent(name, DaemonThreadFactory::new);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, name + "-" + counter.incrementAndGet());
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t,e) ->{
        	log.error(e.getMessage(),t);
        });
        return thread;
    }
}