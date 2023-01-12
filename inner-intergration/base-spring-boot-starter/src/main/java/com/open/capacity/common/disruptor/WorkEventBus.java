package com.open.capacity.common.disruptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.common.disruptor.thread.DaemonThreadFactory;

/**
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class WorkEventBus<E> {
    private final Disruptor<E> workRingBuffer;
    private final List<EventListener> eventListeners = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    @SuppressWarnings("unchecked")
    public WorkEventBus(int ringBufferSize,
                        int workerHandlerNum,
                        EventFactory<E> eventFactory,
                        Supplier<WorkHandler<E>> workHandlerSupplier) {

        workRingBuffer = new Disruptor<>(
                eventFactory,
                ringBufferSize, // 4096
                DaemonThreadFactory.getInstance("WorkEventBus") ,
                ProducerType.MULTI,
                new SleepingWaitStrategy()
        );

        WorkHandler<E>[] workHandlers = new WorkHandler[workerHandlerNum]; // 1
        for (int i = 0; i < workHandlers.length; i++) {
            workHandlers[i] = workHandlerSupplier.get();
        }

        workRingBuffer.handleEventsWithWorkerPool(workHandlers);
        workRingBuffer.start();
        
    }
    
    public boolean register(EventListener eventListener) {
        // 针对我们的监听器注册，写锁
        lock.writeLock().lock();
        try {
            if (eventListeners.contains(eventListener)) {
                return false;
            }
            eventListeners.add(eventListener);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<EventListener> getEventListeners(BaseEvent event) {
        lock.readLock().lock();
        try {
            return eventListeners.stream()
                    .filter(e -> e.accept(event))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean publish(EventTranslator<E> translator) {
        return workRingBuffer.getRingBuffer().tryPublishEvent(translator);
    }
}