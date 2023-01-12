package com.open.capacity.common.disruptor.listener;

import org.springframework.beans.factory.annotation.Autowired;

import com.open.capacity.common.disruptor.AsyncContext;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.thread.ExecutorService;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author someday
 * 模仿java.util.EventListener实现观察者模型
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 * 
 */
public abstract class EventListener<E extends BaseEvent ,T extends AsyncContext>  {

	@Getter
	@Setter
	private int order;

	@Getter
	@Setter
	protected ExecutorService executorService;
	 
	public abstract  boolean accept(BaseEvent event);
	

	public abstract  void onEvent(E event, T eventContext);
	

}