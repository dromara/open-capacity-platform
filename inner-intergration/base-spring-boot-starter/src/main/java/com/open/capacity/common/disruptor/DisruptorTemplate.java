package com.open.capacity.common.disruptor;

import com.open.capacity.common.disruptor.callback.CallBack;
import com.open.capacity.common.disruptor.event.BaseEvent;

import lombok.Data;

/**
 * @author someday
 * 模仿ApplicationContext.pushEvent
 * 发布者设计模式
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
public class DisruptorTemplate {

	
	private BossEventBus bossEventBus ;
	
	public DisruptorTemplate(BossEventBus bossEventBus) {
		super();
		this.bossEventBus = bossEventBus;
	}
	
    public boolean publish(String channel, BaseEvent event, AsyncContext context) {
    	
        return  bossEventBus.publish(channel, event, context) ;
    	
    }

    public boolean publish(String channel, BaseEvent event, AsyncContext context, CallBack callback) {
    	 
        boolean success =   bossEventBus.publish(channel, event, context) ;
        if (!success) {
        	callback.onError(channel, event, context);
        }
        return success;
    	
    }
	
}
