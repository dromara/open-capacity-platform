package com.open.capacity.log.dmo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceLogDmo {
	private String traceId ;
	private String appName ;
	private String serverIp ;
	private String message ;
	private Date startTime ;
	private Date endTime ;
	private Integer from ;
	private Integer size ;
}
