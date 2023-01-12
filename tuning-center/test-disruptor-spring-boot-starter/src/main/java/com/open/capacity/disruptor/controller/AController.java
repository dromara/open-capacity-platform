package com.open.capacity.disruptor.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.common.disruptor.DisruptorTemplate;
import com.open.capacity.disruptor.context.TAsyncContext;
import com.open.capacity.disruptor.event.OrderEvent;

@RestController
public class AController {

	 
	@Autowired
	private DisruptorTemplate disruptorTemplate;

	@GetMapping("/hello")
	public void hello(HttpServletRequest request, HttpServletResponse response) {

		OrderEvent event = new OrderEvent();
		event.flag = "A" + 100 + "   ";

		TAsyncContext context = new TAsyncContext();

		javax.servlet.AsyncContext asyncContext = request.startAsync();

		asyncContext.setTimeout(5000);

		context.setAsyncContext(asyncContext);

		disruptorTemplate.publish("step1", event, context);

	}

}
