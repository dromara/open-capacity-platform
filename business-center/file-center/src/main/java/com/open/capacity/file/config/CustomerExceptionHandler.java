package com.open.capacity.file.config;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.DefaultExceptionAdvice;

/**
 * 异常通用处理 服务于oauth 服务端与客户端
 * 
 * @author someday
 * @date 2018/12/22
 */
@RestControllerAdvice
public class CustomerExceptionHandler extends DefaultExceptionAdvice {

	@ExceptionHandler(CannotGetJdbcConnectionException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity handleException(CannotGetJdbcConnectionException e) {
		return defHandler("数据库连接异常", e);
	}

	@ExceptionHandler(MyBatisSystemException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity myBatisSystemExceptionHandler(MyBatisSystemException e) {
		return defHandler("SQL执行异常，请检查SQL是否正确!", e);
	}

}
