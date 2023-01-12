package com.open.capacity.disruptor.event;

import com.open.capacity.common.disruptor.event.BaseEvent;

import lombok.Data;

@Data
public class OrderEvent extends BaseEvent {
	
	public String flag ;

}
