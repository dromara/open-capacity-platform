package com.open.capacity.log.dmo;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * nginx日志对象,映射es中的索引kafka_nginxlogs-*
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NinxLogDmo{
    private String id;
    private String lon ;
    private String lat ;
    
    private Integer from ;
	private Integer size ;
	private Date startTime ;
	private Date endTime ;
    
}