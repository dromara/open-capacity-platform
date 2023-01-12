package com.open.capacity.jpush;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.open.capacity.jpush.EnableJPush;
import com.open.capacity.jpush.JPushTemplate;
import com.open.capacity.jpush.PushObject;

@EnableJPush
@SpringBootApplication
public class Application {

	@Autowired
	JPushTemplate template;

	@PostConstruct
	public void test() throws IOException {
		PushObject pushObject = new PushObject();
		System.out.println(template.sendPush(pushObject));
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}
