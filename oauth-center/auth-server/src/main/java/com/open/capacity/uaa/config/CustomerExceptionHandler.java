package com.open.capacity.uaa.config;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.open.capacity.common.exception.CustomerExceptionAdvice;

/**
 * 异常通用处理 服务于oauth 服务端与客户端
 * @author someday
 * @date 2018/12/22
 */
@RestControllerAdvice
public class CustomerExceptionHandler extends CustomerExceptionAdvice {
	

}
