package com.open.capacity.common.disruptor.thread;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.open.capacity.common.disruptor.autoconfigure.ExecutorConfig;

/**
 * @author someday
 * 不同topic对应的线程池
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class ExecutorService {
    private static final ConcurrentHashMap<String, SafeThreadPool> BUFFER = new ConcurrentHashMap<>();

    public ExecutorService(ExecutorConfig executorConfig) {
        for (ExecutorConfig.Config config : executorConfig.getExecutors()) {
            BUFFER.put(config.getThreadPool(), new SafeThreadPool(config.getThreadPool(), config.getThreadCount()));
        }
    }

    public void execute(String channel, Runnable task) {
        Optional.ofNullable(BUFFER.get(channel)).ifPresent(safeThreadPool -> safeThreadPool.execute(task));
    }
}