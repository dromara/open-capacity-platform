package com.open.capacity;

import org.jboss.logging.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TestAysnc5 {

	 
	
	private Integer i = 0;

	@Async
	public String test8() {


		System.out.println("child =============="+MDC.get("traceId"));

		 
		return "test8";

	}

	 
}
