package com.open.capacity.log.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public class AuditLogSinkConfig {
	public interface Sink {
		/**
		 * Input channel name.
		 */
		String INPUT = "input";

		/**
		 * @return input channel.
		 */
		@Input(Sink.INPUT)
		SubscribableChannel input();

	}

}
