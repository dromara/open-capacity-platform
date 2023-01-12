package com.open.capacity.common.feign;

import lombok.Data;

@Data
public class FeignFailResult {
	 private int statusCodeValue;

	 private String msg;
}
