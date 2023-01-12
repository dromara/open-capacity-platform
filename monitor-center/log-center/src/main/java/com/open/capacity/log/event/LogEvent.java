package com.open.capacity.log.event;

import com.open.capacity.common.disruptor.event.BaseEvent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogEvent   extends BaseEvent {
	
	public String lines;

}
