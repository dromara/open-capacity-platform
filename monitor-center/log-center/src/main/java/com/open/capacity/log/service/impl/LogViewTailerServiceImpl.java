package com.open.capacity.log.service.impl;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.open.capacity.common.disruptor.AsyncContext;
import com.open.capacity.common.disruptor.DisruptorTemplate;
import com.open.capacity.common.disruptor.annocation.Channel;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.log.event.LogEvent;
import com.open.capacity.log.service.ILogViewTailerService;

@Service
@Channel("log")
public class LogViewTailerServiceImpl extends EventListener<LogEvent, AsyncContext> implements ILogViewTailerService {

	Tailer tailer;

	@Autowired
	private DisruptorTemplate disruptorTemplate;

	@Lazy
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Override
	public void changeLogFile(File file) {

		CompletableFuture.runAsync(new Runnable() {

			@Override
			public void run() {

				if (tailer != null)
					tailer.stop();
				TailerListener listener = new TailerListenerAdapter() {
					@Override
					public void handle(String line) {
						disruptorTemplate.publish("log", LogEvent.builder().lines(line).build(), null);
					}
				};
				tailer = new Tailer(file, listener, 100, true);
				tailer.run();

			}
		});

	}

	@Override
	public boolean accept(BaseEvent event) {
		return true;
	}

	@Override
	public void onEvent(LogEvent event, AsyncContext eventContext) {
		messagingTemplate.convertAndSend("/topic/pullFileLogger", event.getLines());
	}

}
