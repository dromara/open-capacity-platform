package com.open.capacity.log.dto;


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
public class NinxLogDto{
    private String id;
    private String lon ;
    private String lat ;
    
}