package com.open.capacity;

import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAysnc4 {

	 @Autowired
	 private TestAysnc5 testAysnc5 ;
	
	private Integer i = 0;

	@GetMapping("/test8")
	public String test8() {


		MDC.put("traceId", "11111111");
		System.out.println("parent =============="+MDC.get("traceId"));
		 
		testAysnc5.test8();
		
		MDC.put("traceId", "11111112");
		System.out.println("parent =============="+MDC.get("traceId"));
		
		return "test8";

	}

	 
}
