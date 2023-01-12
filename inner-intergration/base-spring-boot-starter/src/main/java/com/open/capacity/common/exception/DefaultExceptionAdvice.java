package com.open.capacity.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.log.annotation.ExceptionNoticeLog;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常通用处理
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@ResponseBody
public class DefaultExceptionAdvice extends CustomerExceptionAdvice {

	/**
	 * 所有异常统一处理 返回状态码:500
	 */
	@ExceptionNoticeLog
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ResponseEntity handleException(Throwable e) {
		return defHandler("未知异常", e);
	}

}
