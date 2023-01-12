package com.open.capacity.common.utils;

import lombok.experimental.UtilityClass;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.open.capacity.common.dto.ResponseEntity;

import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @author someday
 * @date 2018/5/5 code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@UtilityClass
public class WebfluxResponseUtil {
	/**
	 * webflux的response返回json对象
	 */
	public Mono<Void> responseWriter(ServerWebExchange exchange, int httpStatus, String msg) {
		ResponseEntity result = ResponseEntity.of(httpStatus, msg, null);
		return responseWrite(exchange, httpStatus, result);
	}

	public Mono<Void> responseFailed(ServerWebExchange exchange, String msg) {
		ResponseEntity result = ResponseEntity.failed(msg);
		return responseWrite(exchange, HttpStatus.INTERNAL_SERVER_ERROR.value(), result);
	}

	public Mono<Void> responseFailed(ServerWebExchange exchange, int httpStatus, String msg) {
		ResponseEntity result = ResponseEntity.failed(msg);
		return responseWrite(exchange, httpStatus, result);
	}

	public Mono<Void> responseWrite(ServerWebExchange exchange, int httpStatus, ResponseEntity result) {
		if (httpStatus == 0) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
		ServerHttpResponse response = exchange.getResponse();
		response.getHeaders().setAccessControlAllowCredentials(true);
		response.getHeaders().setAccessControlAllowOrigin("*");
		response.setStatusCode(HttpStatus.valueOf(httpStatus));
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		DataBufferFactory dataBufferFactory = response.bufferFactory();
		DataBuffer buffer = dataBufferFactory.wrap(JsonUtil.toJSONString(result).getBytes(Charset.defaultCharset()));
		return response.writeWith(Mono.just(buffer)).doOnError((error) -> {
			DataBufferUtils.release(buffer);
		});
	}

	public Mono<Void>  responseProblem(ServerWebExchange exchange,org.springframework.http.ResponseEntity<Problem> entity) {
		ServerHttpResponse response = exchange.getResponse();
		response.getHeaders().setAccessControlAllowCredentials(true);
		response.getHeaders().setAccessControlAllowOrigin("*");
		response.setStatusCode(HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		DataBufferFactory dataBufferFactory = response.bufferFactory();
		DataBuffer buffer = dataBufferFactory.wrap(JsonUtil.toJSONString(entity).getBytes(Charset.defaultCharset()));
		return response.writeWith(Mono.just(buffer)).doOnError((error) -> {
			DataBufferUtils.release(buffer);
		});
	}
}
