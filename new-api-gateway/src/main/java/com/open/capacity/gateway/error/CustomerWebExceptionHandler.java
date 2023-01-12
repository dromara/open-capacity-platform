package com.open.capacity.gateway.error;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.gateway.config.RouteAliasConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义异常处理
 * @author someday
 * @date 2018/3/30
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@SuppressWarnings(value = { "all" })
public class CustomerWebExceptionHandler
		extends org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler {
	public CustomerWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
			ErrorProperties errorProperties, ApplicationContext applicationContext) {
		super(errorAttributes, resources, errorProperties, applicationContext);
	}

	@Override
	protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
		// 这里可以根据异常类型进行定制化逻辑
		int status = CommonConstant.FAIL;
		Throwable error = super.getError(request);
		Map<String, Object> errorAttributes = new HashMap<>(4);
		String message = error.getMessage();
		if (error instanceof NotFoundException) {
			NotFoundException e = (NotFoundException) error;
			HttpStatus httpStatus = e.getStatus();
			if (404 == httpStatus.value()) {
				message = "接口访问404,请查看路由信息是否正确";
				errorAttributes.put(CommonConstant.RESPONSE_STATUS, HttpStatus.NOT_FOUND.value());
			} else if (503 == httpStatus.value()) {
				Route route = request.exchange().getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
				String routeAlias = MapUtils.getString(RouteAliasConfig.getRouteAlias(), route.getId(), route.getId());
				message = routeAlias + " 服务不可用";
				errorAttributes.put(CommonConstant.RESPONSE_STATUS, HttpStatus.SERVICE_UNAVAILABLE.value());
			}
		}else if (error instanceof ResponseStatusException) {
			if(404 == ((ResponseStatusException) error).getStatus().value()){
				message = "接口访问404,请查看路由信息是否正确或者当前服务是否正常)！";
			}else {
				message = "其他异常！";
			}
			errorAttributes.put(CommonConstant.RESPONSE_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
		}else {
			errorAttributes.put(CommonConstant.RESPONSE_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		errorAttributes.put(CommonConstant.STATUS, status);
		errorAttributes.put("msg", message);
		log.error("path: {}", request.path());
		log.error("method: {}", request.methodName());
		return errorAttributes;
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
	}

	@Override
	protected int getHttpStatus(Map<String, Object> errorAttributes) {
		return super.getHttpStatus(errorAttributes);
	}
}
