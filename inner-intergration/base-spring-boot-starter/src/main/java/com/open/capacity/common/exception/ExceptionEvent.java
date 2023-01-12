package com.open.capacity.common.exception;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;


/**
 * @author owen
 */
@Data
@Builder
@SuppressWarnings("serial")
public class ExceptionEvent implements Serializable {
	
	
	/**
	 * 报警主题
	 */
	private String title;

	/**
	 * 报警应用
	 */
	private String application;

	/**
	 * 报警接口
	 */
	private String apiPath;

	/**
	 * 报警跟踪号
	 */
	private String traceId;

	/**
	 * 报警提示
	 */
	private String message;
	
	/**
	 * 报警堆栈
	 */
	private String stackTrace;

}
