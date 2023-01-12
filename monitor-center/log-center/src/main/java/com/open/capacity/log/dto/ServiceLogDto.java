package com.open.capacity.log.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceLogDto {
	private String traceId ;
	private String spanId ;
	private String appName ;
	private String serverIp ;
	private String serverPort ;
	private String message ;
	
	private String logLevel ;
	private Date timestamp;
	private String classname ;
	private String threadName ;
	
	private Date startTime ;
	private Date endTime ;
}
