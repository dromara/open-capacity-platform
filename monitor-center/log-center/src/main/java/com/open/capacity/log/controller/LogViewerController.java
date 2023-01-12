package com.open.capacity.log.controller;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.capacity.log.service.ILogViewTailerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@EnableScheduling
public class LogViewerController {

	private int info =0 ;
	
	@Autowired
	private ILogViewTailerService iLogViewTailerService;
	
	@MessageMapping("/openFile")
	public void filename_change(Map<String, String> map) throws Exception {
		String dir = map.get("dir");
		String fileName = map.get("fileName");
		iLogViewTailerService.changeLogFile(new File("F:\\code\\newocp\\monitor-center\\logs\\log-center", "log-center-info.log"));
	}

	
}
