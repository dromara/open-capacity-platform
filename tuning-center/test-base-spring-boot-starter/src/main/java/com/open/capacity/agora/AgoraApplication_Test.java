package com.open.capacity.agora;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.agora.AgoraTemplate;
import com.open.capacity.common.agora.interactive.resp.AcquireResourceResponse;

@SpringBootApplication
public class AgoraApplication_Test {
	
	@Autowired
	private AgoraTemplate template;
	@Autowired
	private ObjectMapper objectMapper;
	
	@PostConstruct
	public void testAcquireId() throws Exception {

		AcquireResourceResponse response =  template.opsForCloudRecording().acquireId("10000", "121212");
		System.out.println(objectMapper.writeValueAsString(response));

	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(AgoraApplication_Test.class, args);
	}

}
