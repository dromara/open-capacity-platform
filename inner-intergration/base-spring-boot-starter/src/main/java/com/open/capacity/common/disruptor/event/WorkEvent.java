package com.open.capacity.common.disruptor.event;

import java.util.List;

import com.open.capacity.common.disruptor.AsyncContext;
import com.open.capacity.common.disruptor.listener.EventListener;

import lombok.Data;

/**
 * @author someday
 * work event
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Data
public class WorkEvent {

	private BaseEvent event;

	private AsyncContext context;

	/*
	 * 责任链处理链路
	 */
	private List<EventListener> listeners;

	public void clear() {
		event = null;
		context = null;
		listeners = null;
	}
}