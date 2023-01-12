package com.open.capacity.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.open.capacity.domain.Foo;

@RestController
public class UserController {

	@GetMapping("/user")
	public Authentication currentUser() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}

	@GetMapping("/users")
	public Authentication user(Authentication user) {
		return user;
	}

	@RequestMapping("/dashboard/message")
	public Map<String, Object> dashboard() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		return Collections.<String, Object>singletonMap("message", auth.getPrincipal());
	}

	@RequestMapping("/dashboard/user")
	public Principal user(Principal user) {
		return user;
	}

	@GetMapping("/content")
	public Map getFoos() {
		List<Foo> list = Lists.newArrayList();
		Map map = Maps.newHashMap();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		list.add(new Foo("Client-1", "This is first client"));
		map.put("authentication", authentication);
		map.put("foos", list);
		
		return map;
	}

}
