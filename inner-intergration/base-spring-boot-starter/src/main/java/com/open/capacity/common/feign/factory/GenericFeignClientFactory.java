package com.open.capacity.common.feign.factory;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.exception.RemoteCallException;
import com.open.capacity.common.feign.FeignFailResult;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenericFeignClientFactory<T> {
	private FeignClientBuilder feignClientBuilder;

	public GenericFeignClientFactory(ApplicationContext appContext) {
		this.feignClientBuilder = new FeignClientBuilder(appContext);
	}

	public T getFeignClient(final Class<T> type, String serviceId) {
		return this.feignClientBuilder.forType(type, serviceId).customize(item -> item.errorDecoder(new ErrorDecoder() {
			@Override
			public Exception decode(String methodKey, Response response) {
				Exception exception = null;
				try {
					String bodyString = Util.toString(response.body().asReader());
					if (StringUtils.isNotEmpty(bodyString)) {
						if (bodyString.contains(CommonConstant.STATUS)) {
							FeignFailResult result = com.open.capacity.common.utils.JsonUtil.toObject(bodyString,
									FeignFailResult.class);
							result.setStatusCodeValue(response.status());
							if (result.getStatusCodeValue() != HttpStatus.OK.value()) {
								exception = new RemoteCallException(result.getMsg());
							} else {
								exception = feign.FeignException.errorStatus(methodKey, response);
							}
						} else {
							exception = new RemoteCallException(bodyString);
						}
					} else {
						exception = feign.FeignException.errorStatus(methodKey, response);
					}
				} catch (IOException ex) {
					log.error(ex.getMessage(), ex);
				}
				return exception;
			}
		})).decode404(true).build();
	}
}
