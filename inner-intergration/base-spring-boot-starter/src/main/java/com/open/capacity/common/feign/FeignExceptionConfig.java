package com.open.capacity.common.feign;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.exception.RemoteCallException;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author owen
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
public class FeignExceptionConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new UserErrorDecoder();
    }
    /**
     * 重新实现feign的异常处理，捕捉restful接口返回的json格式的异常信息
     */
    public class UserErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            Exception exception = null;
            try {
                String bodyString = Util.toString(response.body().asReader());
                if (StringUtils.isNotEmpty(bodyString)) {
                    if (bodyString.contains(CommonConstant.STATUS)) {
                        FeignFailResult result = com.open.capacity.common.utils.JsonUtil.toObject(bodyString, FeignFailResult.class)  ;
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


    }
}