package com.open.capacity.log.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AController {

	@GetMapping("hello")
	public String hello() {
		log.info("msg:{}" , "hello");
		return "hello";
	}
}
