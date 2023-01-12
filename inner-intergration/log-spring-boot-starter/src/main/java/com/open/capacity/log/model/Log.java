package com.open.capacity.log.model;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.structlog4j.IToLog;
import com.google.common.collect.Maps;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Builder;
import lombok.Data;

/**
 * 业务日志
 * @author owen
 * @create 2020年04月02日
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform 
 */
@Data
@Builder
public class Log implements IToLog {
   
   
    private String appName ;
    private String serverIP ;
    private String serverPort;
    private String clientIp ;
    private String timestamp ;
    
    private String objectId ;
    private String objectType ;
    
    private String params ;
    
    private String body ;
    
    private Map<String, String> objectParam ;
    private String msg ;
    
    private String traceId ;
    private String spanId ;
    private String sql ;
    
    
    private String userId ;
    private String userName;
    private String clientId ;
    private String operation;
    
    @Override
    public Object[] toLog() {
        return new Object[] {
                "appName",     Optional.ofNullable(SpringUtil.getProperty("spring.application.name")).orElse(""),
                "serverIP",    Optional.ofNullable(SpringUtil.getProperty("spring.cloud.client.ip-address")).orElse(""),
                "serverPort",  Optional.ofNullable(SpringUtil.getProperty("server.port")).orElse(""),
                "clientIp" , Optional.ofNullable(clientIp).orElse("")    ,
                "traceId" , Optional.ofNullable(traceId).orElse("")    ,
                "spanId" , Optional.ofNullable(spanId).orElse("")    ,
                "timestamp" ,  Optional.ofNullable(timestamp).orElse("")  ,
                "objectId" , Optional.ofNullable(objectId).orElse("")    ,
                "params" , Optional.ofNullable(params).orElse("")    ,
                "body" , Optional.ofNullable(body).orElse("")    ,
                "objectType" , Optional.ofNullable(objectType).orElse("")    ,
                "objectParam" , this.objectParam()    ,
                "sql" ,Optional.ofNullable(sql).orElse("")    ,
                "msg" , Optional.ofNullable(msg).orElse("")
               
                
        };
    }
    
    
    private String objectParam() {
    	return Optional.ofNullable(objectParam).orElse(Maps.newHashMap()).keySet().stream().map( key-> String.format("%s=%s", key ,objectParam.get(key) )).sorted().collect(Collectors.joining( "&" , "","")) ;
    }
    
}