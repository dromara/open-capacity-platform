package com.open.capacity.common.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 异常事件生产者
 * @author owen
 *
 */
@Component
public class ExceptionPublisher {
	@Autowired
	private ApplicationEventPublisher publisher;

	/**
	 * 异常生产者
	 * @param exceptionEvent
	 */
	public void publishEvent(ExceptionEvent exceptionEvent) {
		publisher.publishEvent(exceptionEvent);
	}
}