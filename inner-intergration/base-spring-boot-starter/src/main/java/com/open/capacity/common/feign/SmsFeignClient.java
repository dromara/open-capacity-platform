package com.open.capacity.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.open.capacity.common.constant.ServiceNameConstants;
import com.open.capacity.common.feign.fallback.SmsFeignClientFallbackFactory;

/**
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51 
 * 调用短信中心发送短信
 */
@FeignClient(name = ServiceNameConstants.SMS_SERVICE, configuration = FeignExceptionConfig.class, fallbackFactory = SmsFeignClientFallbackFactory.class, decode404 = true)
public interface SmsFeignClient {

	@PostMapping(value = "/sms-internal/codes", params = { "phone"})
	public String sendSmsCode(@RequestParam("phone") String phone );

}
