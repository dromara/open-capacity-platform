package com.open.capacity.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import com.open.capacity.common.constant.CodeEnum;

/**
 * @Author: [zhangzhiguang]
 * @Date: [2018-08-01 23:39]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.zzg]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEntity<T> implements Serializable {

	private Integer statusCodeValue;
	private String msg;
	private T datas;

	public static <T> ResponseEntity<T> succeed(String msg) {
		return of(CodeEnum.SUCCESS.getStatusCodeValue(), msg, null);
	}

	public static <T> ResponseEntity<T> succeed(T model, String msg) {
		return of(CodeEnum.SUCCESS.getStatusCodeValue(), msg, model);
	}

	public static <T> ResponseEntity<T> succeed(T model) {
		return of(CodeEnum.SUCCESS.getStatusCodeValue(), "", model);
	}

	public static <T> ResponseEntity<T> of(Integer statusCodeValue, String msg, T datas) {
		return new ResponseEntity<>(statusCodeValue, msg, datas);
	}

	public static <T> ResponseEntity<T> failed(String msg) {
		return of(CodeEnum.ERROR.getStatusCodeValue(), msg, null);
	}

	public static <T> ResponseEntity<T> failed(T model, String msg) {
		return of(CodeEnum.ERROR.getStatusCodeValue(), msg, model);
	}
	
	public static <T> ResponseEntity<T> succeedWith(T data, Integer status, String msg) {
		return new ResponseEntity<T>(status, msg , data);
	}

	public static <T> ResponseEntity<T> failedWith(T data, Integer status, String msg) {
        return new ResponseEntity<T>(status  , msg , data);
    }
	
}
