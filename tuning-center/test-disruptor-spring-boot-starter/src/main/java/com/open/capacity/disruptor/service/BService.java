package com.open.capacity.disruptor.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import org.springframework.stereotype.Service;

import com.open.capacity.common.disruptor.annocation.Channel;
import com.open.capacity.common.disruptor.event.BaseEvent;
import com.open.capacity.common.disruptor.listener.EventListener;
import com.open.capacity.disruptor.context.TAsyncContext;
import com.open.capacity.disruptor.event.OrderEvent;

@Service
@Channel("step2")
public class BService extends EventListener<OrderEvent, TAsyncContext> {

	public boolean accept(BaseEvent event) {
		return true;
	}

	public void onEvent(OrderEvent event, TAsyncContext eventContext) {
		ServletResponse response = eventContext.getAsyncContext().getResponse();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		try (ServletOutputStream out = response.getOutputStream()) {
			String s = "step2";
			out.write(s.getBytes(StandardCharsets.UTF_8));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			eventContext.getAsyncContext().complete();
		}

		System.out.println("step2");
	}

}
