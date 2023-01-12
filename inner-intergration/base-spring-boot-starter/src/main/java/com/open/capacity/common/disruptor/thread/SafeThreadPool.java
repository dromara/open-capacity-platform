package com.open.capacity.common.disruptor.thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class SafeThreadPool {

    private final Semaphore semaphore;

    private final ThreadPoolExecutor threadPoolExecutor;

    public SafeThreadPool(String name, int permits) {
        semaphore = new Semaphore(permits);
        
        threadPoolExecutor = new ThreadPoolExecutor(
                0,
                permits * 2,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                DaemonThreadFactory.getInstance(name)
        );
    }

    public void execute(Runnable task) {
       
        semaphore.acquireUninterruptibly();
        threadPoolExecutor.submit(() -> {
            try {
                task.run();  
            } finally {
                semaphore.release();
            }
        });
    }
}