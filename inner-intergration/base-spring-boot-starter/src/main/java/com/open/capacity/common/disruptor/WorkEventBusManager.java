package com.open.capacity.common.disruptor;

import java.util.concurrent.ConcurrentHashMap;

import com.open.capacity.common.disruptor.autoconfigure.WorkerConfig;
import com.open.capacity.common.disruptor.event.WorkEvent;
import com.open.capacity.common.disruptor.handler.WorkEventHandler;

/**
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class WorkEventBusManager {

    private static final WorkEventBusManager SINGLETON = new WorkEventBusManager();

    private final ConcurrentHashMap<String, WorkEventBus> BUFFER = new ConcurrentHashMap<>();

    private WorkEventBusManager() {}

    public static WorkEventBusManager getSingleton() {
        return SINGLETON;
    }
 
    public void register(WorkerConfig.Config config) {
        BUFFER.computeIfAbsent(config.getChannel(), k -> new WorkEventBus<>(
                config.getRingbufferSize(),  
                config.getEventHandlerNum(),  
                WorkEvent::new,
                WorkEventHandler::new)
        );
    }

    public WorkEventBus getWorkEventBus(String channel) {
        return BUFFER.get(channel);
    }

}