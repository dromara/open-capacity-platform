package com.open.capacity.common.disruptor.handler;

import java.util.List;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.WorkHandler;
import com.open.capacity.common.disruptor.WorkEventBus;
import com.open.capacity.common.disruptor.WorkEventBusManager;
import com.open.capacity.common.disruptor.event.BossEvent;
import com.open.capacity.common.disruptor.event.WorkEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
 

/**
 * @author someday
 * boss处理器
 * 多消费者不重复消费问题
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class BossEventHandler implements WorkHandler<BossEvent> {

    @Override
    public void onEvent(BossEvent event) throws Exception {
        try {
            dispatchBossEvent(event);
        } finally {
            event.clear();
        }
    }
 
    @SuppressWarnings("unchecked")
    private void dispatchBossEvent(BossEvent event) {
        WorkEventBus workEventBus = WorkEventBusManager.getSingleton().getWorkEventBus(event.getChannel());
        List<EventListener> eventListeners = workEventBus.getEventListeners(event.getEvent());
        EventTranslator<WorkEvent> translator = (e, s) -> {
            e.setEvent(event.getEvent());    
            e.setContext(event.getContext());   
            e.setListeners(eventListeners);  
        };
        boolean success =  workEventBus.publish(translator);
        if (!success) {
        	event.getAction().execute();
        }
    }
}