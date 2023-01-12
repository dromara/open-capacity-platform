package com.open.capacity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class PacketUplinkProducer {

	@Autowired
	private MessageSource messageSource;

	public void publish(PacketModel model) {
		Message<PacketModel> message = MessageBuilder.withPayload(model).setHeader("type", model.getType())
				.setHeader("x-delay", 1000 * 5 * 1)
				.build();
		messageSource.packetUplinkOutput().send(message);
	}

}