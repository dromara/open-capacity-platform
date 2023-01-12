package com.open.capacity.common.disruptor.callback;

import com.open.capacity.common.disruptor.AsyncContext;
import com.open.capacity.common.disruptor.event.BaseEvent;

/**
 * @author someday
 * 异常回调
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public interface CallBack {
	
	public void onError(String channel, BaseEvent event, AsyncContext context);

}
