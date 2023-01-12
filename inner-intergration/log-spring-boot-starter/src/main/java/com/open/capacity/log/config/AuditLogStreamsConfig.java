package com.open.capacity.log.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public class AuditLogStreamsConfig {

	public interface Source {
		String OUTPUT = "output";

		@Output(Source.OUTPUT)
		MessageChannel output();

	}

}