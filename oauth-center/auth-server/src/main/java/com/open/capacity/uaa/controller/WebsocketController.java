package com.open.capacity.uaa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 公告
 * @author owen
 *
 */
@RequestMapping("actutor")
@RestController
public class WebsocketController {
    @Autowired
    private SimpMessagingTemplate template;

    
    @GetMapping("/notice")
    public void sendToAll(String msg) {
        template.convertAndSend("/topic/notice", msg);
    }
}
