package com.open.capacity.gateway.error;

import java.net.SocketTimeoutException;
import java.sql.SQLException;

import javax.validation.ConstraintViolationException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;
import org.zalando.problem.spring.webflux.advice.security.SecurityAdviceTrait;

import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.exception.IdempotencyException;

import reactor.core.publisher.Mono;

/**
 * 异常通用处理
 * @author someday
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class CustomerExceptionHandler implements SecurityAdviceTrait, ProblemHandling {

	@Override
    public boolean isCausalChainsEnabled() {
        return false;
    }

	@ExceptionHandler({ IllegalArgumentException.class })
	public Mono<ResponseEntity<Problem>> badRequestException(final IllegalArgumentException ex,
			final ServerWebExchange request) {
		return this.handleAllException(Status.BAD_REQUEST, ex, request);
	}
	@ExceptionHandler({ NotFoundException.class })
	public Mono<ResponseEntity<Problem>> notFoundExceptionException(final NotFoundException ex,
			final ServerWebExchange request) {
		return this.handleAllException(Status.NOT_FOUND, ex, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleBindingResult(WebExchangeBindException ex, ServerWebExchange request) {
		return this.handleAllException(Status.BAD_REQUEST, ex, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleMediaTypeNotSupportedException(UnsupportedMediaTypeStatusException ex,
			ServerWebExchange request) {
		return this.handleAllException(Status.BAD_REQUEST, ex, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleMediaTypeNotAcceptable(NotAcceptableStatusException ex,
			ServerWebExchange request) {
		return this.handleAllException(Status.BAD_REQUEST, ex, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleRequestMethodNotSupportedException(MethodNotAllowedException ex,
			ServerWebExchange request) {
		return this.handleAllException(Status.BAD_REQUEST, ex, request);
	}
	
	@Override
	public Mono<ResponseEntity<Problem>> handleResponseStatusException(ResponseStatusException ex,
			ServerWebExchange request) {
		return this.handleAllException(Status.BAD_REQUEST, ex, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleConstraintViolation(ConstraintViolationException ex,
			ServerWebExchange request) {
		return this.handleAllException(Status.BAD_REQUEST, ex, request);
	}

	/**
	 * SQLException sql异常处理 返回状态码:500
	 */
	@ExceptionHandler({ SQLException.class })
	public Mono<ResponseEntity<Problem>> badRequestException(final SQLException ex, final ServerWebExchange request) {
		return this.handleAllException(Status.INTERNAL_SERVER_ERROR, ex, request);
	}

	/**
	 * BusinessException 业务异常处理 返回状态码:500
	 */
	@ExceptionHandler(BusinessException.class)
	public Mono<ResponseEntity<Problem>> badRequestException(final BusinessException ex,
			final ServerWebExchange request) {
		return this.handleAllException(Status.INTERNAL_SERVER_ERROR, ex, request);
	}

	/**
	 * IdempotencyException 幂等性异常
	 */
	@ExceptionHandler(IdempotencyException.class)
	public Mono<ResponseEntity<Problem>> badRequestException(final IdempotencyException ex,
			final ServerWebExchange request) {
		return this.handleAllException(Status.INTERNAL_SERVER_ERROR, ex, request);
	}

	

	@Override
	public Mono<ResponseEntity<Problem>> handleUnsupportedOperation(UnsupportedOperationException ex,
			ServerWebExchange request) {
		return this.handleAllException(Status.NOT_IMPLEMENTED, ex, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleAuthentication(AuthenticationException ex, ServerWebExchange request) {
		return this.handleAllException(Status.UNAUTHORIZED, ex, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleSocketTimeout(SocketTimeoutException ex, ServerWebExchange request) {
		return this.handleAllException(Status.GATEWAY_TIMEOUT, ex, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleAccessDenied(AccessDeniedException ex, ServerWebExchange request) {
		return this.handleAllException(Status.FORBIDDEN, ex, request);
	}

	@Override
	public Mono<ResponseEntity<Problem>> handleProblem(ThrowableProblem throwable, ServerWebExchange request) {
		return this.handleAllException(Status.INTERNAL_SERVER_ERROR, throwable, request);
	}

	private Mono<ResponseEntity<Problem>> handleAllException(Status status, Throwable ex, ServerWebExchange request) {
		CustomerThrowableProblem toProblem = new CustomerThrowableProblem( request.getRequest().getURI() , ex.getMessage(), status);
		toProblem.setMsg(ex.getMessage());
		if(!isCausalChainsEnabled()) {
			toProblem.setStackTrace( new StackTraceElement[] {
				    new StackTraceElement("", "", "", 0),
				});
		}
		return create(ex, toProblem, request);
	};
}
