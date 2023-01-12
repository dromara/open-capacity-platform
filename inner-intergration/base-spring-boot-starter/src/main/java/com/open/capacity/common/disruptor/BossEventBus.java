package com.open.capacity.common.disruptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.minlog.Log;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.open.capacity.common.disruptor.autoconfigure.BossConfig;
import com.open.capacity.common.disruptor.autoconfigure.WorkerConfig;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.event.BossEvent;
import com.open.capacity.common.disruptor.handler.BossEventHandler;
import com.open.capacity.common.disruptor.thread.DaemonThreadFactory;
import com.open.capacity.common.disruptor.thread.DisruptorShutdownHook;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
@Slf4j
public class BossEventBus {

    private final Disruptor<BossEvent> bossRingBuffer;

    public BossEventBus(BossConfig bossConfig,
                        WorkerConfig workerConfig) {
 
        WorkEventBusManager workEventBusManager = WorkEventBusManager.getSingleton();
        for (WorkerConfig.Config config : workerConfig.getWorkers()) {
            workEventBusManager.register(config);
        }

        bossRingBuffer = new Disruptor<>(BossEvent::new, bossConfig.getRingbufferSize(),
        		DaemonThreadFactory.getInstance("BossEventBus"));
        BossEventHandler[] eventHandlers = new BossEventHandler[bossConfig.getEventHandlerNum()];
        for (int i = 0; i < eventHandlers.length; i++) {
            eventHandlers[i] = new BossEventHandler();
        }
        bossRingBuffer.handleEventsWithWorkerPool(eventHandlers);
        bossRingBuffer.start();
        
		Runtime.getRuntime().addShutdownHook(new DisruptorShutdownHook(bossRingBuffer));
        
    }

    
    public boolean publish(String channel, BaseEvent event, AsyncContext context) {
        
        EventTranslator<BossEvent> translator = (e, s) -> {
            e.setChannel(channel);
            e.setEvent(event);
            e.setContext(context);
            e.setAction(() -> log.error("消费通道{},消费数据{}发生异常！",channel,JSONObject.toJSONString(event)));
        };
        
        return  bossRingBuffer.getRingBuffer().tryPublishEvent(translator);
    }

}