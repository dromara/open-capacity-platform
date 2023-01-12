package com.open.capacity.common.disruptor.event;

import com.open.capacity.common.disruptor.AsyncContext;
import com.open.capacity.common.disruptor.callback.Action;

import lombok.Data;

/**
 * @author someday
 * boss event
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
public class BossEvent {

    private String channel;

    private BaseEvent event;

    private AsyncContext context;
    
    private Action action;

    public void clear() {
        channel = null;
        event = null;
        context = null;
    }
}