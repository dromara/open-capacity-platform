package com.open.capacity.gateway.context;

import org.apache.commons.chain.impl.ContextBase;
import org.springframework.web.server.ServerWebExchange;

import com.open.capacity.common.dto.ResponseEntity;

import lombok.Data;

/**
 * 请求角色： 既封装请求参数，又封装处理结果
 */

@SuppressWarnings("serial")
@Data
public class SecurityContext extends ContextBase {

	private ServerWebExchange exchange;
	// 处理结果
	private boolean result;
	
	private Integer code ;
	//处理明细
	private ResponseEntity entity; 

}
