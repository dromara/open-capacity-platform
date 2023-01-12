package com.open.capacity.common.exception;

import java.sql.SQLException;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.log.annotation.ExceptionNoticeLog;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常通用处理 oauth服务端与客户端需要分开处理
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@ResponseBody
public class CustomerExceptionAdvice {
	  /**
     * IllegalArgumentException异常处理返回json
     * 返回状态码:400
     */
	@ExceptionNoticeLog
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity badRequestException(IllegalArgumentException e) {
        return defHandler("参数解析失败", e);
    }

	@ExceptionNoticeLog
    @ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity handleError(MissingServletRequestParameterException e) {
    	 return defHandler("参数解析失败", e);
	}

	@ExceptionNoticeLog
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity handleError(MethodArgumentTypeMismatchException e) {
    	return defHandler("参数解析失败", e);
	}

	@ExceptionNoticeLog
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity handleError(MethodArgumentNotValidException e) {
		return defHandler("参数解析失败", e);
	}

	@ExceptionNoticeLog
	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity handleError(BindException e) {
		return defHandler("参数解析失败", e);
	}

	@ExceptionNoticeLog
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity handleError(ConstraintViolationException e) {
		return defHandler("参数解析失败", e);
	}
	
	@ExceptionNoticeLog
	@ExceptionHandler(MissingPathVariableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity handleError(MissingPathVariableException e) {
		  return defHandler("参数解析失败", e);
	}
	
	@ExceptionNoticeLog
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity handleError(HttpMessageNotReadableException e) {
		  return defHandler("http请求参数转换异常", e);
	}
	
	@ExceptionNoticeLog
	@ExceptionHandler(HttpMessageNotWritableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity handleError(HttpMessageNotWritableException e) {
		  return defHandler("http响应参数转换异常", e);
	}
	
	@ExceptionNoticeLog
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity handleAccessDeniedException(MaxUploadSizeExceededException e) {
		return defHandler("文件上传过大异常", e);
	}
    
    /**
     * AccessDeniedException异常处理返回json
     * 返回状态码:403
     */
	@ExceptionNoticeLog
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity badMethodExpressException(AccessDeniedException e) {
        return defHandler("没有权限请求当前方法", e);
    }

    /**
     * 返回状态码:405
     */
	@ExceptionNoticeLog
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return defHandler("不支持当前请求方法", e);
    }

    /**
     * 返回状态码:415
     */
	@ExceptionNoticeLog
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResponseEntity handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return defHandler("不支持当前媒体类型", e);
    }

    /**
     * SQLException sql异常处理
     * 返回状态码:500
     */
	@ExceptionNoticeLog
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({SQLException.class})
    public ResponseEntity handleSQLException(SQLException e) {
        return defHandler("服务运行SQLException异常", e);
    }
    
    /**
     * RemoteCallException 服务调用异常处理
     * 返回状态码:500
     */
	@ExceptionNoticeLog
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RemoteCallException.class})
    public ResponseEntity handleRemoteCallException(RemoteCallException e) {
        return defHandler("服务调用异常", e);
    }
    
	 /**
     * AsyncRequestTimeoutException 异步处理超时
     * 返回状态码:500
     */
	@ExceptionNoticeLog
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({AsyncRequestTimeoutException.class})
    public ResponseEntity handleRemoteCallException(AsyncRequestTimeoutException e) {
        return defHandler("异步处理超时", e);
    }
	

    /**
     * BusinessException 业务异常处理
     * 返回状态码:500
     */
	@ExceptionNoticeLog
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handleException(BusinessException e) {
        return defHandler(e.getMessage(), e);
    }

    /**
     * IdempotencyException 幂等性异常
     * 返回状态码:200
     */
    @ExceptionNoticeLog
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IdempotencyException.class)
    public ResponseEntity handleException(IdempotencyException e) {
    	return defHandler(e.getMessage(), e);
    }

    
    protected ResponseEntity defHandler(String msg, Throwable e) {
        log.error(msg, e);
        return ResponseEntity.failed(msg);
    }
}
