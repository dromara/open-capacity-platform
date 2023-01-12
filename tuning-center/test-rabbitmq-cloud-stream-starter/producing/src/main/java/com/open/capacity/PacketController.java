package com.open.capacity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PacketController {

	@Autowired
	private PacketUplinkProducer packetUplinkProducer;

	private Random random = new Random();
	private List<String> devEuis = new ArrayList<>(10);

	@PostConstruct
	private void initDevEuis() {
		devEuis.add("10001");
		devEuis.add("10002");
		devEuis.add("10003");
		devEuis.add("10004");
		devEuis.add("10005");
		devEuis.add("10006");
		devEuis.add("10007");
		devEuis.add("10008");
		devEuis.add("10009");
		devEuis.add("10010");
	}

	@GetMapping("/hello")
	public String hello() {

			String devEui = getDevEuis();
			String type = "short_link.add.link.mapping.routing.key";
			packetUplinkProducer.publish(new PacketModel(devEui, type));

		return "hello";
	}

	private String getDevEuis() {
		return devEuis.get(random.nextInt(10));
	}
}
