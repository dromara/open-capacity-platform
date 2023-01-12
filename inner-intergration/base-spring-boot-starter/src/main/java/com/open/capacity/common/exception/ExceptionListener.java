package com.open.capacity.common.exception;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Maps;
import com.open.capacity.common.properties.ExceptionNoticeProperties;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 异常消费者
 * @author owen
 *
 */
@Slf4j
@Component
public class ExceptionListener {

	public static final String MSG_TEMPLATE = "["
			+ "    {"
			+ "        \"labels\": {"
			+ "            \"alertname\": \"温馨提示：服务名称:【%s】接口名称:【%s】存在异常！请尽快处理！\""
			+ "        },"
			+ "        \"annotations\": {"
			+ " 		   \"timestamp\": \"%s\","
			+ "            \"traceId\": \"%s\","
			+ "            \"message\": %s ,"
			+ "            \"stackTrace\": \"%s\""
			+ "        }"
			+ "    }"
			+ "]" ;
	
	@Resource
	private RestTemplate restTemplate ;
	
	@Resource
	private ExceptionNoticeProperties exceptionNoticeProperties ;
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	
	/**
	 * 消费异常事件
	 * @param event
	 */
	@EventListener({ExceptionEvent.class})
	public void comsumer(ExceptionEvent event) {
		CompletableFuture.runAsync (() -> {
			try {
				this.sendMsg(exceptionNoticeProperties.getAlertUrl(), HttpMethod.POST,
						String.format(MSG_TEMPLATE, event.getApplication(), event.getApiPath(), DateUtil.now(),
								event.getTraceId(), event.getMessage(), event.getStackTrace()),
						Maps.newHashMap());
			} finally {
			}
		},taskExecutor);
	}
 
	private void sendMsg(String path, HttpMethod httpMethod, String msg , Map<String, String> formData) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(msg,headers);
		String userInfo;
		try {
			ResponseEntity<String> forEntity = restTemplate.exchange(path, httpMethod, httpEntity, String.class, formData);
			userInfo = forEntity.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}